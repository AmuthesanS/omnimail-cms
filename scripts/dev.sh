#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

echo "Starting MongoDB..."
docker compose -f "$ROOT/docker-compose.yml" up -d mongodb

echo "Starting API (JDK 17+ required)..."
export MONGODB_URI="${MONGODB_URI:-mongodb://localhost:27017/omnimail}"
export OMNIMAIL_DATA_DIR="${OMNIMAIL_DATA_DIR:-$ROOT/data/blobs}"
cd "$ROOT/api"
mvn -q spring-boot:run
