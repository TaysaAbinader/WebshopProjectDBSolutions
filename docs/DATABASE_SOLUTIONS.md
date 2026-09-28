# Relational Database Solutions & Architecture

This document details the underlying relational database architecture, SQL optimizations, views, triggers, indexing strategies, and data integrity mechanisms implemented in the MariaDB / MySQL database for the Webshop project.

All database-level components referenced here are defined in [`schema.sql`](../schema.sql), [`database_features.sql`](../database_features.sql), and the incremental patch script [`apply_schema_updates.sql`](../apply_schema_updates.sql).

---

## Table of Contents
1. [Schema Overview & ER Relationships](#1-schema-overview--er-relationships)
2. [Database Views (Read Models)](#2-database-views-read-models)
   * [2.1 `v_product_catalog`](#21-v_product_catalog)
   * [2.2 `v_order_details`](#22-v_order_details)
3. [Database Triggers (Automated Invariants)](#3-database-triggers-automated-invariants)
   * [3.1 `trg_before_order_item_insert` (Stock Invariant Guard)](#31-trg_before_order_item_insert-stock-invariant-guard)
   * [3.2 `trg_after_order_item_insert` (Automatic Stock Decrement)](#32-trg_after_order_item_insert-automatic-stock-decrement)
4. [Indexing Strategy & Query Optimization](#4-indexing-strategy--query-optimization)
5. [Temporal Audit Columns](#5-temporal-audit-columns)
6. [Security, Transaction Isolation & Data Integrity](#6-security-transaction-isolation--data-integrity)
7. [Physical Naming & Case Sensitivity](#7-physical-naming--case-sensitivity)

---

## 1. Schema Overview & ER Relationships

The application database models a relational e-commerce system with 8 core entity tables:
* **`Products`**: Core merchandise catalog (name, description, unit price, stock quantity, category, supplier, temporal timestamps).
* **`ProductCategories`**: Classification hierarchy for products.
* **`Suppliers`**: Partner vendors providing goods.
* **`SupplierAddresses`**: Multi-address tracking for vendor facilities (street, city, state, postal code, country).
* **`Customers`**: Registered user profiles (first name, last name, unique email, phone, creation timestamp).
* **`CustomerAddresses`**: Saved shipping and delivery addresses for customers (street, city, state, postal code, country, `is_default` flag).
* **`Orders`**: Purchase order headers (customer, shipping address, lifecycle status, order date, delivery date, temporal timestamps).
* **`OrderItems`**: Purchase line items locking product ID, purchased quantity, and purchase unit price.

---

## 2. Database Views (Read Models)

SQL views abstract complex multi-table joins and calculations away from the application layer, allowing Spring Boot (via dedicated JPA `@Entity` views) to execute straightforward `SELECT * FROM view_name` queries with full query engine caching.

### 2.1 `v_product_catalog`

```sql
CREATE OR REPLACE VIEW v_product_catalog AS
SELECT
    p.id AS product_id,
    p.name AS product_name,
    p.description AS product_description,
    p.price,
    p.stock_quantity,
    c.id AS category_id,
    c.name AS category_name,
    s.id AS supplier_id,
    s.name AS supplier_name
FROM Products p
LEFT JOIN ProductCategories c ON p.category_id = c.id
LEFT JOIN Suppliers s ON p.supplier_id = s.id;
```

#### Why it was implemented:
* **Denormalization without Data Duplication**: The web storefront requires category and supplier names alongside product details for catalog display and filtering.
* **Simplifies Application Code**: JPA repository [`ProductCatalogViewRepository.java`](../src/main/java/com/webshop/repository/ProductCatalogViewRepository.java) maps directly to this view as a read-only entity (`@Immutable`), eliminating manual multi-table `JOIN` boilerplate from the application layer.
* **Query Optimizer Synergy**: Leverages indexes on `p.category_id` and `p.supplier_id` automatically when executing joins.

---

### 2.2 `v_order_details`

```sql
CREATE OR REPLACE VIEW v_order_details AS
SELECT
    o.id AS order_id,
    o.customer_id,
    CONCAT(cust.first_name, ' ', cust.last_name) AS customer_name,
    cust.email AS customer_email,
    o.status AS order_status,
    o.order_date,
    o.delivery_date,
    addr.street_address,
    addr.city,
    addr.postal_code,
    addr.country,
    COALESCE(SUM(oi.quantity * oi.unit_price), 0.00) AS total_amount
FROM Orders o
JOIN Customers cust ON o.customer_id = cust.id
LEFT JOIN CustomerAddresses addr ON o.shipping_address_id = addr.id
LEFT JOIN OrderItems oi ON o.id = oi.order_id
GROUP BY o.id, o.customer_id, cust.first_name, cust.last_name, cust.email,
         o.status, o.order_date, o.delivery_date, addr.street_address,
         addr.city, addr.postal_code, addr.country;
```

#### Why it was implemented:
* **Monetary Aggregate Calculation**: Computes `total_amount = SUM(quantity * unit_price)` dynamically on demand. If an order has no items yet, `COALESCE` safely defaults to `0.00`.
* **String Concatenation & Formatting**: Consolidates customer name and shipping address fields in the database engine, reducing serialization and DTO manipulation overhead in the application tier.
* **Unified Reporting**: Powers order search and history filtering (`GET /v1/orders?customer_id=...&status=...`).

---

## 3. Database Triggers (Automated Invariants)

Database triggers enforce critical inventory constraints directly within the database engine. This prevents inventory corruption even if multiple application nodes or direct database migrations insert rows concurrently.

### 3.1 `trg_before_order_item_insert` (Stock Invariant Guard)

```sql
DELIMITER //
CREATE TRIGGER trg_before_order_item_insert
BEFORE INSERT ON OrderItems
FOR EACH ROW
BEGIN
    DECLARE current_stock INT;

    SELECT stock_quantity INTO current_stock
    FROM Products
    WHERE id = NEW.product_id;

    IF current_stock < NEW.quantity THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Insufficient stock for requested product';
    END IF;
END;
//
DELIMITER ;
```

* **Trigger Type**: `BEFORE INSERT ON OrderItems`
* **Mechanism**: Intercepts the row before it is committed. Queries current stock in `Products`.
* **Fail-Safe**: If `current_stock < NEW.quantity`, it raises user-defined error `SQLSTATE '45000'`, aborting the SQL transaction immediately.

---

### 3.2 `trg_after_order_item_insert` (Automatic Stock Decrement)

```sql
DELIMITER //
CREATE TRIGGER trg_after_order_item_insert
AFTER INSERT ON OrderItems
FOR EACH ROW
BEGIN
    UPDATE Products
    SET stock_quantity = stock_quantity - NEW.quantity
    WHERE id = NEW.product_id;
END;
//
DELIMITER ;
```

* **Trigger Type**: `AFTER INSERT ON OrderItems`
* **Mechanism**: Automatically runs an `UPDATE` on `Products` immediately after each line item insertion.
* **Benefit**: Guarantees synchronized inventory numbers without requiring developers to manually write and sequence individual stock deduction statements.

---

## 4. Indexing Strategy & Query Optimization

Without indexing, queries on foreign keys and text filters degrade to $O(N)$ full table scans. The following B-Tree indexes were created:

```sql
-- 1. Full/Prefix text search on product catalog
CREATE INDEX idx_products_name ON Products(name);

-- 2. Foreign Key indexes for relational JOINs and constraint checks
CREATE INDEX idx_products_category ON Products(category_id);
CREATE INDEX idx_products_supplier ON Products(supplier_id);
CREATE INDEX idx_orders_customer ON Orders(customer_id);
CREATE INDEX idx_orderitems_order ON OrderItems(order_id);
CREATE INDEX idx_orderitems_product ON OrderItems(product_id);
```

### Optimization Breakdown:
1. **Catalog Search (`idx_products_name`)**: Accelerates `WHERE product_name LIKE ?` operations during user catalog search.
2. **Catalog View Joins (`idx_products_category`, `idx_products_supplier`)**: Transforms the joins in `v_product_catalog` into $O(\log N)$ index lookups instead of scanning every product row.
3. **Order Aggregation (`idx_orderitems_order`, `idx_orderitems_product`)**: When computing sums in `v_order_details` (`GROUP BY o.id` and joining `OrderItems`), indexing `order_id` ensures MariaDB quickly accesses only the line items belonging to that specific order.
4. **Customer Order History (`idx_orders_customer`)**: Ensures instant response times when fetching all orders for a specific customer (`/v1/orders?customer_id=...`).

---

## 5. Temporal Audit Columns

To track row lifecycles without requiring application-side timestamp boilerplate:

* **`Products`**:
  * `created_at DATETIME DEFAULT CURRENT_TIMESTAMP`
  * `updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`
* **`Orders`**:
  * `created_at DATETIME DEFAULT CURRENT_TIMESTAMP`
  * `updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`
* **`Customers`**:
  * `created_at DATETIME DEFAULT CURRENT_TIMESTAMP`

These columns ensure complete auditability directly managed by the MySQL / MariaDB storage engine.

---

## 6. Security, Transaction Isolation & Data Integrity

1. **SQL Injection Defense**:
   * Spring Boot uses Spring Data JPA / Hibernate which strictly binds parameters via JDBC `PreparedStatement` interfaces, completely neutralizing SQL injection risks.
2. **ACID Transaction Management**:
   * Order placement in [`OrderService.java`](../src/main/java/com/webshop/service/OrderService.java) is annotated with `@Transactional`.
   * If any line item fails validation or triggers an `InsufficientStockException` (or SQLSTATE 45000), the entire transaction rolls back, preventing orphaned order headers.
3. **Foreign Key Integrity & Cascades**:
   * Foreign keys ensure child records (e.g. `OrderItems`, `CustomerAddresses`, `SupplierAddresses`) cannot reference non-existent parents. Deleting a customer or supplier cascades to their addresses, while order headers are restricted from accidental deletion.
4. **Cross-Tenant Validation**:
   * During checkout, the service verifies that `shippingAddress.customerId == order.customerId`. If an address belonging to another customer is passed, the request is rejected with `400 Bad Request`.
5. **Unique Constraints**:
   * Customer emails are protected with a database-level `UNIQUE` constraint and pre-validated in `CustomerService` during registration and updates.

---

## 7. Physical Naming & Case Sensitivity

By default, Spring Boot automatically converts entity table names from PascalCase to snake_case (e.g. `ProductCategories` becomes `product_categories`).

On Linux/MariaDB environments where table names are case-sensitive by default, this creates `Table doesn't exist` errors. To prevent this, the application configures:

```properties
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
```

This guarantees Hibernate queries the exact table names annotated on the entities (`ProductCategories`, `SupplierAddresses`, `CustomerAddresses`, `OrderItems`, etc.), ensuring identical behavior across Windows, macOS, and Linux servers.
