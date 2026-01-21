## Polls (MongoDB)

This project imports poll data from `sample_data.json` into **MongoDB** and exposes read-only endpoints to query polls.

Supported queries:
- `initiated`
- `title`
- `initiator.name`

---

## Run locally (docker-compose)

Prerequisite: Docker running.

```bash
cd /Users/ajclaros/Documents/Inditex/Workspace/other/doodle
docker compose up --build
```

- Service: http://localhost:8080
- MongoDB: mongodb://localhost:27017/doodle

On startup, the app imports `src/main/resources/sample_data.json` **only if the `polls` collection is empty**.

---

## API endpoints

Base path: `/api/v1/polls`

All endpoints return the **raw poll JSON payload** (the full original poll document) as JSON objects.

1) List polls created by a user (initiator.name)

- `GET /api/v1/polls/by-initiator?name={initiatorName}`

2) List polls by title

- `GET /api/v1/polls/by-title?title={title}`

3) List polls created after a date

- `GET /api/v1/polls/initiated-after?initiatedAfter=2026-01-01T00:00:00Z`

---

## Running tests

Unit/integration tests use **Testcontainers** for MongoDB.

Prerequisite: Docker running.

```bash
./mvnw test
```

---

## Notes

- `PollImportService` loads the JSON file and persists a minimal document shape (`id`, `initiated`, `title`, `initiator`) plus the full raw JSON payload.
- Repository queries are implemented via Spring Data Mongo derived query methods.

---

## Areas of improvement

1) **API contract & error handling**
   - Add consistent error responses (problem+json), validation, and clear HTTP semantics.
   - Document the API with OpenAPI/Swagger.

2) **Search capabilities & pagination**
   - Add pagination/sorting to all list endpoints.
   - Consider richer search (e.g., combined filters, exact vs partial matching).

3) **Data ingestion & migrations**
   - Make imports idempotent and support migrations/backfills when the schema evolves (e.g., re-import vs upsert-by-id).
   - Add a dedicated migration flag/property so startup behavior is predictable across environments.

4) **Observability & operations**
   - Add request logging/correlation IDs and basic metrics (latency, counts) via Micrometer/Actuator.
   - Improve local/dev parity (profiles, env vars) and add health/readiness endpoints if deploying to containers.

5) **Testing coverage & maintainability**
   - Add integration tests for the controller stack against a real Mongo (Testcontainers) to validate end-to-end behavior.
   - Reduce duplication with shared fixtures/builders and add edge-case tests (missing params, invalid timestamps, empty results).
