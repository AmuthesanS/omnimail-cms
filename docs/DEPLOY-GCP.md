# Deploy to Google Cloud

This guide covers installing OmniMail CMS on **Google Cloud Platform**. There is no Terraform or automated installer in the repo yet — use the steps below with `gcloud` and `api/Dockerfile`.

Architecture overview: [SOLUTION.md](SOLUTION.md).

## What you deploy

| Component | GCP service |
|-----------|-------------|
| API | **Cloud Run** (`api/Dockerfile`) |
| Database | **MongoDB Atlas** (GCP region) or self-managed MongoDB |
| Template / asset blobs | **Cloud Storage** (GCS) |
| UI (optional) | Firebase Hosting, GCS static + CDN, or Cloud Run (`web/Dockerfile`) |
| Email delivery | External ESP (SendGrid/Mailgun) — not a native GCP product |

```text
Browser / client  →  Cloud Run (API, profile gcp)
                        ├→ MongoDB Atlas (metadata)
                        └→ GCS bucket (blobs / rendered HTML)
```

## Prerequisites

- GCP project with billing enabled
- [gcloud CLI](https://cloud.google.com/sdk/docs/install) installed and authenticated

```bash
gcloud auth login
gcloud config set project YOUR_PROJECT_ID

gcloud services enable run.googleapis.com \
  artifactregistry.googleapis.com \
  cloudbuild.googleapis.com \
  storage.googleapis.com
```

## 1. MongoDB (Atlas recommended)

1. Create a cluster in [MongoDB Atlas](https://www.mongodb.com/atlas) → **Google Cloud** → region aligned with Cloud Run.
2. Create a database user and note the connection string:  
   `mongodb+srv://user:pass@cluster.mongodb.net/omnimail`
3. **Network access:** allow Cloud Run egress IPs, or use Atlas Private Endpoint / a **VPC connector** for production.

## 2. GCS bucket

```bash
export PROJECT_ID=your-project-id
export GCS_BUCKET=omnimail-assets-prod
export REGION=europe-west1   # align with Cloud Run and Atlas

gsutil mb -l $REGION gs://$GCS_BUCKET
```

### Service account for the API

```bash
gcloud iam service-accounts create omnimail-api \
  --display-name="OmniMail CMS API"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:omnimail-api@${PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/storage.objectAdmin"
```

For production, prefer granting **object admin on the bucket only**, not project-wide.

## 3. Build and push the API image

```bash
cd api

gcloud artifacts repositories create omnimail \
  --repository-format=docker \
  --location=$REGION

gcloud auth configure-docker ${REGION}-docker.pkg.dev

export IMAGE=${REGION}-docker.pkg.dev/${PROJECT_ID}/omnimail/api:latest

docker build -t $IMAGE .
docker push $IMAGE
```

The container uses **Java 21** (see `api/Dockerfile`) and listens on port **8080**. Cloud Run sets `PORT` automatically.

## 4. Deploy to Cloud Run

```bash
gcloud run deploy omnimail-cms-api \
  --image=$IMAGE \
  --region=$REGION \
  --platform=managed \
  --allow-unauthenticated \
  --service-account=omnimail-api@${PROJECT_ID}.iam.gserviceaccount.com \
  --set-env-vars="SPRING_PROFILES_ACTIVE=gcp" \
  --set-env-vars="GCP_PROJECT_ID=${PROJECT_ID}" \
  --set-env-vars="GCS_BUCKET=${GCS_BUCKET}" \
  --set-env-vars="MONGODB_URI=mongodb+srv://USER:PASS@cluster.mongodb.net/omnimail" \
  --set-env-vars="CDN_BASE_URL=https://storage.googleapis.com/${GCS_BUCKET}" \
  --memory=512Mi \
  --min-instances=0 \
  --max-instances=10
```

`SPRING_PROFILES_ACTIVE=gcp` loads `api/src/main/resources/application-gcp.yml`, which sets `omnimail.storage.type=gcs`.

Get the service URL:

```bash
gcloud run services describe omnimail-cms-api --region=$REGION --format='value(status.url)'
```

Smoke test:

```bash
curl -s https://YOUR_CLOUD_RUN_URL/api/v1/templates -H 'X-Tenant-Id: default'
```

## 5. System configuration

Git author metadata for publish flows lives in `config/system.yaml`:

```bash
cp config/system.example.yaml config/system.yaml
# Edit git.author.email, environment: prod, etc.
```

Spring Boot imports optional `config/system.yaml` via `spring.config.import` in `application.yml`. For Cloud Run, either bake the file into the image or map values to environment variables (see SOLUTION.md §17).

## 6. Optional: web UI

```bash
cd web
npm install
VITE_API_URL=https://YOUR_CLOUD_RUN_URL npm run build
```

Deploy `dist/` to Firebase Hosting, a GCS bucket behind Cloud CDN, or build `web/Dockerfile` and deploy a second Cloud Run service.

## Environment variables

| Variable | Required (GCP) | Purpose |
|----------|----------------|---------|
| `SPRING_PROFILES_ACTIVE` | `gcp` | Enables GCS storage (`application-gcp.yml`) |
| `GCP_PROJECT_ID` | Yes | GCS client project |
| `GCS_BUCKET` | Yes | Asset / HTML blob bucket |
| `MONGODB_URI` | Yes | MongoDB connection string |
| `CDN_BASE_URL` | Recommended | Public URL prefix for asset links |
| `PORT` | Set by Cloud Run | HTTP port (8080) |

Store secrets (especially `MONGODB_URI`) in **Secret Manager** and reference them from Cloud Run in production.

## Production checklist

| Item | Notes |
|------|--------|
| **Authentication** | Local dev uses `X-Tenant-Id` / `X-User-Roles`. Production should use Identity Platform or IAP (see SOLUTION.md). |
| **TLS** | Provided by Cloud Run on the service URL. |
| **Publish pipeline** | `POST /api/v1/templates/{id}/publish` returns `202`; full MJML → HTML → GCS is intended for **Cloud Build** (not fully automated in this repo). |
| **Email** | Default provider logs only; wire SendGrid/Mailgun for real sends. |

## Related configuration in code

| File | Purpose |
|------|---------|
| `api/src/main/resources/application-gcp.yml` | GCS profile |
| `api/src/main/java/io/omnimail/cms/infrastructure/gcp/GcsBlobStore.java` | GCS adapter |
| `api/Dockerfile` | Cloud Run image build |
| `config/system.example.yaml` | Git / environment template |

## References

- [Cloud Run](https://cloud.google.com/run/docs)
- [Cloud Storage](https://cloud.google.com/storage/docs)
- [MongoDB Atlas on GCP](https://www.mongodb.com/atlas/google-cloud)
- [API reference](API.md)
