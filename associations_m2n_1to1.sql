USE project;

-- NOTE: foreign key columns must have the same type as the referenced id columns.
-- In this database Products.id, Suppliers.id and Customers.id are INT(11).

-- N:M: a product can have many suppliers and a supplier many products
CREATE TABLE IF NOT EXISTS ProductSuppliers (
    product_id INT NOT NULL,
    supplier_id INT NOT NULL,
    PRIMARY KEY (product_id, supplier_id),
    CONSTRAINT fk_ps_product FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    CONSTRAINT fk_ps_supplier FOREIGN KEY (supplier_id) REFERENCES Suppliers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- copy the existing main supplier of every product into the join table
INSERT IGNORE INTO ProductSuppliers (product_id, supplier_id)
SELECT id, supplier_id FROM Products WHERE supplier_id IS NOT NULL;

-- 1:1: a customer has one profile (UNIQUE customer_id enforces "one")
CREATE TABLE IF NOT EXISTS CustomerProfiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL UNIQUE,
    birth_date DATE,
    newsletter_subscribed BOOLEAN NOT NULL DEFAULT FALSE,
    preferred_language VARCHAR(10),
    CONSTRAINT fk_profile_customer FOREIGN KEY (customer_id) REFERENCES Customers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
