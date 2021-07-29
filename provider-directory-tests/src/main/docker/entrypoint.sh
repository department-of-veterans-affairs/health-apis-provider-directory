#!/usr/bin/env bash

set -euo pipefail

if [ -z "${SENTINEL_BASE_DIR:-}" ]; then SENTINEL_BASE_DIR=/sentinel; fi
cd $SENTINEL_BASE_DIR

test -n "${K8S_ENVIRONMENT}"
test -n "${K8S_LOAD_BALANCER}"

if [ -z "${SENTINEL_ENV:-}" ]; then SENTINEL_ENV="$K8S_ENVIRONMENT"; fi
if [ -z "${SENTINEL_URL:-}" ]; then SENTINEL_URL="https://${K8S_LOAD_BALANCER}"; fi

java-tests \
  --module-name "provider-directory-tests" \
  --regression-test-pattern ".*IT\$" \
  --smoke-test-pattern ".*SmokeIT\$" \
  -Dsentinel="$SENTINEL_ENV" \
  -Dsentinel.internal.url="${SENTINEL_URL}" \
  $@

exit $?
