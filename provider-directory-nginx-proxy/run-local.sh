#!/usr/bin/env bash
#=============================================

usage() {
  cat <<EOF
Usage:
  ${0} [options]

Options:
  --debug            Turn on debug output
  -h|--help          Display this menu
  -n|--nginx-conf    Location of nginx-conf file to use
  -s|--secrets-conf  Location of secrets file for environment substitution

${1}
EOF
exit 1
}

#=============================================

init() {
  set -euo pipefail

  WORK="$(readlink -f $(dirname $0))"
  cd ${WORK}

  DEPLOYMENT_UNIT="health-apis-provider-directory-deployment"

  SECRETS_CONF="${WORK}/secrets.conf"
  DEV_CONF="${WORK}/dev-nginx.conf"
}

main() {
  ARGS=$(getopt -n $(basename ${0}) \
      -l "debug,help,nginx-conf:,secrets-conf:" \
      -o "hn:s:" -- "$@")
  [ $? != 0 ] && usage
  eval set -- "$ARGS"
  while true
  do
    case "$1" in
      --debug) set -x;;
      -h|--help) usage "Just don't. Don't even. I can't.";;
      -n|--nginx-conf) NGINX_CONF="$(readlink -f ${2})";;
      -s|--secrets-conf) SECRETS_CONF="${2}";;
      --) shift;break;;
    esac
    shift;
  done

  # Source if found, else try anyway (file may have been pre-populated)
  if [ -f "${SECRETS_CONF}" ]
  then 
    source ${SECRETS_CONF}
  else
    echo "Could not find file (${SECRETS_CONF}). Attempting to run anyway..."
  fi

  if [ -z "${NGINX_CONF:-}" ]
  then 
    echo "Using default nginx configuration from deployment-unit."
    useDefaultNginxConf
  fi

  cat ${NGINX_CONF} | replacePorts | envsubst > ${DEV_CONF}

  local appVersion="$(determineProjectVersion)"
  findDockerImageForVersion "${appVersion}"

  runProviderDirectoryNginxProxy "${appVersion}"
}

#=============================================

determineProjectVersion() {
  cat ${WORK}/../pom.xml \
    | grep -A 3 '<artifactId>provider-directory-parent</artifactId>' \
    | grep version \
    | sed 's/.*<version>\(.*\)<[/]version>/\1/'
}

findDockerImageForVersion() {
  local version="${1:-}"

  echo "Version: ${version}"
  if [ -z "$(docker images --filter=reference=vasdvp/health-apis-provider-directory-nginx-proxy:${version} -q)" ]
  then
    echo "Couldn't find docker image for ${version} locally... Rebuilding..."
    mvn clean package -Prelease
  fi
}

replacePorts() {
  cat | sed \
    -e "/set.*dq.*/s/\${BLUE_LOAD_BALANCER_PORT}/${DQ_PROXY_PORT:-${BLUE_LOAD_BALANCER_PORT}}/" \
    -e "/set.*vfq.*/s/\${BLUE_LOAD_BALANCER_PORT}/${VFQ_PROXY_PORT:-${BLUE_LOAD_BALANCER_PORT}}/"
}

runProviderDirectoryNginxProxy() {
  local version="${1:-}"

  docker run --rm \
    --name provider-directory-proxy \
    --volume ${DEV_CONF}:/local-nginx.conf \
    --net="host" \
    --env NGINX_CONF=/local-nginx.conf \
    vasdvp/health-apis-provider-directory-nginx-proxy:${version}
}

useDefaultNginxConf() {
  local du=$(find "${SHANKTOPUS_WORKSPACE:-~/va}" -type d -name "${DEPLOYMENT_UNIT}")
  if [ -z "${du:-}" ]; then usage "Couldn't find provider-directory deployment unit: ${DEPLOYMENT_UNIT}";fi
  local conf="$(find ${du} -type f -name 'nginx.properties' | head -n +1)"
  if [ ! -f "${conf:-}" ]; then usage "Couldn't find default nginx.conf file."; fi
  NGINX_CONF="${conf}"
}

#=============================================

init
main $@
