#!/usr/bin/env bash

set -euo pipefail

if [ -z "${SENTINEL_BASE_DIR:-}" ]; then SENTINEL_BASE_DIR=/sentinel; fi
cd $SENTINEL_BASE_DIR

test -n "${DEPLOYMENT_ENVIRONMENT}"
test -n "${DEPLOYMENT_TEST_HOST}"
test -n "${DEPLOYMENT_TEST_PROTOCOL}"

if [ -z "${SENTINEL_ENV:-}" ]; then SENTINEL_ENV="$DEPLOYMENT_ENVIRONMENT"; fi
if [ -z "${SENTINEL_URL:-}" ]; then SENTINEL_URL="${DEPLOYMENT_TEST_PROTOCOL}://${DEPLOYMENT_TEST_HOST}"; fi

java-tests \
  --module-name "provider-directory-tests" \
  --regression-test-pattern ".*IT\$" \
  --smoke-test-pattern ".*SmokeIT\$" \
  -Dsentinel="$SENTINEL_ENV" \
  -Dsentinel.internal.url="${SENTINEL_URL}" \
  $@

exit $?
