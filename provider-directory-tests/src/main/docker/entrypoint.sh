#!/usr/bin/env bash
set -euo pipefail
# =~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=

init() {
  test -n "${K8S_ENVIRONMENT}"

  if [ -z "${SENTINEL_BASE_DIR:-}" ]; then SENTINEL_BASE_DIR=/sentinel; fi
  cd $SENTINEL_BASE_DIR

  SYSTEM_PROPERTIES=()

  if [ -z "${SENTINEL_ENV:-}" ]; then SENTINEL_ENV=$K8S_ENVIRONMENT; fi
  if [ -z "${PDAPI_URL:-}" ]; then PDAPI_URL=https://$K8S_LOAD_BALANCER; fi
}

main() {
  addToSystemProperties "sentinel" "${SENTINEL_ENV}"
  addToSystemProperties "sentinel.internal.url" "${PDAPI_URL}"
  addToSystemProperties "sentinel.r4.url" "${PDAPI_URL}"
  addToSystemProperties "access-token" "${MAGIC_ACCESS_TOKEN}"

  if [ -n "${INTERNAL_API_PATH:-}" ]; then addToSystemProperties "sentinel.internal.api-path" "$INTERNAL_API_PATH"; fi
  if [ -n "${R4_API_PATH:-}" ]; then addToSystemProperties "sentinel.r4.api-path" "$R4_API_PATH"; fi

  java-tests \
    --module-name "provider-directory-tests" \
    --regression-test-pattern ".*IT\$" \
    --smoke-test-pattern ".*ProviderDirectoryIT\$" \
    ${SYSTEM_PROPERTIES[@]} \
    $@ \
    2>&1 | grep -v "WARNING: "

  exit $?
}

# =~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=

addToSystemProperties() {
  SYSTEM_PROPERTIES+=("-D$1=$2")
}

# =~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=~=

init
main $@
