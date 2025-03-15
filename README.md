# Trading System Application

## Overview
This is a Spring Boot-based trading system application that provides functionality for managing assets and orders with role-based access control.

### Project Structure
```
├── api
│   ├── controller
│   │   ├── AssetController.java
│   │   ├── AuthController.java
│   │   └── OrderController.java
│   ├── dto
│   │   ├── AssetDto.java
│   │   └── OrderDto.java
│   ├── request
│   │   ├── CreateOrderRequest.java
│   │   ├── LoginRequest.java
│   │   └── UnblockRequest.java
│   └── response
│       ├── AssetsResponse.java
│       ├── LoginResponse.java
│       └── OrdersResponse.java
├── constant
│   └── Constants.java
├── domain
│   ├── model
│   │   ├── Asset.java
│   │   ├── CustomUserDetails.java
│   │   ├── Order.java
│   │   ├── User.java
│   │   └── UserRole.java
│   └── repository
│       ├── AssetRepository.java
│       ├── OrderRepository.java
│       └── UserRepository.java
├── enums
│   ├── OrderSide.java
│   └── OrderStatus.java
├── exception
│   ├── AssetNotFoundException.java
│   ├── BaseException.java
│   ├── ErrorResponse.java
│   ├── GlobalExceptionHandler.java
│   ├── InsufficientAssetException.java
│   ├── InvalidBigDecimalArgumentException.java
│   ├── InvalidSymbolException.java
│   ├── OrderNotFoundException.java
│   └── OrderStatusException.java
├── infrastructure
│   └── security
│       ├── AuthenticationFailureListener.java
│       ├── JwtAuthFilter.java
│       └── SecurityConfig.java
├── service
│   ├── auth
│   │   ├── AuthService.java
│   │   ├── CustomUserDetailsService.java
│   │   └── JwtService.java
│   └── trade
│       ├── factory
│       │   └── OrderFactory.java
│       ├── strategy
│       │   ├── BuyOrderStrategy.java
│       │   ├── OrderExecutionStrategy.java
│       │   └── SellOrderStrategy.java
│       ├── validation
│       │   ├── AssetValidationHandler.java
│       │   ├── OrderValidator.java
│       │   ├── SymbolValidationHandler.java
│       │   └── ValidationHandler.java
│       ├── AssetService.java
│       └── OrderService.java
├── util
│   └── BigDecimalUtil.java
└── CaseApplication.java
```

## Features

### Authentication and Security
- JWT-based authentication mechanism implemented with Spring Security
- Role-based authorization (ADMIN and USER roles)
- Account blocking mechanism after failed login attempts to prevent brute force attacks
- Admin-only unblock functionality

### API Endpoints

#### Authentication Controller (`/auth`)
- `POST /auth/login` - Authenticate users and generate JWT token
- `PATCH /auth/unblock` - Unblock users (ADMIN only)

#### Asset Controller (`/asset`)
- `GET /asset?customerId={id}` - List assets for a customer (ADMIN or asset owner)

#### Order Controller (`/order`)
- `POST /order` - Create new order (ADMIN or order owner)
- `GET /order?customerId={id}&startDate={date}&endDate={date}` - List orders (ADMIN or order owner)
- `DELETE /order/{orderId}` - Cancel order (ADMIN or order owner)
- `PATCH /order/{orderId}/match` - Match order (ADMIN only)

### Design Patterns
The trade package implements several design patterns for maintainable and extensible code:

1. Factory Pattern
   - `OrderFactory`: Creates Order objects with standardized initialization

2. Strategy Pattern
   - `OrderExecutionStrategy`: Defines interface for order execution
   - `BuyOrderStrategy` and `SellOrderStrategy`: Implement different order execution logic

3. Chain of Responsibility Pattern
   - `ValidationHandler`: Defines interface for validation chain
   - `AssetValidationHandler` and `SymbolValidationHandler`: Implement specific validation rules

### Testing
- Comprehensive unit tests for business logic
- Integration tests using Testcontainers for database testing
- Mock MVC tests for API endpoints

### Database
- H2 in-memory database for development
- Pre-populated data using `data.sql` for testing and development

### Monitoring and Observability
- Spring Actuator integration for application metrics
- Structured logging to files for integration with log aggregation tools (Fluentbit/Logstash)

### Containerization
- Dockerized application with multi-stage build
- Ready for deployment in containerized environments

### Development Tools
- Swagger/OpenAPI for API documentation
- Spotless for code formatting

## Getting Started

### Prerequisites
- Java 21
- Docker
- Maven

### Running the Application
1. Run tests:
   ```bash
   mvn clean test
   ```
2. Build the application:
   ```bash
   mvn clean package
   ```
3. Run the application:
   ```bash
   java -jar target/case-0.0.1-SNAPSHOT.jar
   ``` 
4. Run with Docker:
   ```bash
   docker build -t case .
   docker run -p 8080:8080 case
   ```

### API Documentation
Access the Swagger UI at: `http://localhost:8080/swagger-ui/index.html`

### Monitoring
Actuator endpoints: `http://localhost:8080/actuator`

### Example API Usage

#### Login
- Place the access token you get from this endpoint to other requests as a Bearer token

```bash
curl --location 'localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "username": "user1",
    "password": "deneme"
}'
```

#### Create Order

```bash
curl --location 'localhost:8080/order' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTc0MjA3Mjg2MCwiZXhwIjoxNzQyMDc2NDYwfQ.7HjWe8f_GWap8NpTDICOUJRUqSnihU-8U8B1wtO9M6dWRyVcRYRoPw8CpCg6UhI_' \
--data '{
    "customerId": "1",
    "symbol": "AAPL",
    "side": "BUY",
    "size": 1,
    "price": 100
}'
```

#### List Orders

```bash
curl --location 'localhost:8080/order?customerId=1&startDate=2024-01-01T00%3A00%3A00&endDate=2026-02-01T00%3A00%3A00' \
--header 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTc0MjA3Mjg2MCwiZXhwIjoxNzQyMDc2NDYwfQ.7HjWe8f_GWap8NpTDICOUJRUqSnihU-8U8B1wtO9M6dWRyVcRYRoPw8CpCg6UhI_'
```

#### Cancel Order

```bash
curl --location --request DELETE 'localhost:8080/order/3' \
--header 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTc0MjA3Mjg2MCwiZXhwIjoxNzQyMDc2NDYwfQ.7HjWe8f_GWap8NpTDICOUJRUqSnihU-8U8B1wtO9M6dWRyVcRYRoPw8CpCg6UhI_'
```

#### List Assets

```bash
curl --location 'localhost:8080/asset?customerId=3' \
--header 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTc0MjA3Mjg2MCwiZXhwIjoxNzQyMDc2NDYwfQ.7HjWe8f_GWap8NpTDICOUJRUqSnihU-8U8B1wtO9M6dWRyVcRYRoPw8CpCg6UhI_'
```

#### Match Order

```bash
curl --location --request PATCH 'localhost:8080/order/1/match' \
--header 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTc0MjA3Mjg2MCwiZXhwIjoxNzQyMDc2NDYwfQ.7HjWe8f_GWap8NpTDICOUJRUqSnihU-8U8B1wtO9M6dWRyVcRYRoPw8CpCg6UhI_'
```