# Zenith API Gateway

Single entry point for all client requests. Routes to microservices and validates JWT (when configured).

## Port

**8080**

## Dependencies

- None (routes to other services; JWT validation optional).

## Run

```bash
# From repo root
mvn -pl zenith-api-gateway spring-boot:run

# Or from this folder
mvn spring-boot:run
```

## Status

Routes and JWT filter to be configured. Target routes:

- `/api/auth/**` → User Service (8081)
- `/api/users/**` → User Service (8081)
- `/api/stores/**` → Store Service (8082)
- `/api/orders/**` → Order Service (8083)
- `/api/delivery/**` → Delivery Service (8084)
