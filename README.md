# Webshop Project REST API & Relational Database Service

A RESTful backend service for a webshop built with **Spring Boot 3**, **Java 21**, **Spring Data JPA / Hibernate**, and **MariaDB / MySQL**. The service provides core CRUD interfaces for products, categories, customers, addresses, orders, and suppliers, backed by a fully validated OpenAPI 3.0 specification and optimized with relational database features (views, triggers, and indices).

---

## 1. Project Overview & Architecture

* **Runtime & Framework**: Java 21, Spring Boot 3.3.4 (Spring Web, Spring Data JPA, Spring Validation)
* **Database Management System**: MariaDB / MySQL
* **Database Drivers**: MariaDB Java Client (`org.mariadb.jdbc`), MySQL Connector/J
* **API Specification**: OpenAPI 3.0.3 (`openapi.yaml`) & SpringDoc OpenAPI (`/swagger-ui.html`)
* **Design Pattern**: Layered Enterprise Architecture (Controllers, Services, Repositories, Entities/DTOs)

The application handles core e-commerce workflows, including catalog browsing with database views, category filtering, customer profile and delivery address management, and transactional multi-table order submission with automatic inventory tracking.

---

## 2. API Specification & Documentation

The API design follows a design-first approach adhering to RESTful principles and is documented in `openapi.yaml`:

* **Products** (`/v1/products`): Query catalog items via `v_product_catalog`, filter by category or supplier, search by keyword, create, update, or remove products.
* **Product Categories** (`/v1/categories`): Manage product categories.
* **Customers** (`/v1/customers`): Register customer accounts and manage nested customer delivery addresses (`/v1/customers/{customerId}/addresses`).
* **Orders** (`/v1/orders`): Submit purchase orders with items payloads, view order summaries via `v_order_details`, and update order statuses.
* **Suppliers** (`/v1/suppliers`): Manage business partner profiles and operational addresses (`/v1/suppliers/{supplierId}/addresses`).

### Interactive Swagger UI & OpenAPI Docs
When running the Spring Boot application locally:
* **Interactive Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **OpenAPI 3 JSON Specification**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 3. Running & Testing Locally

### Prerequisites
* Java 21+ (`java -version`)
* Maven 3.9+ (`mvn -version`)
* Running MariaDB or MySQL database instance with schemas and `database_features.sql` applied.

### Database Configuration
Environment variables can be configured or set in `src/main/resources/application.properties`:
* `PORT` (default: 8080)
* `DB_HOST` (default: localhost)
* `DB_PORT` (default: 3306)
* `DB_NAME` (default: project)
* `DB_USER` (default: root)
* `DB_PASSWORD` (default: abc123def)

### Build & Run
Run with Maven:
```bash
# Run automated tests
mvn clean test

# Package into an executable JAR
mvn clean package -DskipTests

# Start the Spring Boot application
mvn spring-boot:run
```
