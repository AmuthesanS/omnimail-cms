# OmniMail CMS

Email campaign template CMS (GCP-ready, **lightweight local dev**).

Architecture: [docs/SOLUTION.md](docs/SOLUTION.md)

---

## Lightweight local setup

| Before (heavy) | Now (light) |
|----------------|-------------|
| Parent + child Maven POM | **Single** `api/pom.xml` |
| Spring Security + OAuth2 | Simple **HTTP headers** for tenant/roles |
| fake-gcs-server + init + web containers | **MongoDB + API** only in Docker |
| 5 Docker services | **2 Docker services** |
| GCS emulator required locally | **Local disk** storage (`./data/blobs`) |

### Option A — one script (Mongo in Docker, API on host)

```bash
chmod +x scripts/dev.sh
./scripts/dev.sh
```

Needs **JDK 17+** and Maven.

### Option B — Docker only

```bash
docker compose up --build
```

API: http://localhost:8080

### Option C — UI (optional, separate terminal)

```bash
cd web && npm install && npm run dev
```

UI: http://localhost:5173 (proxies `/api` to the API)

---

## Try the API

```bash
curl -s -X POST http://localhost:8080/api/v1/templates \
  -H 'Content-Type: application/json' \
  -d '{"name":"Welcome","mjmlSource":"<html><body>Hi {{name}}</body></html>","variablesSchema":{}}'
```

Dev headers (optional): `X-Tenant-Id`, `X-User-Id`, `X-User-Roles` (defaults grant all roles).

---

## Production (Google Cloud)

Set profile **`gcp`** and storage type **`gcs`**:

```bash
export SPRING_PROFILES_ACTIVE=gcp
export OMNIMAIL_STORAGE_TYPE=gcs   # maps to omnimail.storage.type
export GCP_PROJECT_ID=your-project
export GCS_BUCKET=your-bucket
export MONGODB_URI=mongodb+srv://...
```

Cloud Run: build from `api/Dockerfile`, attach service account for GCS.

---

## Project layout

```text
api/              # Spring Boot (single module)
web/              # React UI (optional)
config/           # system.yaml (Git author, etc.)
scripts/dev.sh    # quick local run
docker-compose.yml
```

---

## Branch

Application code: **`feature/application`**

---

## License

Apache 2.0 — see [LICENSE](LICENSE).
