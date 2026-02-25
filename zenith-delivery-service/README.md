# Zenith Delivery Service

Delivery partner availability, GPS tracking (Redis geospatial), assignment of orders to partners, and the smart batching engine.

## Port

**8084**

## Dependencies

- **PostgreSQL** – delivery records (DB: `zenith_delivery`)
- **Redis** – live partner locations (geospatial), optional pub/sub

## Environment

See root `.env.example`. Variables: `DB_*`, `REDIS_HOST`, `REDIS_PORT`.

## Run

```bash
# From repo root
mvn -pl zenith-delivery-service spring-boot:run

# Or from this folder
mvn spring-boot:run
```

## API (planned)

- Partner availability toggle
- Location updates (GPS)
- Order assignment (from order-accepted events)
- Live tracking (WebSocket or polling)
- Batching job (scheduled)

API docs (Swagger) can be added when endpoints are implemented.
