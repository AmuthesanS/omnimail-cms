# Troubleshooting

Common issues when running OmniMail CMS locally. For API behaviour see [API.md](API.md). For GCP deploy issues see [DEPLOY-GCP.md](DEPLOY-GCP.md).

## Docker Compose: `unknown shorthand flag: 'f' in -f`

**Cause:** Docker is installed but the **Compose plugin** is missing (`docker compose` is not available).

**Fix (Ubuntu/Debian):**

```bash
sudo apt install docker-compose-v2
docker compose version
```

The dev script (`scripts/dev.sh`) also falls back to standalone `docker-compose` if installed.

---

## Docker: `permission denied` on `/var/run/docker.sock`

**Cause:** Your user is not in the `docker` group.

**Fix:**

```bash
sudo usermod -aG docker $USER
```

Log out and back in, or run `newgrp docker`, then:

```bash
docker info
./scripts/dev.sh
```

**Workaround:** `sudo ./scripts/dev.sh` (not recommended long-term; files may be owned by root).

---

## JDK / Maven: API will not start via `dev.sh`

**Cause:** JDK 17+ or Maven not installed.

**Fix (Ubuntu):**

```bash
sudo apt update
sudo apt install openjdk-17-jdk maven
java -version
mvn -version
```

If an older Java is default:

```bash
sudo update-alternatives --config java
```

---

## `vite: not found` when running the UI

**Cause:** Frontend dependencies were not installed. Vite is a **local** dev dependency in `web/node_modules`, not a global command.

**Fix:**

```bash
cd web
npm install
npm run dev
```

Always use `npm run dev`, not `vite` directly.

---

## `npm install` fails with `ENOTFOUND registry.npmjs.org`

**Cause:** No network access to the npm registry (offline machine, proxy, or DNS).

**Fix:** Check internet/DNS/VPN. If behind a proxy, configure npm:

```bash
npm config set proxy http://your-proxy:port
npm config set https-proxy http://your-proxy:port
```

Prefer **Node 20 LTS** over Ubuntu’s `apt install npm` when possible:

```bash
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs
```

---

## API runs but nothing in the browser at `http://localhost:8080/`

**Cause:** `./scripts/dev.sh` starts the **REST API only**, not a homepage.

**Fix:**

| What you want | URL |
|---------------|-----|
| API (JSON) | http://localhost:8080/api/v1/templates |
| Web UI | Start separately: `cd web && npm install && npm run dev` → http://localhost:5173 |

---

## Empty template list / wrong tenant

**Cause:** Data is scoped by `X-Tenant-Id` (default `default`). Templates created under another tenant will not appear.

**Fix:**

```bash
curl -s http://localhost:8080/api/v1/templates -H 'X-Tenant-Id: default'
```

Use the same tenant header when creating and listing. MJML content is on **versions**:

```bash
curl -s http://localhost:8080/api/v1/templates/TEMPLATE_ID/versions \
  -H 'X-Tenant-Id: default'
```

---

## Read data directly from MongoDB

```bash
docker exec -it $(docker ps -qf name=mongodb) mongosh omnimail --quiet \
  --eval 'db.templates.find().toArray()'

docker exec -it $(docker ps -qf name=mongodb) mongosh omnimail --quiet \
  --eval 'db.template_versions.find().pretty()'
```

---

## Workflow errors (`409` / `WorkflowException`)

**Cause:** Invalid status transition or missing role (`editor`, `reviewer`, `publisher`).

**Fix:** Send roles via header:

```bash
-H 'X-User-Roles: editor,reviewer,publisher,admin'
```

See allowed transitions in [API.md](API.md#workflow-statuses).

---

## GCP deploy: API cannot write to GCS

**Cause:** Cloud Run service account lacks bucket permissions.

**Fix:** Grant `roles/storage.objectAdmin` on the bucket (or project) to `omnimail-api@PROJECT.iam.gserviceaccount.com`. Confirm `SPRING_PROFILES_ACTIVE=gcp`, `GCP_PROJECT_ID`, and `GCS_BUCKET` are set on the service.

---

## Still stuck?

1. Confirm MongoDB is up: `docker compose ps` or `docker ps | grep mongo`
2. Check API logs in the terminal running `dev.sh` or `mvn spring-boot:run`
3. See [API.md](API.md) for request/response formats
