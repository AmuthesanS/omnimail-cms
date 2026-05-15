# OmniMail CMS

A **Google Cloud–based** content management system for email marketing campaigns: versioned templates, review workflows, asset storage on **Cloud Storage (GCS)**, and on-demand email delivery.

This repository is in the **planning / documentation** phase. Implementation will follow the architecture described in [docs/SOLUTION.md](docs/SOLUTION.md).

---

## Goals

- Store and version **email templates** (MJML/HTML) and **campaign metadata**
- Support **authoring** (UI drafts + Git as source of truth for production)
- Enforce **authentication**, **RBAC**, and **approval workflows** before publish
- Serve large assets via **GCS + Cloud CDN**
- **Send** rendered email content on request (test and production paths)
- Run on **Google Cloud only** (no AWS services or SDKs)
- Remain **portable** to other clouds later via ports-and-adapters (see solution doc)

---

## Architecture (summary)

| Layer | Technology |
|-------|------------|
| UI | React (Firebase Hosting or GCS + Cloud CDN) |
| API | Java 21, Spring Boot 3 on **Cloud Run** |
| Documents | **MongoDB Atlas** (GCP region) |
| Blobs | **Cloud Storage** (`google-cloud-storage`) |
| Cache (optional) | **Memorystore for Redis** |
| Auth | **Identity Platform** (OIDC / JWT) |
| CI/CD | **Cloud Build** |
| Email delivery | **SendGrid** or **Mailgun** (HTTP API; secrets in Secret Manager) |

**Data flow:** Git (source) → Cloud Build (validate, render) → GCS + MongoDB (immutable published versions) → Send service resolves version and dispatches via ESP.

Full design: **[docs/SOLUTION.md](docs/SOLUTION.md)**

---

## Repository layout (planned)

```text
omnimail-cms/
  config/
    system.yaml          # System settings (git author, paths)
    system.example.yaml  # Template for new environments
  docs/
    SOLUTION.md          # Architecture & decisions (this review set)
  api/                   # Spring Boot CMS + send API (future)
  web/                   # React UI (future)
  infra/
    cloudbuild/          # Publish pipelines (future)
  docker-compose.yml     # Local: Mongo, fake-gcs-server (future)
```

---

## Key decisions

| Topic | Choice |
|-------|--------|
| Authoring | UI for drafts; Git + Cloud Build for production publish |
| Database | MongoDB Atlas on GCP (not Firestore) — portability |
| Runtime | Spring Boot on Cloud Run |
| Object storage | GCS native SDK |
| Multi-tenant | `tenantId` on all documents from day one |
| Cloud scope | GCP only; ESP is external SaaS (non-AWS) |

---

## Configuration

System settings live under [`config/`](config/). Set **Git author** name and email (used for automated commits from the CMS and publish pipeline):

```yaml
omnimail:
  system:
    git:
      author:
        name: "Your Name or Service"
        email: "you@your-org.example"
```

Copy [`config/system.example.yaml`](config/system.example.yaml) to `config/system.yaml` if needed. See [docs/SOLUTION.md §17](docs/SOLUTION.md#17-system-configuration) for all keys.

---

## Documentation

| Document | Description |
|----------|-------------|
| [docs/SOLUTION.md](docs/SOLUTION.md) | Solution architecture, data model, APIs, workflow, GCP mapping, portability |

---

## Status

| Phase | Scope | Status |
|-------|--------|--------|
| 0 | Docs + repo structure | In progress |
| 1 | API skeleton, GCS/Mongo adapters, draft CRUD | Planned |
| 2 | Auth, workflow, versioning | Planned |
| 3 | Cloud Build publish from Git | Planned |
| 4 | Send API + ESP integration | Planned |
| 5 | CDN, cache, compliance logging | Planned |

---

## License

Apache License 2.0 — see [LICENSE](LICENSE).
