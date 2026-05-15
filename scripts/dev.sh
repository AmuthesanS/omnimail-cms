#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

compose() {
  if docker compose version &>/dev/null; then
    docker compose "$@"
  elif command -v docker-compose &>/dev/null; then
    docker-compose "$@"
  else
    echo "Error: Docker Compose is not installed." >&2
    echo "  Ubuntu/Debian: sudo apt install docker-compose-v2" >&2
    echo "  (or: sudo apt install docker-compose)" >&2
    exit 1
  fi
}

if ! docker info &>/dev/null; then
  echo "Error: Cannot connect to the Docker daemon." >&2
  if [[ -S /var/run/docker.sock ]] && ! groups | grep -qw docker; then
    echo "Your user is not in the 'docker' group. Run:" >&2
    echo "  sudo usermod -aG docker \"\$USER\"" >&2
    echo "Then log out and back in (or: newgrp docker), and retry." >&2
  else
    echo "Is the Docker service running? Try: sudo systemctl start docker" >&2
  fi
  exit 1
fi

echo "Starting MongoDB..."
compose -f "$ROOT/docker-compose.yml" up -d mongodb

echo "Starting API (JDK 17+ required)..."
export MONGODB_URI="${MONGODB_URI:-mongodb://localhost:27017/omnimail}"
export OMNIMAIL_DATA_DIR="${OMNIMAIL_DATA_DIR:-$ROOT/data/blobs}"
cd "$ROOT/api"
mvn -q spring-boot:run
