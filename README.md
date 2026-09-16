# Webshop Project REST API & Relational Database Service

A RESTful backend service for a webshop built with Node.js, Express, and MariaDB. The service provides core CRUD interfaces for products, categories, customers, addresses, orders, and suppliers, backed by a fully validated OpenAPI 3.0 specification and optimized with relational database features.

---

## 1. Project Overview & Architecture

* Runtime & Framework: Node.js, Express.js
* Database Management System: MariaDB / MySQL
* API Specification: OpenAPI 3.0.3 (`openapi.yaml`)
* Design Pattern: Layered Architecture (Routes, Controllers, Database Connection Pool)

The application handles core e-commerce workflows, including catalog browsing, category filtering, customer profile and delivery address management, and transactional multi-table order submission with automatic inventory tracking.

---

## 2. API Specification (`openapi.yaml`)

The API design follows a design-first approach adhering to RESTful principles and is documented in `openapi.yaml`:

* Products (`/v1/products`): Query catalog items, filter by category or supplier, search by keyword, create, update, or remove products.
* Product Categories (`/v1/categories`):** Manage product categories.
* Customers (`/v1/customers`): Register customer accounts and manage nested customer delivery addresses (`/v1/customers/{customerId}/addresses`).
* Orders (`/v1/orders`): Submit purchase orders with items payloads, view order summaries, and update order statuses.
* Suppliers (`/v1/suppliers`): Manage business partner profiles and operational addresses.

### Testing & Specification Tooling
To preview or lint the spec locally using VS Code:
* Preview interactive documentation using the Swagger Viewer or Redocly extension.
* Mock API endpoints locally without running a database using Prism:
  ```bash
  npx @stoplight/prism-cli mock openapi.yaml
