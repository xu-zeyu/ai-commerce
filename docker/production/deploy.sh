#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)

cd "$SCRIPT_DIR"

if docker compose version >/dev/null 2>&1; then
  COMPOSE_CMD="docker compose"
elif command -v docker-compose >/dev/null 2>&1; then
  COMPOSE_CMD="docker-compose"
else
  echo "Neither 'docker compose' nor 'docker-compose' is available." >&2
  exit 1
fi

if [ -f .release.env ]; then
  set -a
  # shellcheck disable=SC1091
  . ./.release.env
  set +a
fi

$COMPOSE_CMD --env-file .env -f docker-compose.yml pull config-server admin-web
$COMPOSE_CMD --env-file .env -f docker-compose.yml up -d
