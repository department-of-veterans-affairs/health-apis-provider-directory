#!/usr/bin/env bash
set -eo pipefail
# ==================================================

log() {
  echo "$(date --iso-8601=seconds) $1"
}

# ==================================================

CONF_FILE=${NGINX_CONF:-nginx.conf}

mkdir /nginx

if [ -n "${DU_AWS_BUCKET_NAME:-}" ] && [ -n "${DU_S3_FOLDER:-}" ] 
then
  log "Pulling nginx configuration from s3..."
  aws s3 cp s3://${DU_AWS_BUCKET_NAME}/${DU_S3_FOLDER}/${CONF_FILE} /nginx/nginx.conf
else
  log "Using local nginx configuration..."
  cp ${CONF_FILE} /nginx/nginx.conf
fi

log "Starting nginx..."
nginx -g "daemon off;" -c /nginx/nginx.conf

