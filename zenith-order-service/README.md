# Zenith Order Service

Handles cart, checkout, order lifecycle, and mock payment. Business owners accept orders; status flows: PLACED → ACCEPTED → PREPARING → OUT_FOR_DELIVERY → DELIVERED.

## Port

**8083**

## Dependencies

- **PostgreSQL** – orders and order items (DB: `zenith_orders`)
- **Redis** (optional) – pub/sub for order-accepted events to Delivery / Notification

## Environment

See root `.env.example`. Variables: `DB_*`, `REDIS_HOST`, `REDIS_PORT` (if used).

## Run

```bash
# From repo root
mvn -pl zenith-order-service spring-boot:run

# Or from this folder
mvn spring-boot:run
```

## API (planned)

- Place order (cart/checkout)
- Mock payment
- Order list for store owner
- Accept / update order status
- Publish events for delivery and notifications

API docs (Swagger) can be added when endpoints are implemented.
