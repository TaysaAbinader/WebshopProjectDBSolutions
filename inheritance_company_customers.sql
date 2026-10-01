USE project;

-- Inheritance (JOINED): CompanyCustomers extends Customers. The shared columns stay in Customers,
-- the company specific ones go here. id is both primary key and foreign key to Customers.id (INT, like Customers.id).
CREATE TABLE IF NOT EXISTS CompanyCustomers (
    id INT NOT NULL PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    vat_number VARCHAR(50),
    CONSTRAINT fk_company_customer FOREIGN KEY (id) REFERENCES Customers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
