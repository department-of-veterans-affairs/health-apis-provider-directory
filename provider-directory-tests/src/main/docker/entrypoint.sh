#!/usr/bin/env bash

set -euo pipefail

if [ -z "${SENTINEL_BASE_DIR:-}" ]; then SENTINEL_BASE_DIR=/sentinel; fi
cd $SENTINEL_BASE_DIR

java-tests \
  --module-name "provider-directory-tests" \
  --regression-test-pattern ".*IT\$" \
  --smoke-test-pattern ".*SmokeIT\$" \
  -Dsentinel="$SENTINEL_ENV" \
  -Dsentinel.internal.url="${SENTINEL_URL}" \
  $@

exit $?
