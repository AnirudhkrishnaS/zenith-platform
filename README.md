# Zenith Platform

A unified, affordable delivery platform for local businesses. Shared delivery partners and intelligent route batching reduce cost per delivery.

## Architecture

- **API Gateway** (8080) – Single entry point, routes to microservices
- **User Service** (8081) – Registration, login (JWT), profile
- **Store Service** (8082) – Business & catalogue management
- **Order Service** (8083) – Orders & mock payment
- **Delivery Service** (8084) – Logistics, tracking, batching
- **Notification Service** (8085) – Order status notifications

**Infrastructure:** PostgreSQL, Redis (Docker Compose at repo root).

## Prerequisites

- **Java 21**
- **Maven 3.9+**
- **Docker & Docker Compose** (for PostgreSQL and Redis)
- (Optional) **Node.js 18+** for frontend

## Quick Start

### 1. Clone and configure environment

```bash
git clone <repo-url>
cd ZENITH
cp .env.example .env
# Edit .env if needed (defaults work for local dev)
```

### 2. Start infrastructure

```bash
docker-compose up -d
```

This starts PostgreSQL (5432) and Redis (6379) and creates databases: `zenith_users`, `zenith_stores`, `zenith_orders`, `zenith_delivery`.

### 3. Run services

Run each service from the repo root or from its module folder.

**From root (Maven multi-module):**

```bash
# User Service (auth, profile)
mvn -pl zenith-user-service spring-boot:run

# API Gateway (after adding routes)
mvn -pl zenith-api-gateway spring-boot:run

# Other services similarly:
# mvn -pl zenith-store-service spring-boot:run
# mvn -pl zenith-order-service spring-boot:run
# mvn -pl zenith-delivery-service spring-boot:run
# mvn -pl zenith-notification-service spring-boot:run
```

**Or** open the project in an IDE and run the main class of each service.

### 4. Verify

- User Service: http://localhost:8081/actuator/health  
- API docs (User Service): http://localhost:8081/swagger-ui.html  
- Register: `POST http://localhost:8081/api/auth/register`  
- Login: `POST http://localhost:8081/api/auth/login`

## Ports

| Service              | Port |
|----------------------|------|
| API Gateway          | 8080 |
| User Service         | 8081 |
| Store Service        | 8082 |
| Order Service        | 8083 |
| Delivery Service     | 8084 |
| Notification Service | 8085 |

## Build all modules

```bash
mvn clean install
```

## License

