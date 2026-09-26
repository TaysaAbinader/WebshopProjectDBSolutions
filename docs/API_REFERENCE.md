# Complete API Reference & Specification

This document provides a comprehensive, exhaustive reference for all REST API endpoints implemented in the Webshop backend service.

---

## Table of Contents
1. [Overview & Standards](#1-overview--standards)
2. [Standard Error Response Schema](#2-standard-error-response-schema)
3. [Products API (`/v1/products`)](#3-products-api-v1products)
4. [Categories API (`/v1/categories`)](#4-categories-api-v1categories)
5. [Customers API (`/v1/customers`)](#5-customers-api-v1customers)
6. [Customer Addresses API (`/v1/customers/{customerId}/addresses`)](#6-customer-addresses-api-v1customerscustomeridaddresses)
7. [Orders API (`/v1/orders`)](#7-orders-api-v1orders)
8. [Suppliers API (`/v1/suppliers`)](#8-suppliers-api-v1suppliers)
9. [Supplier Addresses API (`/v1/suppliers/{supplierId}/addresses`)](#9-supplier-addresses-api-v1supplierssupplieridaddresses)

---

## 1. Overview & Standards

* **Base URL**: `http://localhost:8080/v1`
* **Content Type**: `application/json` for all request bodies and responses.
* **Authentication**: Currently configured for public/internal service mesh; inputs are validated at the controller layer with Jakarta Bean Validation (`@Valid`).

---

## 2. Standard Error Response Schema

All non-2xx responses returned by the application conform to a uniform JSON error payload handled centrally by `GlobalExceptionHandler.java`:

```json
{
  "code": "VALIDATION_FAILED",
  "message": "Validation failed for one or more fields",
  "fieldErrors": {
    "email": "Email must be a valid email address",
    "firstName": "First name is required"
  },
  "timestamp": "2026-09-27T02:30:00"
}
```

### Handled Error Codes
| HTTP Status | Error Code | Description |
| :--- | :--- | :--- |
| `400 Bad Request` | `VALIDATION_FAILED` | One or more input fields failed validation constraints (`@NotBlank`, `@Positive`, etc.). |
| `400 Bad Request` | `INSUFFICIENT_STOCK` | Requested purchase quantity exceeds available product stock. |
| `400 Bad Request` | `INVALID_ARGUMENT` | Business rule failed (e.g. shipping address does not belong to the ordering customer). |
| `404 Not Found` | `NOT_FOUND` | Target entity (product, order, customer, etc.) does not exist. |
| `500 Internal Server Error`| `INTERNAL_SERVER_ERROR`| Uncaught system exception or unexpected database failure. |

---

## 3. Products API (`/v1/products`)

### 3.1 List Products
* **Method & Path**: `GET /v1/products`
* **Purpose**: Retrieves products from the underlying `v_product_catalog` database view. Supports filtering by category, supplier, and case-insensitive keyword search.
* **Request Query Parameters**:
  * `categoryId` (optional, integer): Filter products belonging to a specific category ID.
  * `supplierId` (optional, integer): Filter products supplied by a specific supplier ID.
  * `search` (optional, string): Search term matched against product name and product description.
* **Response**:
  * Status: `200 OK`
  * Body:
    ```json
    [
      {
        "productId": 1,
        "productName": "Wireless Ergonomic Mouse",
        "productDescription": "2.4GHz rechargeable wireless mouse with USB receiver",
        "price": 29.99,
        "stockQuantity": 150,
        "categoryId": 2,
        "categoryName": "Electronics",
        "supplierId": 1,
        "supplierName": "Tech Supplies Inc."
      }
    ]
    ```

### 3.2 Get Product By ID
* **Method & Path**: `GET /v1/products/{id}`
* **Purpose**: Retrieves single product details from the `v_product_catalog` view.
* **Path Parameters**: `id` (integer, required)
* **Response**:
  * Status: `200 OK`
  * Body: Single product object as shown above.
  * Errors: `404 Not Found` if product does not exist.

### 3.3 Create Product
* **Method & Path**: `POST /v1/products`
* **Purpose**: Adds a new product entity to the database.
* **Request Body**:
  ```json
  {
    "name": "Mechanical Keyboard",
    "description": "RGB backlit mechanical keyboard with blue switches",
    "price": 89.99,
    "stockQuantity": 75,
    "categoryId": 2,
    "supplierId": 1
  }
  ```
* **Validation Rules**:
  * `name`: `@NotBlank`, max 255 chars.
  * `price`: `@NotNull`, `@Positive`.
  * `stockQuantity`: `@NotNull`, `@PositiveOrZero`.
* **Response**:
  * Status: `201 Created`
  * Body:
    ```json
    {
      "id": 15,
      "name": "Mechanical Keyboard",
      "description": "RGB backlit mechanical keyboard with blue switches",
      "price": 89.99,
      "stockQuantity": 75,
      "categoryId": 2,
      "supplierId": 1,
      "createdAt": "2026-09-27T02:30:00",
      "updatedAt": "2026-09-27T02:30:00"
    }
    ```

### 3.4 Update Product
* **Method & Path**: `PUT /v1/products/{id}`
* **Purpose**: Updates an existing product's attributes and stock level.
* **Path Parameters**: `id` (integer, required)
* **Request Body**: Same schema as Create Product.
* **Response**:
  * Status: `200 OK`
  * Body: Updated `Product` entity.

### 3.5 Delete Product
* **Method & Path**: `DELETE /v1/products/{id}`
* **Purpose**: Removes a product from the database.
* **Path Parameters**: `id` (integer, required)
* **Response**:
  * Status: `204 No Content`
  * Errors: `404 Not Found` if product does not exist.

---

## 4. Categories API (`/v1/categories`)

### 4.1 List Categories
* **Method & Path**: `GET /v1/categories`
* **Purpose**: Returns all product categories.
* **Response**:
  * Status: `200 OK`
  * Body:
    ```json
    [
      {
        "id": 1,
        "name": "Computers & Peripherals",
        "description": "Desktops, laptops, monitors, and accessories"
      }
    ]
    ```

### 4.2 Get Category By ID
* **Method & Path**: `GET /v1/categories/{id}`
* **Path Parameters**: `id` (integer, required)
* **Response**:
  * Status: `200 OK`
  * Body: Single Category object.

### 4.3 Create Category
* **Method & Path**: `POST /v1/categories`
* **Request Body**:
  ```json
  {
    "name": "Audio",
    "description": "Headphones, speakers, and microphones"
  }
  ```
* **Validation Rules**: `name` must not be blank.
* **Response**:
  * Status: `201 Created`
  * Body: Created `Category` object with generated `id`.

### 4.4 Update Category
* **Method & Path**: `PUT /v1/categories/{id}`
* **Path Parameters**: `id` (integer, required)
* **Request Body**: Same as Create Category.
* **Response**:
  * Status: `200 OK`
  * Body: Updated `Category` object.

### 4.5 Delete Category
* **Method & Path**: `DELETE /v1/categories/{id}`
* **Path Parameters**: `id` (integer, required)
* **Response**:
  * Status: `204 No Content`

---

## 5. Customers API (`/v1/customers`)

### 5.1 List Customers
* **Method & Path**: `GET /v1/customers`
* **Purpose**: Retrieves all registered customer accounts.
* **Response**:
  * Status: `200 OK`
  * Body:
    ```json
    [
      {
        "id": 1,
        "firstName": "Jane",
        "lastName": "Doe",
        "email": "jane.doe@example.com",
        "phone": "+1-555-0199"
      }
    ]
    ```

### 5.2 Get Customer By ID
* **Method & Path**: `GET /v1/customers/{id}`
* **Path Parameters**: `id` (integer, required)
* **Response**: `200 OK` with customer object, or `404 Not Found`.

### 5.3 Register Customer
* **Method & Path**: `POST /v1/customers`
* **Purpose**: Registers a new customer account.
* **Request Body**:
  ```json
  {
    "firstName": "Jane",
    "lastName": "Doe",
    "email": "jane.doe@example.com",
    "phone": "+1-555-0199"
  }
  ```
* **Validation Rules**:
  * `firstName`, `lastName`: `@NotBlank`
  * `email`: `@NotBlank`, `@Email`
* **Response**: `201 Created` with created customer object.

### 5.4 Update Customer
* **Method & Path**: `PUT /v1/customers/{id}`
* **Path Parameters**: `id` (integer, required)
* **Request Body**: Same as Register Customer.
* **Response**: `200 OK` with updated customer object.

### 5.5 Delete Customer
* **Method & Path**: `DELETE /v1/customers/{id}`
* **Path Parameters**: `id` (integer, required)
* **Response**: `204 No Content`.

---

## 6. Customer Addresses API (`/v1/customers/{customerId}/addresses`)

### 6.1 List Customer Addresses
* **Method & Path**: `GET /v1/customers/{customerId}/addresses`
* **Purpose**: Retrieves all saved shipping/billing addresses for a customer.
* **Path Parameters**: `customerId` (integer, required)
* **Response**:
  * Status: `200 OK`
  * Body:
    ```json
    [
      {
        "id": 10,
        "customerId": 1,
        "streetAddress": "123 Main St, Apt 4B",
        "city": "Boston",
        "postalCode": "02118",
        "country": "USA"
      }
    ]
    ```

### 6.2 Add Customer Address
* **Method & Path**: `POST /v1/customers/{customerId}/addresses`
* **Purpose**: Adds an address to the specified customer profile.
* **Path Parameters**: `customerId` (integer, required)
* **Request Body**:
  ```json
  {
    "streetAddress": "456 Elm Ave",
    "city": "Cambridge",
    "postalCode": "02139",
    "country": "USA"
  }
  ```
* **Validation Rules**: `streetAddress`, `city`, `postalCode`, `country` must not be blank.
* **Response**: `201 Created` with created `CustomerAddress` object.

### 6.3 Delete Customer Address
* **Method & Path**: `DELETE /v1/customers/{customerId}/addresses/{addressId}`
* **Path Parameters**: `customerId` (integer, required), `addressId` (integer, required)
* **Response**: `204 No Content`.

---

## 7. Orders API (`/v1/orders`)

### 7.1 List Orders
* **Method & Path**: `GET /v1/orders`
* **Purpose**: Retrieves aggregated order summaries using the `v_order_details` view.
* **Request Query Parameters**:
  * `customerId` (optional, integer): Filter orders belonging to a specific customer.
  * `status` (optional, string): Filter by order status (`pending`, `shipped`, `delivered`, `cancelled`).
* **Response**:
  * Status: `200 OK`
  * Body:
    ```json
    [
      {
        "orderId": 101,
        "customerId": 1,
        "customerName": "Jane Doe",
        "customerEmail": "jane.doe@example.com",
        "orderStatus": "pending",
        "orderDate": "2026-09-27T01:15:00",
        "deliveryDate": null,
        "streetAddress": "123 Main St, Apt 4B",
        "city": "Boston",
        "postalCode": "02118",
        "country": "USA",
        "totalAmount": 149.97
      }
    ]
    ```

### 7.2 Get Order By ID
* **Method & Path**: `GET /v1/orders/{id}`
* **Purpose**: Returns single order details from the `v_order_details` view.
* **Path Parameters**: `id` (integer, required)
* **Response**: `200 OK` with order summary object, or `404 Not Found`.

### 7.3 Submit Order (Checkout)
* **Method & Path**: `POST /v1/orders`
* **Purpose**: Atomically creates an order header and line items, verifies stock, checks customer & address ownership, computes totals, and triggers inventory deduction.
* **Request Body**:
  ```json
  {
    "customerId": 1,
    "shippingAddressId": 10,
    "items": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 3,
        "quantity": 1
      }
    ]
  }
  ```
* **Validation & Business Logic Enforced**:
  1. Customer must exist (`404 Not Found` if missing).
  2. If `shippingAddressId` is provided, it must belong to `customerId` (`400 Bad Request` with `INVALID_ARGUMENT` if owned by another customer).
  3. Every item must have sufficient inventory (`400 Bad Request` with `INSUFFICIENT_STOCK` if requested > available).
  4. Wrapped in a Spring database transaction (`@Transactional`).
* **Response**:
  * Status: `201 Created`
  * Body:
    ```json
    {
      "id": 102,
      "customerId": 1,
      "shippingAddressId": 10,
      "status": "pending",
      "orderDate": "2026-09-27T02:30:00",
      "deliveryDate": null,
      "totalAmount": 119.97,
      "items": [
        {
          "id": 201,
          "orderId": 102,
          "productId": 1,
          "quantity": 2,
          "unitPrice": 29.99
        },
        {
          "id": 202,
          "orderId": 102,
          "productId": 3,
          "quantity": 1,
          "unitPrice": 59.99
        }
      ]
    }
    ```

### 7.4 Update Order Status
* **Method & Path**: `PATCH /v1/orders/{id}/status`
* **Purpose**: Progresses the order lifecycle status (`shipped`, `delivered`, `cancelled`).
* **Path Parameters**: `id` (integer, required)
* **Request Body**:
  ```json
  {
    "status": "shipped"
  }
  ```
* **Validation Rules**: `status` must not be blank.
* **Response**: `200 OK` with updated order object and calculated total amount.

---

## 8. Suppliers API (`/v1/suppliers`)

### 8.1 List Suppliers
* **Method & Path**: `GET /v1/suppliers`
* **Response**:
  * Status: `200 OK`
  * Body:
    ```json
    [
      {
        "id": 1,
        "name": "Tech Supplies Inc.",
        "contactName": "Alice Johnson",
        "contactEmail": "alice@techsupplies.com",
        "phone": "+1-555-8822"
      }
    ]
    ```

### 8.2 Get Supplier By ID
* **Method & Path**: `GET /v1/suppliers/{id}`
* **Response**: `200 OK` with supplier object, or `404 Not Found`.

### 8.3 Create Supplier
* **Method & Path**: `POST /v1/suppliers`
* **Request Body**:
  ```json
  {
    "name": "Nordic Electronics",
    "contactName": "Lars Svensson",
    "contactEmail": "lars@nordicelec.se",
    "phone": "+46-8-123456"
  }
  ```
* **Validation Rules**: `name` must not be blank.
* **Response**: `201 Created` with created supplier entity.

### 8.4 Update Supplier
* **Method & Path**: `PUT /v1/suppliers/{id}`
* **Response**: `200 OK` with updated supplier entity.

### 8.5 Delete Supplier
* **Method & Path**: `DELETE /v1/suppliers/{id}`
* **Response**: `204 No Content`.

---

## 9. Supplier Addresses API (`/v1/suppliers/{supplierId}/addresses`)

### 9.1 List Supplier Addresses
* **Method & Path**: `GET /v1/suppliers/{supplierId}/addresses`
* **Path Parameters**: `supplierId` (integer, required)
* **Response**: `200 OK` with array of supplier addresses.

### 9.2 Add Supplier Address
* **Method & Path**: `POST /v1/suppliers/{supplierId}/addresses`
* **Request Body**:
  ```json
  {
    "streetAddress": "Hamngatan 12",
    "city": "Stockholm",
    "postalCode": "11147",
    "country": "Sweden"
  }
  ```
* **Response**: `201 Created` with created supplier address.

### 9.3 Delete Supplier Address
* **Method & Path**: `DELETE /v1/suppliers/{supplierId}/addresses/{addressId}`
* **Response**: `204 No Content`.
