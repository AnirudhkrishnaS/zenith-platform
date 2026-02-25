# Zenith Notification Service

Consumes order lifecycle events (e.g. from Redis pub/sub) and sends notifications (email, in-app, or log for MVP).

## Port

**8085**

## Dependencies

- **Redis** – subscribe to order/status events
- **Mail** (optional) – SMTP for email notifications

## Environment

See root `.env.example`. Variables: `REDIS_HOST`, `REDIS_PORT`, and mail settings if email is enabled.

## Run

```bash
# From repo root
mvn -pl zenith-notification-service spring-boot:run

# Or from this folder
mvn spring-boot:run
```

## Status

Event consumers and notification logic to be implemented. API docs (Swagger) can be added if the service exposes HTTP endpoints.
