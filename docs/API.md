# API reference

**Base URL (local):** `http://localhost:8080`  
**Base URL (Cloud Run):** your service URL (see [DEPLOY-GCP.md](DEPLOY-GCP.md))

All endpoints are under `/api/v1`. Request and response bodies are **JSON** unless noted.

## Request headers (dev)

| Header | Required | Default | Description |
|--------|----------|---------|-------------|
| `Content-Type` | For JSON bodies | — | `application/json` |
| `X-Tenant-Id` | No | `default` | Tenant scope for all queries |
| `X-User-Id` | No | `dev-user` | Actor id recorded on workflow events |
| `X-User-Roles` | No | all roles | Comma-separated: `editor`, `reviewer`, `publisher`, `admin` |

If `X-User-Roles` is omitted, all roles are granted. The `admin` role satisfies any role check.

Example:

```bash
curl -s http://localhost:8080/api/v1/templates \
  -H 'X-Tenant-Id: default' \
  -H 'X-User-Id: alice' \
  -H 'X-User-Roles: editor,reviewer,publisher'
```

## Error responses

Errors return JSON:

```json
{
  "timestamp": "2026-05-15T12:00:00Z",
  "status": 404,
  "message": "Template not found: abc"
}
```

| HTTP status | When |
|-------------|------|
| `400` | Validation failure, domain rule violation |
| `404` | Resource not found |
| `409` | Workflow / role conflict (`WorkflowException`) |
| `500` | Unexpected server error |

## MongoDB collections

| Collection | Contents |
|------------|----------|
| `templates` | Template metadata (`id`, `name`, `tags`, `tenantId`, timestamps) |
| `template_versions` | Versioned content (`mjmlSource`, `status`, `variablesSchema`, …) |
| `campaigns` | Campaign definitions linked to a `templateVersionId` |
| `send_logs` | Send history per campaign |
| `assets` | Uploaded file metadata (bytes in blob store) |
| `workflow_events` | Audit trail for template workflow transitions |

Database name: **`omnimail`** (from `MONGODB_URI`).

## Workflow statuses

Template versions use: `DRAFT` → `IN_REVIEW` → `APPROVED` → `PUBLISHING` → `PUBLISHED` (or `REJECTED` / `FAILED` with retries).

Allowed transitions:

| From | To |
|------|-----|
| `DRAFT` | `IN_REVIEW` |
| `IN_REVIEW` | `APPROVED`, `REJECTED` |
| `APPROVED` | `PUBLISHING`, `REJECTED` |
| `PUBLISHING` | `PUBLISHED`, `FAILED` |
| `FAILED` | `PUBLISHING` |
| `REJECTED` | `DRAFT` (via edit, then resubmit) |

---

## Templates — `/api/v1/templates`

### `POST /api/v1/templates`

Create a template and **version 1** in `DRAFT`.

- **Role:** `editor`
- **Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | string | yes | Template name |
| `tags` | string[] | no | Tags (default `[]`) |
| `mjmlSource` | string | no | MJML/HTML source for v1 |
| `variablesSchema` | object | no | JSON schema for template variables |

- **Response:** `201` — `Template` object

```json
{
  "id": "uuid",
  "tenantId": "default",
  "name": "Welcome",
  "tags": [],
  "createdAt": "2026-05-15T12:00:00Z",
  "updatedAt": "2026-05-15T12:00:00Z"
}
```

### `GET /api/v1/templates`

List all templates for the tenant (metadata only; no MJML).

- **Response:** `200` — `Template[]`

### `GET /api/v1/templates/{id}`

Get one template by id.

- **Response:** `200` — `Template`

### `PUT /api/v1/templates/{id}`

Update the **latest** version’s draft content (only when status is `DRAFT` or `REJECTED`).

- **Role:** `editor`
- **Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `mjmlSource` | string | no | Updated source |
| `variablesSchema` | object | no | Updated variables schema |

- **Response:** `200` — `TemplateVersion`

### `GET /api/v1/templates/{id}/versions`

List all versions for a template (includes `mjmlSource`).

- **Response:** `200` — `TemplateVersion[]`

### `GET /api/v1/templates/{id}/versions/{version}`

Get a specific version number (e.g. `1`).

- **Response:** `200` — `TemplateVersion`

```json
{
  "templateVersionId": "tpl_{templateId}_v1",
  "tenantId": "default",
  "templateId": "uuid",
  "version": 1,
  "status": "DRAFT",
  "mjmlSource": "<html>...</html>",
  "htmlObjectKey": null,
  "variablesSchema": {},
  "assetRefs": [],
  "gitRef": null,
  "sourcePath": null,
  "publishedAt": null,
  "publishedBy": null,
  "createdAt": "2026-05-15T12:00:00Z"
}
```

### `POST /api/v1/templates/{id}/submit`

Move latest version: `DRAFT` → `IN_REVIEW`.

- **Role:** `editor`
- **Response:** `200` — `TemplateVersion`

### `POST /api/v1/templates/{id}/approve`

Move latest version: `IN_REVIEW` → `APPROVED`.

- **Role:** `reviewer`
- **Response:** `200` — `TemplateVersion`

### `POST /api/v1/templates/{id}/reject`

Move latest version: `IN_REVIEW` or `APPROVED` → `REJECTED`.

- **Role:** `reviewer`
- **Body (optional):**

| Field | Type | Description |
|-------|------|-------------|
| `comment` | string | Rejection reason (stored in workflow events) |

- **Response:** `200` — `TemplateVersion`

### `POST /api/v1/templates/{id}/publish`

Start publish: `APPROVED` → `PUBLISHING`. Returns acceptance payload (full publish completes via Cloud Build in production).

- **Role:** `publisher`
- **Response:** `202`

```json
{
  "templateVersionId": "tpl_..._v1",
  "status": "PUBLISHING",
  "message": "Publish accepted; complete via Cloud Build",
  "gitAuthorEmail": "author@example.com"
}
```

`gitAuthorEmail` comes from `config/system.yaml` when present.

---

## Campaigns — `/api/v1/campaigns`

### `POST /api/v1/campaigns`

Create a campaign in `DRAFT` linked to a template version.

- **Role:** `editor`
- **Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | string | yes | Campaign name |
| `templateVersionId` | string | yes | e.g. `tpl_{id}_v1` |
| `subject` | string | yes | Email subject line |
| `fromEmail` | string | yes | Valid email address |
| `variables` | object | no | Default Mustache variables |

- **Response:** `201` — `Campaign`

```json
{
  "id": "uuid",
  "tenantId": "default",
  "name": "May newsletter",
  "templateVersionId": "tpl_..._v1",
  "subject": "Hello",
  "fromEmail": "noreply@example.com",
  "variables": { "firstName": "there" },
  "status": "DRAFT",
  "createdAt": "2026-05-15T12:00:00Z",
  "updatedAt": "2026-05-15T12:00:00Z"
}
```

### `GET /api/v1/campaigns`

List campaigns for the tenant.

- **Response:** `200` — `Campaign[]`

### `GET /api/v1/campaigns/{id}`

Get one campaign.

- **Response:** `200` — `Campaign`

### `PUT /api/v1/campaigns/{id}`

Update campaign fields (omitted fields keep existing values).

- **Role:** `editor`
- **Body:**

| Field | Type | Description |
|-------|------|-------------|
| `name` | string | Campaign name |
| `subject` | string | Email subject |
| `fromEmail` | string | Valid email |
| `variables` | object | Default variables |

- **Response:** `200` — `Campaign`

### `POST /api/v1/campaigns/{id}/send`

Send email to recipients using the campaign’s template. Renders HTML with **Mustache** (`{{variable}}`).

- **Role:** `editor` or `publisher`
- **Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `recipients` | string[] | yes | Email addresses |
| `variables` | object | no | Per-send overrides (merged over campaign `variables`) |
| `mode` | string | no | `test` (default) or production send |

- **Send rules:**
  - `mode=test` (default): allowed even if template version is not `PUBLISHED` (uses `mjmlSource` or stored HTML).
  - Any other `mode`: template version must be `PUBLISHED`.
- **Response:** `200` — `SendLog`

```json
{
  "id": "uuid",
  "tenantId": "default",
  "campaignId": "uuid",
  "templateVersionId": "tpl_..._v1",
  "mode": "test",
  "recipientCount": 1,
  "status": "sent",
  "createdAt": "2026-05-15T12:00:00Z"
}
```

Local dev uses a **logging** email provider (messages appear in API logs, not real SMTP).

---

## Assets — `/api/v1/assets`

### `POST /api/v1/assets`

Upload a file (deduplicated by content hash).

- **Role:** `editor`
- **Content-Type:** `multipart/form-data`
- **Form field:** `file` (the binary upload)
- **Response:** `200` — `Asset`

```json
{
  "contentHash": "sha256...",
  "tenantId": "default",
  "objectKey": "default/sha256...",
  "mimeType": "image/png",
  "sizeBytes": 1234,
  "createdAt": "2026-05-15T12:00:00Z"
}
```

### `GET /api/v1/assets/{hash}`

Get asset metadata and CDN URL.

- **Response:** `200`

```json
{
  "contentHash": "sha256...",
  "objectKey": "default/sha256...",
  "mimeType": "image/png",
  "sizeBytes": 1234,
  "cdnUrl": "http://localhost:8080/..."
}
```

Blob bytes are stored locally under `OMNIMAIL_DATA_DIR` (default `./data/blobs`) or in GCS when `omnimail.storage.type=gcs` (see [DEPLOY-GCP.md](DEPLOY-GCP.md)).

---

## Quick example (end-to-end)

```bash
BASE=http://localhost:8080
HDR='-H Content-Type: application/json -H X-Tenant-Id: default'

# 1. Create template
TPL=$(curl -s $BASE/api/v1/templates $HDR -d \
  '{"name":"Welcome","mjmlSource":"<html><body>Hi {{name}}</body></html>","variablesSchema":{}}')
TID=$(echo "$TPL" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)

# 2. Read versions (get templateVersionId)
curl -s $BASE/api/v1/templates/$TID/versions $HDR

# 3. List templates
curl -s $BASE/api/v1/templates $HDR
```
