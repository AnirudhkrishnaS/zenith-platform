# Zenith User Service

Handles registration, login (JWT), and profile for all user types: **Customer**, **Business Owner**, **Delivery Partner**.

## Port

**8081**

## Dependencies

- **PostgreSQL** – user data (DB: `zenith_users`)

## Environment

See root `.env.example`. Main variables:

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET`, `JWT_EXPIRATION_MS`

## Run

```bash
# From repo root
mvn -pl zenith-user-service spring-boot:run

# Or from this folder
mvn spring-boot:run
```

## API

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/auth/register` | No | Register (email, password, userType, fullName, phone) |
| POST | `/api/auth/login` | No | Login → JWT + user |
| GET | `/api/users/me` | Bearer JWT | Current user profile |
| PUT | `/api/users/me` | Bearer JWT | Update fullName, phone |

## API documentation

- **OpenAPI (Swagger UI):** http://localhost:8081/swagger-ui.html  
- **OpenAPI JSON:** http://localhost:8081/v3/api-docs
