# se4801-assignment1--ATE-8400-14-
# ShopWave — Product Management REST API

**Name:** Kassahun Belachew  
**Student Number:** ATE/8400/14

---

## Project Overview

ShopWave is a Spring Boot REST API for managing products, categories, and orders.
It demonstrates a layered architecture with JPA entities, service layer, REST controllers,
validation, exception handling, and a full test suite including unit, integration, and
Testcontainers-based tests.

---

## Tech Stack

- Java 21
- Spring Boot 3.5.12
- Spring Data JPA
- H2 (runtime / testing)
- PostgreSQL 
- Lombok
- Jakarta Validation
- JUnit 5 + Mockito
- Testcontainers

---

## Project Structure
```
src/
├── main/java/com/shopwave/
│   ├── controller/        # REST controllers
│   ├── dto/               # Request and response DTOs
│   ├── exception/         # Custom exceptions and global handler
│   ├── mapper/            # Entity ↔ DTO conversion
│   ├── model/             # JPA entities
│   ├── repository/        # Spring Data repositories
│   └── service/           # Business logic
└── test/java/com/shopwave/
    ├── controller/        # ProductControllerTest
    ├── repository/        # ProductRepositoryTest, ProductRepositoryContainerTest
    └── service/           # ProductServiceTest
```

---

## How to Build

Make sure you have **Java 21** and **Maven** installed, then run:
```bash
mvn clean install -DskipTests
```

---

## How to Run
```bash
mvn spring-boot:run
```

The application starts on **http://localhost:8080**

To verify it is running, open:
```
http://localhost:8080/actuator/health
```

To explore the H2 in-memory database console:
```
http://localhost:8080/h2-console
```

Use these connection settings:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: _(leave blank)_

---

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/products | Get all products (paginated) |
| GET | /api/products/{id} | Get product by ID |
| POST | /api/products | Create a new product |
| GET | /api/products/search | Search by keyword and/or maxPrice |
| PATCH | /api/products/{id}/stock | Update product stock |
| POST | /api/categories | Create a new category |

### Example — Create a category
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Electronics", "description": "Electronic devices"}'
```

### Example — Create a product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Keyboard",
    "description": "Compact keyboard",
    "price": 49.99,
    "stock": 20,
    "categoryId": 1
  }'
```

### Example — Update stock
```bash
curl -X PATCH http://localhost:8080/api/products/1/stock \
  -H "Content-Type: application/json" \
  -d '{"delta": 5}'
```

---

## How to Run Tests

### Run all tests
```bash
mvn test
```

### Run a specific test class
```bash
mvn test -Dtest=ProductRepositoryTest
mvn test -Dtest=ProductServiceTest
mvn test -Dtest=ProductControllerTest
```

### Run the Testcontainers bonus test (requires Docker)

Make sure **Docker Desktop** is running, then:
```bash
mvn test -Dtest=ProductRepositoryContainerTest
```

This spins up a real **PostgreSQL 16** container and runs the repository tests against it.
You should see the container starting in your console output.

---

## Test Summary

| Test Class | Type | Description |
|------------|------|-------------|
| ProductRepositoryTest | @DataJpaTest + H2 | Tests findByNameContainingIgnoreCase |
| ProductServiceTest | Mockito unit test | Tests createProduct happy/error path, updateStock |
| ProductControllerTest | @WebMvcTest | Tests GET /api/products returns 200, GET /api/products/999 returns 404 with error JSON |
| ProductRepositoryContainerTest | Testcontainers + PostgreSQL | Bonus — same repository tests against real DB |

---


