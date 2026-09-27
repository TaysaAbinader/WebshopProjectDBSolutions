# Webshop REST API & Relational Database Solutions

A production-grade RESTful e-commerce backend service and relational database solution built with **Spring Boot 3**, **Java 21**, **Spring Data JPA / Hibernate**, and **MariaDB / MySQL**.

The project delivers core online store workflows—catalog browsing, customer profile and delivery address management, supplier management, and transactional multi-table order placement—backed by advanced relational database features (SQL views, triggers, performance indexes).

---

## 📚 Documentation Map

To keep this README scannable and easy to digest, detailed specifications are modularized into dedicated technical guides:

* 📖 **[Complete API Reference Guide](docs/API_REFERENCE.md)**: Exhaustive documentation for every endpoint, query parameters, path variables, JSON request/response schemas, and error codes.
* 🗄️ **[Database Architecture & Solutions Guide](docs/DATABASE_SOLUTIONS.md)**: Deep dive into the underlying relational schema, SQL view definitions, database triggers, indexing strategy, audit columns, and ACID transaction security.

---

## 🚀 Key Implemented Features & Business Logic

* **Optimized Catalog Browsing**: Pre-joined catalog read models (`v_product_catalog`) supporting fast keyword search, category filtering, and supplier filtering.
* **Transactional Multi-Table Checkout**: Atomic order placement (`POST /v1/orders`) that verifies customer existence, validates delivery address ownership, checks real-time product stock, and locks unit prices.
* **Database-Enforced Stock Guards**: Database triggers abort checkout attempts exceeding available stock (`SQLSTATE '45000'`) and automatically decrement inventory on commit.
* **Aggregated Order Summaries**: Real-time order summaries (`v_order_details`) calculating dynamic order monetary sums, shipping addresses, and customer profiles.
* **Nested Address Sub-Resources**: Dedicated endpoints for managing multiple customer delivery addresses and supplier operational facilities.
* **Standardized Error Handling**: Uniform JSON error responses across all controllers (`code`, `message`, `fieldErrors`, `timestamp`).

---

## 🌐 REST API Endpoints at a Glance

All endpoints are prefixed with `/v1`. For detailed request/response schemas and examples, see the **[API Reference Guide](docs/API_REFERENCE.md)**.

| HTTP Method | Endpoint | Resource | Purpose |
| :--- | :--- | :--- | :--- |
| `GET` | `/v1/products` | Products | List catalog products (filters: `category_id`, `supplier_id`, `search`) |
| `GET` | `/v1/products/{id}` | Products | Retrieve a single product by ID via catalog view |
| `POST` | `/v1/products` | Products | Create a new catalog product |
| `PUT` | `/v1/products/{id}` | Products | Update an existing product |
| `DELETE` | `/v1/products/{id}` | Products | Delete a product |
| `GET` | `/v1/categories` | Categories | List all product categories |
| `GET` | `/v1/categories/{id}` | Categories | Get single category details |
| `POST` | `/v1/categories` | Categories | Create a new product category |
| `PUT` | `/v1/categories/{id}` | Categories | Update a category |
| `DELETE` | `/v1/categories/{id}` | Categories | Delete a category |
| `GET` | `/v1/customers` | Customers | List all registered customers |
| `GET` | `/v1/customers/{id}` | Customers | Get customer profile by ID |
| `POST` | `/v1/customers` | Customers | Register a new customer |
| `PUT` | `/v1/customers/{id}` | Customers | Update customer contact information |
| `DELETE` | `/v1/customers/{id}` | Customers | Delete a customer account |
| `GET` | `/v1/customers/{id}/addresses` | Addresses | List delivery addresses for a customer |
| `POST` | `/v1/customers/{id}/addresses` | Addresses | Add a new delivery address for a customer |
| `DELETE` | `/v1/customers/{id}/addresses/{addrId}` | Addresses | Remove a customer delivery address |
| `GET` | `/v1/orders` | Orders | List order summaries with calculated sums & addresses |
| `GET` | `/v1/orders/{id}` | Orders | Get detailed order summary by ID |
| `POST` | `/v1/orders` | Orders | Submit a purchase order with items (transactional checkout) |
| `PUT` | `/v1/orders/{id}/status` | Orders | Update order status (`pending`, `shipped`, `delivered`, etc.) |
| `GET` | `/v1/suppliers` | Suppliers | List all partner suppliers |
| `GET` | `/v1/suppliers/{id}` | Suppliers | Get supplier profile by ID |
| `POST` | `/v1/suppliers` | Suppliers | Register a new supplier |
| `PUT` | `/v1/suppliers/{id}` | Suppliers | Update supplier details |
| `DELETE` | `/v1/suppliers/{id}` | Suppliers | Delete a supplier |
| `GET` | `/v1/suppliers/{id}/addresses` | Addresses | List operational addresses for a supplier |
| `POST` | `/v1/suppliers/{id}/addresses` | Addresses | Add an operational address for a supplier |
| `DELETE` | `/v1/suppliers/{id}/addresses/{addrId}` | Addresses | Remove a supplier operational address |

---

## 🗄️ Relational Database Solutions at a Glance

The backend delegates key operational burdens to the database engine ([`database_features.sql`](database_features.sql)). For complete SQL code and design rationale, see the **[Database Solutions Guide](docs/DATABASE_SOLUTIONS.md)**.

* **Database Views**:
  * `v_product_catalog`: Denormalizes `Products`, `ProductCategories`, and `Suppliers` for rapid frontend catalog display.
  * `v_order_details`: Combines `Orders`, `Customers`, `CustomerAddresses`, and `OrderItems` while calculating order totals (`COALESCE(SUM(quantity * unit_price), 0.00)`).
* **Database Triggers**:
  * `trg_before_order_item_insert`: Prevents placing order items if stock is insufficient (`SIGNAL SQLSTATE '45000'`).
  * `trg_after_order_item_insert`: Automatically decrements `Products.stock_quantity` whenever an order item is inserted.
* **Performance Indexing**:
  * Text index on `Products(name)` for search speed.
  * Foreign key indexes on `category_id`, `supplier_id`, `customer_id`, `order_id`, and `product_id` to eliminate full table scans during joins and view execution.
* **Temporal Auditing & Security**:
  * Auto-updating audit timestamps (`created_at`, `updated_at`).
  * SQL injection immunity via Spring Data JPA prepared statement binding.
  * Cross-tenant address ownership validation during checkout.

---

## 🛠️ Running & Testing Locally

### Prerequisites
* **Java 21+** (`java -version`)
* **Maven 3.9+** (`mvn -version`)
* **MariaDB or MySQL 8.x** running on port 3306

### 1. Database Setup
```sql
CREATE DATABASE IF NOT EXISTS project CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
Apply the base schema followed by the database features script (views, triggers, indexes):
```bash
# 1. Create base tables and constraints
mysql -u root -p project < schema.sql

# 2. Create database views, triggers, and indexes
mysql -u root -p project < database_features.sql
```

### 2. Configure Database Connection
Environment variables or defaults in `src/main/resources/application.properties`:
* `PORT` (default: `8080`)
* `DB_HOST` (default: `localhost`)
* `DB_PORT` (default: `3306`)
* `DB_NAME` (default: `project`)
* `DB_USER` (default: `root`)
* `DB_PASSWORD` (default: `abc123def`)

### 3. Build & Run
```bash
# Execute automated test suite
mvn clean test

# Package into executable JAR
mvn clean package -DskipTests

# Run the Spring Boot application
mvn spring-boot:run
```
