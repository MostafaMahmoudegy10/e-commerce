# StyleHub Backend

StyleHub is an AI-driven fashion social commerce and multi-vendor e-commerce backend built with Spring Boot. The platform combines online shopping, brand dashboards, customer product browsing, model profiles, and brand-model collaboration workflows in one system.

This repository contains the backend API for the StyleHub graduation project. It focuses on secure REST APIs, role-based access, catalog and inventory management, customer checkout, payment handling, ratings, notifications, and event-driven communication using RabbitMQ.

## Project Overview

StyleHub is designed for a fashion marketplace where brands can manage their stores and products, customers can browse and purchase fashion items, and models can create profiles to collaborate with brands.

The backend supports the main flows needed for a real social commerce platform:

- Brand owners can manage products, colors, variants, stock, categories, orders, dashboard insights, and model collaborations.
- Customers can browse brand storefronts, search products, view details, manage cart and wishlist, checkout, pay, and rate products.
- Models can create profiles, receive collaboration requests, accept or reject requests, submit deliverables, and view reviews.
- The system publishes business events through RabbitMQ for notifications, emails, stock alerts, order lifecycle updates, and collaboration events.

## Key Features

- JWT-based authentication and role-based authorization
- Brand dashboard APIs for products, categories, orders, calendar, notifications, and analytics
- Customer storefront APIs for brands, categories, products, cart, wishlist, checkout, payments, and ratings
- Product catalog with colors, variants, stock management, images, and full-text search
- Model profile creation and searchable model discovery for brand collaborations
- Brand-model collaboration workflow with requests, agreements, submissions, payments, and reviews
- Order and payment lifecycle management
- RabbitMQ event publishing and listeners for asynchronous business workflows
- Flyway database migrations for PostgreSQL and H2 test profiles
- Swagger/OpenAPI documentation with grouped API sections and JWT bearer authorization

## Tech Stack

- Java 17
- Spring Boot 4
- Spring Web MVC
- Spring Security
- Spring OAuth2 Resource Server
- Spring Data JPA
- PostgreSQL
- H2 Database for tests
- Flyway
- RabbitMQ
- Cloudinary
- Lombok
- Springdoc OpenAPI / Swagger UI
- Maven

## Main API Modules

| Module | Description |
| --- | --- |
| Public APIs | OTP, email review links, and public endpoints |
| Customer APIs | Brand browsing, categories, products, cart, wishlist, checkout, payments, and ratings |
| Brand Dashboard APIs | Brand product management, categories, orders, dashboard home, calendar, notifications, and model discovery |
| Model APIs | Model profile, collaboration requests, agreements, submissions, payments, and reviews |
| Orders & Payments APIs | Customer checkout, customer payments, brand orders, and model agreement payments |

## Swagger Documentation

The deployed backend base URL is:

```text
https://ecommerce-app-e6303c36e118.herokuapp.com/api/v1
```

Swagger UI is available at:

```text
https://ecommerce-app-e6303c36e118.herokuapp.com/swagger-ui/index.html
```

OpenAPI JSON is available at:

```text
https://ecommerce-app-e6303c36e118.herokuapp.com/v3/api-docs
```

Grouped OpenAPI docs:

```text
https://ecommerce-app-e6303c36e118.herokuapp.com/v3/api-docs/public-apis
https://ecommerce-app-e6303c36e118.herokuapp.com/v3/api-docs/customer-apis
https://ecommerce-app-e6303c36e118.herokuapp.com/v3/api-docs/brand-dashboard-apis
https://ecommerce-app-e6303c36e118.herokuapp.com/v3/api-docs/model-apis
https://ecommerce-app-e6303c36e118.herokuapp.com/v3/api-docs/orders-payments-apis
```

Swagger includes JWT Bearer authentication. Use the `Authorize` button and provide a valid JWT token to test protected endpoints.

## Getting Started

### Prerequisites

Make sure the following tools are installed:

- Java 17
- Maven or the included Maven Wrapper
- PostgreSQL
- RabbitMQ

### Clone the Repository

```bash
git clone https://github.com/your-username/stylehub-backend.git
cd stylehub-backend
```

### Configure Environment Variables

The application reads several values from environment variables. Configure them before running the app:

```env
JWT_SECRET=your-jwt-secret
JWT_ISSUER=auth-service
JWT_AUDIENCE=ecommerce-service

CLOUDINARY_CLOUD_NAME=your-cloudinary-cloud-name
CLOUDINARY_API_KEY=your-cloudinary-api-key
CLOUDINARY_API_SECRET=your-cloudinary-api-secret

MAIL_USERNAME=your-mail-username
MAIL_PASSWORD=your-mail-password

API_BASE_URL=https://ecommerce-app-e6303c36e118.herokuapp.com
DASHBOARD_BASE_URL=http://localhost:3000
STOREFRONT_BASE_URL=http://localhost:3000
EMAIL_REVIEW_TOKEN_SECRET=change-this-secret
EMAIL_REVIEW_TOKEN_VALIDITY_DAYS=30
```

For local development, the default PostgreSQL configuration is:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/e_commerce_db
spring.datasource.username=postgres
spring.datasource.password=password
```

You can update these values in `src/main/resources/application-local.properties` or provide your own environment-specific configuration.

### Run the Application

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

If `JAVA_HOME` is not configured correctly on Windows, set it before running Maven:

```powershell
$env:JAVA_HOME='C:\java-17\java'
.\mvnw.cmd spring-boot:run
```

## Running Tests

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

## Database Migrations

Flyway is used to manage database schema changes.

Migration files are organized under:

```text
src/main/resources/db/migration/common
src/main/resources/db/migration/postgres
src/main/resources/db/migration/h2
```

The PostgreSQL profile uses common migrations plus PostgreSQL-specific migrations. The test profile uses common migrations plus H2-specific migrations.

## Security

The backend uses stateless JWT authentication through Spring Security and OAuth2 Resource Server support.

Main access rules:

- Public endpoints under `/api/v1/public/**` are accessible without authentication.
- Brand dashboard endpoints under `/api/v1/brands/**` require the `BRAND_OWNER` role.
- Customer endpoints require authenticated customer access depending on the controller.
- Model endpoints are protected and depend on the authenticated user's role and profile ownership.

## Event-Driven Architecture

RabbitMQ is used to handle asynchronous events across the system, including:

- Customer and brand creation events
- Order lifecycle events
- Low-stock and insufficient-stock alerts
- Product review email requests
- Model collaboration request events
- Model agreement submission, approval, revision, and payment events
- Dashboard notifications

This keeps business workflows decoupled and easier to extend.

## Project Structure

```text
src/main/java/org/stylehub/backend/e_commerce
|-- brand
|-- cart
|-- customer
|-- favourite
|-- model
|-- modules
|   |-- catalog
|   `-- dashboard
|-- order
|-- platform
|-- product
`-- user
```

## Graduation Project Scope

StyleHub demonstrates a complete backend architecture for a modern fashion social commerce platform. The project highlights:

- Real-world e-commerce workflows
- Multi-role access control
- Brand dashboard operations
- Customer shopping experience
- Model collaboration business flow
- API documentation and developer usability
- Event-driven backend communication
- Database migration discipline

## Future Enhancements

- Add AI recommendation service integration
- Add advanced social media feed features
- Add admin dashboard APIs
- Add payment gateway integration with real provider callbacks
- Add more analytics endpoints for brand performance
- Add automated API contract tests from OpenAPI specs
