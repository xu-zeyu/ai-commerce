#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
ROOT_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/../.." && pwd)
DIST_DIR="$ROOT_DIR/docker/app/dist"
CONFIG_SERVER_MODULE=":ai-commerce-start:ai-commerce-starter-config-server"
ADMIN_WEB_MODULE=":jinHan-shop:jinHan-shop-admin-web"
CONFIG_SERVER_JAR_DIR="$ROOT_DIR/ai-commerce-start/ai-commerce-starter-config-server/build/libs"
ADMIN_WEB_JAR_DIR="$ROOT_DIR/jinHan-shop/jinHan-shop-admin-web/build/libs"
BUILD_PLATFORM_DEFAULT="linux/amd64"
PUBLISH_MODE_DEFAULT="buildx-push"
RUNTIME_BASE_IMAGE_DEFAULT="eclipse-temurin:17-jre-noble"

cd "$SCRIPT_DIR"

if ! docker buildx version >/dev/null 2>&1; then
  echo "'docker buildx' is required for cross-platform image publishing." >&2
  exit 1
fi

if [ ! -f .env ]; then
  echo ".env not found. Copy .env.example to .env first." >&2
  exit 1
fi

read_env_value() {
  key=$1
  if [ ! -f .env ]; then
    return 1
  fi
  grep -E "^${key}=" .env | tail -n 1 | cut -d= -f2- | tr -d '\r'
}

IMAGE_REPOSITORY=${IMAGE_REPOSITORY:-$(read_env_value IMAGE_REPOSITORY || true)}
if [ -z "$IMAGE_REPOSITORY" ]; then
  echo "IMAGE_REPOSITORY is required in .env, for example: crpi-xxx.cn-guangzhou.personal.cr.aliyuncs.com/ze_yu/ze_yu1" >&2
  exit 1
fi

IMAGE_VERSION=${1:-${IMAGE_VERSION:-$(read_env_value IMAGE_VERSION || true)}}
if [ -z "$IMAGE_VERSION" ]; then
  IMAGE_VERSION=$(date +"%Y%m%d%H%M%S")
fi

BUILD_PLATFORM=${BUILD_PLATFORM:-$(read_env_value BUILD_PLATFORM || true)}
if [ -z "$BUILD_PLATFORM" ]; then
  BUILD_PLATFORM="$BUILD_PLATFORM_DEFAULT"
fi

PUBLISH_MODE=${PUBLISH_MODE:-$(read_env_value PUBLISH_MODE || true)}
if [ -z "$PUBLISH_MODE" ]; then
  PUBLISH_MODE="$PUBLISH_MODE_DEFAULT"
fi

RUNTIME_BASE_IMAGE=${RUNTIME_BASE_IMAGE:-$(read_env_value RUNTIME_BASE_IMAGE || true)}
if [ -z "$RUNTIME_BASE_IMAGE" ]; then
  RUNTIME_BASE_IMAGE="$RUNTIME_BASE_IMAGE_DEFAULT"
fi

CONFIG_SERVER_IMAGE_OVERRIDE=${CONFIG_SERVER_IMAGE_OVERRIDE:-$(read_env_value CONFIG_SERVER_IMAGE_OVERRIDE || true)}
ADMIN_WEB_IMAGE_OVERRIDE=${ADMIN_WEB_IMAGE_OVERRIDE:-$(read_env_value ADMIN_WEB_IMAGE_OVERRIDE || true)}
CONFIG_SERVER_IMAGE=${CONFIG_SERVER_IMAGE_OVERRIDE:-${IMAGE_REPOSITORY}:config-server-${IMAGE_VERSION}}
ADMIN_WEB_IMAGE=${ADMIN_WEB_IMAGE_OVERRIDE:-${IMAGE_REPOSITORY}:admin-web-${IMAGE_VERSION}}

REGISTRY_HOST=${IMAGE_REPOSITORY%%/*}
REGISTRY_USERNAME=${REGISTRY_USERNAME:-$(read_env_value REGISTRY_USERNAME || true)}
REGISTRY_PASSWORD=${REGISTRY_PASSWORD:-$(read_env_value REGISTRY_PASSWORD || true)}

if [ -n "${REGISTRY_USERNAME:-}" ] && [ -n "${REGISTRY_PASSWORD:-}" ]; then
  printf '%s' "$REGISTRY_PASSWORD" | docker login --username "$REGISTRY_USERNAME" --password-stdin "$REGISTRY_HOST"
fi

find_boot_jar() {
  jar_dir=$1
  jar_path=$(find "$jar_dir" -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' | head -n 1)
  if [ -z "$jar_path" ]; then
    echo "No boot jar found in $jar_dir" >&2
    exit 1
  fi
  printf '%s\n' "$jar_path"
}

echo "Building application jars on host..."
"$ROOT_DIR/gradlew" \
  --project-dir "$ROOT_DIR" \
  --parallel \
  --build-cache \
  "$CONFIG_SERVER_MODULE:bootJar" \
  "$ADMIN_WEB_MODULE:bootJar"

mkdir -p "$DIST_DIR"
cp "$(find_boot_jar "$CONFIG_SERVER_JAR_DIR")" "$DIST_DIR/config-server.jar"
cp "$(find_boot_jar "$ADMIN_WEB_JAR_DIR")" "$DIST_DIR/admin-web.jar"

cat > .release.env <<EOF
CONFIG_SERVER_IMAGE=${CONFIG_SERVER_IMAGE}
ADMIN_WEB_IMAGE=${ADMIN_WEB_IMAGE}
IMAGE_VERSION=${IMAGE_VERSION}
EOF

set -a
# shellcheck disable=SC1091
. ./.release.env
set +a

BUILD_EXTRA_ARGS=""
if [ "${PULL_BASE_IMAGES:-false}" = "true" ]; then
  BUILD_EXTRA_ARGS="--pull"
fi

echo "Publishing runtime images for platform: ${BUILD_PLATFORM}"
echo "Publish mode: ${PUBLISH_MODE}"
echo "Runtime base image: ${RUNTIME_BASE_IMAGE}"

publish_with_buildx_push() {
  image_tag=$1
  jar_file=$2
  install_playwright=$3
  # shellcheck disable=SC2086
  docker buildx build \
    --platform "${BUILD_PLATFORM}" \
    ${BUILD_EXTRA_ARGS} \
    --push \
    -f "$ROOT_DIR/docker/app/Dockerfile.runtime" \
    --build-arg RUNTIME_BASE_IMAGE="${RUNTIME_BASE_IMAGE}" \
    --build-arg APP_JAR_FILE="${jar_file}" \
    --build-arg INSTALL_PLAYWRIGHT="${install_playwright}" \
    -t "${image_tag}" \
    "$ROOT_DIR"
}

publish_with_load_push() {
  image_tag=$1
  jar_file=$2
  install_playwright=$3
  case "$BUILD_PLATFORM" in
    *,*)
      echo "PUBLISH_MODE=load-push only supports a single platform, but BUILD_PLATFORM=${BUILD_PLATFORM}" >&2
      exit 1
      ;;
  esac

  # shellcheck disable=SC2086
  docker buildx build \
    --platform "${BUILD_PLATFORM}" \
    ${BUILD_EXTRA_ARGS} \
    --load \
    -f "$ROOT_DIR/docker/app/Dockerfile.runtime" \
    --build-arg RUNTIME_BASE_IMAGE="${RUNTIME_BASE_IMAGE}" \
    --build-arg APP_JAR_FILE="${jar_file}" \
    --build-arg INSTALL_PLAYWRIGHT="${install_playwright}" \
    -t "${image_tag}" \
    "$ROOT_DIR"

  docker push "${image_tag}"
}

case "$PUBLISH_MODE" in
  buildx-push)
    publish_with_buildx_push "${CONFIG_SERVER_IMAGE}" docker/app/dist/config-server.jar false
    publish_with_buildx_push "${ADMIN_WEB_IMAGE}" docker/app/dist/admin-web.jar true
    ;;
  load-push)
    publish_with_load_push "${CONFIG_SERVER_IMAGE}" docker/app/dist/config-server.jar false
    publish_with_load_push "${ADMIN_WEB_IMAGE}" docker/app/dist/admin-web.jar true
    ;;
  *)
    echo "Unsupported PUBLISH_MODE=${PUBLISH_MODE}. Expected buildx-push or load-push." >&2
    exit 1
    ;;
esac

echo "Published images:"
echo "  ${CONFIG_SERVER_IMAGE}"
echo "  ${ADMIN_WEB_IMAGE}"
