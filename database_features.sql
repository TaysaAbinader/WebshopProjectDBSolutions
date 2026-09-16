-- ----------------------------------------------------------------------------
-- 1. VIEWS
-- ----------------------------------------------------------------------------

-- Joins Products with Categories and Suppliers for simplified retrieval
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

-- Aggregates customer orders, addresses, and total order amounts
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


-- ----------------------------------------------------------------------------
-- 2. TRIGGERS
-- ----------------------------------------------------------------------------

DELIMITER //

-- Prevents placing an order item if stock is insufficient
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

-- Decreases product inventory automatically when an item is ordered
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


-- ----------------------------------------------------------------------------
-- 3. INDEXING
-- ----------------------------------------------------------------------------

-- Search optimization on product names
CREATE INDEX idx_products_name ON Products(name);

-- Foreign Key indexing to speed up SQL JOIN performance
CREATE INDEX idx_products_category ON Products(category_id);
CREATE INDEX idx_products_supplier ON Products(supplier_id);
CREATE INDEX idx_orders_customer ON Orders(customer_id);
CREATE INDEX idx_orderitems_order ON OrderItems(order_id);
CREATE INDEX idx_orderitems_product ON OrderItems(product_id);


-- ----------------------------------------------------------------------------
-- 4. TEMPORAL FEATURES
-- ----------------------------------------------------------------------------

-- Adds automatic creation and modification timestamp tracking
ALTER TABLE Products
ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE Orders
ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
