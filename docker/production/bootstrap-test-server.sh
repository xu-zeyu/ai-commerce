#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
COMPOSE_PROJECT_NAME=${COMPOSE_PROJECT_NAME:-jinhan-commerce-test}
DEPLOY_HOME=${DEPLOY_HOME:-/opt/jinhan/ai-commerce-test}
CONFIG_REPO_HOME=${CONFIG_REPO_HOME:-/opt/jinhan/ai-commerce-test/config-repo}
ADMIN_WEB_CONFIG_HOME=${ADMIN_WEB_CONFIG_HOME:-/opt/jinhan/ai-commerce-test/admin-web/config}
ADMIN_WEB_DATA_HOME=${ADMIN_WEB_DATA_HOME:-/data/jinhan/ai-commerce-test/admin-web}
ADMIN_WEB_LOG_HOME=${ADMIN_WEB_LOG_HOME:-/var/log/jinhan/ai-commerce-test/admin-web}
STATIC_WEB_HOME=${STATIC_WEB_HOME:-/opt/html/jinhan-shop-web-test}

NGINX_HOME=${NGINX_HOME:-/opt/docker/nginx}
MYSQL_HOME=${MYSQL_HOME:-/opt/docker/mysql}
REDIS_HOME=${REDIS_HOME:-/opt/docker/redis}
RABBITMQ_HOME=${RABBITMQ_HOME:-/opt/docker/rabbitmq}

MYSQL_DATA_HOME=${MYSQL_DATA_HOME:-/data/docker/mysql}
MYSQL_LOG_HOME=${MYSQL_LOG_HOME:-/opt/docker/mysql/logs}
REDIS_DATA_HOME=${REDIS_DATA_HOME:-/data/docker/redis}
RABBITMQ_DATA_HOME=${RABBITMQ_DATA_HOME:-/data/docker/rabbitmq}
RABBITMQ_LOG_HOME=${RABBITMQ_LOG_HOME:-/opt/docker/rabbitmq/logs}
MINIO_DATA_HOME=${MINIO_DATA_HOME:-/data/docker/minio}

IMAGE_REPOSITORY=${IMAGE_REPOSITORY:-crpi-exofctq2c8bk7lmo.cn-guangzhou.personal.cr.aliyuncs.com/ze_yu/ze_yu1}
IMAGE_VERSION=${IMAGE_VERSION:-test}
FORCE=${FORCE:-0}

if [ "$(id -u)" -ne 0 ]; then
  echo "Please run this script as root, because it writes to /opt, /data and /var/log." >&2
  exit 1
fi

secret() {
  if command -v openssl >/dev/null 2>&1; then
    openssl rand -hex 24
  elif command -v uuidgen >/dev/null 2>&1; then
    uuidgen | tr -d '-'
  else
    date +%s%N | sha256sum | awk '{print $1}'
  fi
}

write_file() {
  target_file=$1
  if [ -f "$target_file" ] && [ "$FORCE" != "1" ]; then
    echo "skip existing file: $target_file"
    return 1
  fi
  mkdir -p "$(dirname "$target_file")"
  return 0
}

MYSQL_ROOT_PASSWORD=$(secret)
MYSQL_APP_PASSWORD=$(secret)
REDIS_PASSWORD=$(secret)
RABBITMQ_PASSWORD=$(secret)
CONFIG_SERVER_PASSWORD=$(secret)
MINIO_ROOT_PASSWORD=$(secret)

umask 077

mkdir -p \
  "$DEPLOY_HOME/scripts" \
  "$CONFIG_REPO_HOME" \
  "$ADMIN_WEB_CONFIG_HOME" \
  "$ADMIN_WEB_DATA_HOME" \
  "$ADMIN_WEB_LOG_HOME" \
  "$STATIC_WEB_HOME" \
  "$NGINX_HOME/conf/conf.d" \
  "$NGINX_HOME/certs" \
  "$NGINX_HOME/logs" \
  "$MYSQL_HOME/conf" \
  "$MYSQL_LOG_HOME" \
  "$REDIS_HOME/conf" \
  "$RABBITMQ_HOME/logs" \
  "$MYSQL_DATA_HOME" \
  "$REDIS_DATA_HOME" \
  "$RABBITMQ_DATA_HOME" \
  "$RABBITMQ_LOG_HOME" \
  "$MINIO_DATA_HOME"

if write_file "$DEPLOY_HOME/.env"; then
  cat > "$DEPLOY_HOME/.env" <<EOF
COMPOSE_PROJECT_NAME=$COMPOSE_PROJECT_NAME
TZ=Asia/Shanghai

DEPLOY_HOME=$DEPLOY_HOME
CONFIG_REPO_HOME=$CONFIG_REPO_HOME
ADMIN_WEB_CONFIG_HOME=$ADMIN_WEB_CONFIG_HOME
ADMIN_WEB_DATA_HOME=$ADMIN_WEB_DATA_HOME
ADMIN_WEB_LOG_HOME=$ADMIN_WEB_LOG_HOME
STATIC_WEB_HOME=$STATIC_WEB_HOME

NGINX_HOME=$NGINX_HOME
MYSQL_HOME=$MYSQL_HOME
REDIS_HOME=$REDIS_HOME
RABBITMQ_HOME=$RABBITMQ_HOME

MYSQL_DATA_HOME=$MYSQL_DATA_HOME
MYSQL_LOG_HOME=$MYSQL_LOG_HOME
REDIS_DATA_HOME=$REDIS_DATA_HOME
RABBITMQ_DATA_HOME=$RABBITMQ_DATA_HOME
RABBITMQ_LOG_HOME=$RABBITMQ_LOG_HOME
MINIO_DATA_HOME=$MINIO_DATA_HOME

MYSQL_DATABASE=jinhan_shop
MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD
MYSQL_APP_USERNAME=jinhan_app
MYSQL_APP_PASSWORD=$MYSQL_APP_PASSWORD
MYSQL_HOST_PORT=33306

DB_HOST=mysql
DB_PORT=3306
DB_NAME=jinhan_shop
DB_URL=jdbc:mysql://mysql:3306/jinhan_shop?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
DB_USERNAME=jinhan_app
DB_PASSWORD=$MYSQL_APP_PASSWORD
DB_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver

REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=$REDIS_PASSWORD
REDIS_HOST_PORT=6379

RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_AMQP_HOST_PORT=5672
RABBITMQ_MANAGEMENT_PORT=15672
RABBITMQ_USERNAME=jinhan
RABBITMQ_PASSWORD=$RABBITMQ_PASSWORD
RABBITMQ_VHOST=/

MINIO_ROOT_USER=jinhan_minio
MINIO_ROOT_PASSWORD=$MINIO_ROOT_PASSWORD
MINIO_API_PORT=9000
MINIO_CONSOLE_PORT=9001

CONFIG_SERVER_USERNAME=config-server
CONFIG_SERVER_PASSWORD=$CONFIG_SERVER_PASSWORD
CONFIG_SERVER_IMAGE=$IMAGE_REPOSITORY:config-server-$IMAGE_VERSION

ADMIN_WEB_IMAGE=$IMAGE_REPOSITORY:admin-web-$IMAGE_VERSION
ADMIN_WEB_HOST_PORT=8081

BROWSER_MCP_IMAGE=mcr.microsoft.com/playwright/mcp:latest
BROWSER_MCP_ALLOWED_HOSTS=browser-mcp,localhost,127.0.0.1
AI_CHAT_BROWSER_MCP_ENABLED=true
AI_CHAT_BROWSER_MCP_ENDPOINT=http://browser-mcp:8931/mcp
AI_CHAT_BROWSER_MCP_BEARER_TOKEN=
AI_CHAT_BROWSER_MCP_TIMEOUT_MILLIS=60000
AI_CHAT_BROWSER_MCP_MAX_TOOL_ROUND_TRIPS=12

SPRING_CLOUD_CONFIG_PROFILE=test
SPRING_CLOUD_CONFIG_LABEL=main
SPRING_CLOUD_CONFIG_FAIL_FAST=true

NGINX_HTTP_PORT=80
NGINX_HTTPS_PORT=443

IMAGE_REPOSITORY=$IMAGE_REPOSITORY
IMAGE_VERSION=$IMAGE_VERSION
EOF
fi

set -a
# shellcheck disable=SC1090
. "$DEPLOY_HOME/.env"
set +a

if write_file "$DEPLOY_HOME/docker-compose.yml"; then
  cp "$SCRIPT_DIR/docker-compose.yml" "$DEPLOY_HOME/docker-compose.yml"
fi

if write_file "$MYSQL_HOME/conf/jinhan.cnf"; then
  cat > "$MYSQL_HOME/conf/jinhan.cnf" <<'EOF'
[mysqld]
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
default-time-zone=+08:00
default-authentication-plugin=mysql_native_password
max_connections=500
slow_query_log=ON
slow_query_log_file=/var/log/mysql/slow.log
long_query_time=2

[client]
default-character-set=utf8mb4
EOF
fi
chmod 755 "$MYSQL_HOME" "$MYSQL_HOME/conf"
chmod 644 "$MYSQL_HOME/conf/jinhan.cnf"

if write_file "$REDIS_HOME/conf/redis.conf"; then
  cat > "$REDIS_HOME/conf/redis.conf" <<'EOF'
bind 0.0.0.0
port 6379
appendonly yes
appendfsync everysec
save 900 1
save 300 10
save 60 10000
dir /data
protected-mode no
EOF
fi
chmod 755 "$REDIS_HOME" "$REDIS_HOME/conf"
chmod 644 "$REDIS_HOME/conf/redis.conf"

if write_file "$CONFIG_REPO_HOME/jinHan-shop-admin-web-test.yml"; then
  cat > "$CONFIG_REPO_HOME/jinHan-shop-admin-web-test.yml" <<'EOF'
spring:
  datasource:
    url: "jdbc:mysql://${DB_HOST:mysql}:${DB_PORT:3306}/${DB_NAME:jinhan_shop}?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true"
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: ${DB_DRIVER_CLASS_NAME:com.mysql.cj.jdbc.Driver}
  data:
    redis:
      host: ${REDIS_HOST:redis}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD}
  redis:
    host: ${REDIS_HOST:redis}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD}
  rabbitmq:
    host: ${RABBITMQ_HOST:rabbitmq}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USERNAME}
    password: ${RABBITMQ_PASSWORD}
    virtual-host: ${RABBITMQ_VHOST:/}

logging:
  level:
    root: info
    com.jinHan.shop: debug

todo:
  biz-event:
    mode: local
    rabbit:
      exchange: todo.biz.event.exchange
      queue: todo.biz.event.queue
      routing-key: todo.biz.event
EOF
fi

if write_file "$NGINX_HOME/conf/nginx.conf"; then
  cat > "$NGINX_HOME/conf/nginx.conf" <<'EOF'
user nginx;
worker_processes auto;

events {
  worker_connections 1024;
}

http {
  include /etc/nginx/mime.types;
  default_type application/octet-stream;

  log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                  '$status $body_bytes_sent "$http_referer" '
                  '"$http_user_agent" "$http_x_forwarded_for"';

  access_log /var/log/nginx/access.log main;
  error_log /var/log/nginx/error.log warn;

  sendfile on;
  keepalive_timeout 65;
  client_max_body_size 50m;
  server_tokens off;

  gzip on;
  gzip_types text/plain text/css application/json application/javascript application/xml;

  include /etc/nginx/conf.d/*.conf;
}
EOF
fi
chmod 755 "$NGINX_HOME" "$NGINX_HOME/conf" "$NGINX_HOME/conf/conf.d"
chmod 644 "$NGINX_HOME/conf/nginx.conf"

if write_file "$NGINX_HOME/conf/conf.d/jinhan-shop-admin.conf"; then
  cat > "$NGINX_HOME/conf/conf.d/jinhan-shop-admin.conf" <<'EOF'
upstream jinhan_shop_admin {
  server admin-web:8081;
  keepalive 16;
}
EOF
fi
chmod 644 "$NGINX_HOME/conf/conf.d/jinhan-shop-admin.conf"

if write_file "$NGINX_HOME/conf/conf.d/jinhan-shop-web.conf"; then
  cat > "$NGINX_HOME/conf/conf.d/jinhan-shop-web.conf" <<'EOF'
server {
  listen 80 default_server;
  server_name _;

  root /usr/share/nginx/html/jinhan-shop-web;
  index index.html;

  add_header X-Content-Type-Options nosniff always;
  add_header X-Frame-Options SAMEORIGIN always;
  add_header X-XSS-Protection "1; mode=block" always;

  location = /api/ai/chat {
    proxy_pass http://jinhan_shop_admin/ai/chat;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_buffering off;
    proxy_cache off;
    gzip off;
    proxy_read_timeout 3600s;
    proxy_send_timeout 3600s;
  }

  location /api/ {
    proxy_pass http://jinhan_shop_admin/;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
  }

  location /actuator/ {
    return 404;
  }

  location / {
    try_files $uri $uri/ /index.html;
  }
}
EOF
fi
chmod 644 "$NGINX_HOME/conf/conf.d/jinhan-shop-web.conf"

if write_file "$STATIC_WEB_HOME/index.html"; then
  cat > "$STATIC_WEB_HOME/index.html" <<'EOF'
<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8">
  <title>jinhan shop web test</title>
</head>
<body>
  jinhan shop web test
</body>
</html>
EOF
fi

if write_file "$DEPLOY_HOME/scripts/deploy.sh"; then
  cat > "$DEPLOY_HOME/scripts/deploy.sh" <<EOF
#!/usr/bin/env sh
set -eu

cd "$DEPLOY_HOME"

if docker compose version >/dev/null 2>&1; then
  COMPOSE_CMD="docker compose"
elif command -v docker-compose >/dev/null 2>&1; then
  COMPOSE_CMD="docker-compose"
else
  echo "Neither 'docker compose' nor 'docker-compose' is available." >&2
  exit 1
fi

\$COMPOSE_CMD --env-file .env -f docker-compose.yml pull
\$COMPOSE_CMD --env-file .env -f docker-compose.yml up -d
\$COMPOSE_CMD --env-file .env -f docker-compose.yml ps
EOF
  chmod 700 "$DEPLOY_HOME/scripts/deploy.sh"
fi

chmod 600 "$DEPLOY_HOME/.env" 2>/dev/null || true

echo "Generated test configuration:"
echo "  deploy:       $DEPLOY_HOME"
echo "  config repo:  $CONFIG_REPO_HOME"
echo "  nginx config: $NGINX_HOME"
echo "  mysql config: $MYSQL_HOME"
echo "  redis config: $REDIS_HOME"
echo "  mysql data:   $MYSQL_DATA_HOME"
echo "  redis data:   $REDIS_DATA_HOME"
echo
echo "Next steps:"
echo "  1. Review image tags and passwords in $DEPLOY_HOME/.env"
echo "  2. Run: cd $DEPLOY_HOME && docker compose --env-file .env -f docker-compose.yml up -d"
