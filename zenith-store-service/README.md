# Zenith Store Service

Manages businesses (stores) and product catalogues. Business owners register shops and manage products; customers browse nearby stores.

## Port

**8082**

## Dependencies

- **PostgreSQL** – stores and products (DB: `zenith_stores`)

## Environment

See root `.env.example`. Variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.

## Run

```bash
# From repo root
mvn -pl zenith-store-service spring-boot:run

# Or from this folder
mvn spring-boot:run
```

## API (planned)

- Store CRUD (business owners)
- Product CRUD (business owners)
- Browse nearby stores (customers)
- Store detail + products (customers)

API docs (Swagger) can be added when endpoints are implemented.
