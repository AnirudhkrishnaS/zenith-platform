# Zenith Store Service

Manages businesses (stores) and product catalogues. Business owners register shops and manage products; customers browse nearby stores.

## Port

**8082**

## Two-level catalog (Product → SKU)

- **Product**: logical item (e.g. "Espresso", "T-Shirt"). Create with name and description.
- **SKU**: sellable variant under a product (e.g. "ESPRESSO-01", "ESPRESSO-02"); has sku code, optional UPC, and price. One product can have many SKUs.
- Orders (in order service) reference **SKU** (by sku or upc), not product.

## Dependencies

- **PostgreSQL** – stores, products, and skus (DB: `zenith_stores`). If you had an existing `products` table with `sku`/`upc`/`price` columns, drop those columns or use a fresh schema; the new model uses a separate `skus` table.

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
