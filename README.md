# OmniMail CMS

Email campaign template CMS (GCP-ready, **lightweight local dev**).

## Documentation

| Document | Description |
|----------|-------------|
| [docs/SOLUTION.md](docs/SOLUTION.md) | Architecture and design |
| [docs/API.md](docs/API.md) | REST API reference (endpoints, roles, workflow) |
| [docs/DEPLOY-GCP.md](docs/DEPLOY-GCP.md) | Install and deploy on Google Cloud |
| [docs/TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md) | Common local dev issues and fixes |

---

## Quick start (local)

**Prerequisites:** JDK 17+, Maven, Docker + Compose. See [TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md) if setup fails.

### Option A — dev script (Mongo in Docker, API on host)

```bash
chmod +x scripts/dev.sh
./scripts/dev.sh
```

### Option B — Docker only

```bash
docker compose up --build
```

API: http://localhost:8080

### Option C — UI (optional, second terminal)

```bash
cd web && npm install && npm run dev
```

UI: http://localhost:5173 (proxies `/api` to the API)

### Try the API

```bash
curl -s -X POST http://localhost:8080/api/v1/templates \
  -H 'Content-Type: application/json' \
  -d '{"name":"Welcome","mjmlSource":"<html><body>Hi {{name}}</body></html>","variablesSchema":{}}'
```

Full endpoint list: [docs/API.md](docs/API.md).

---

## Production (Google Cloud)

Deploy the API to **Cloud Run** with MongoDB Atlas and GCS. Step-by-step guide: **[docs/DEPLOY-GCP.md](docs/DEPLOY-GCP.md)**.

---

## Project layout

```text
api/                    # Spring Boot API
web/                    # React UI (optional)
config/                 # system.yaml (Git author, etc.)
docs/                   # API, deploy, troubleshooting, architecture
scripts/dev.sh          # quick local run
docker-compose.yml
```

---

## License

Apache 2.0 — see [LICENSE](LICENSE).
