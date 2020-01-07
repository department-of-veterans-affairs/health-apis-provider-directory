#!/usr/bin/env bash

ENDPOINT_DOMAIN_NAME="$K8S_LOAD_BALANCER"
ENVIRONMENT="$K8S_ENVIRONMENT"
TOKEN="$TOKEN"
BASE_PATH="$BASE_PATH"
ENDPOINT_ID="$ENDPOINT_ID"
ENDPOINT_NAME="$ENDPOINT_NAME"

#Put health endpoints here if you got them
PATHS=(/actuator/health \
/openapi.json \
/openapi.yaml)

SUCCESS=0

FAILURE=0

usage() {
cat <<EOF
Commands
  smoke-test [--endpoint-domain-name|-d <endpoint>] [--environment|-e <env>]
  regression-test [--endpoint-domain-name|-d <endpoint>] [--environment|-e <env>]

Example
  smoke-test
    --endpoint-domain-name=localhost
    --environment=qa
    --base-path=/provider-directory
    --ENDPOINT_ID=testva.providerone
    --ENDPOINT_NAME=TestVA

$1
EOF
exit 1
}

doCurl () {
  if [[ -n "$2" ]]
  then
    REQUEST_URL="$ENDPOINT_DOMAIN_NAME$BASE_PATH${path// /%20}"
    status_code=$(curl -k -H "Authorization: Bearer $2" --write-out %{http_code} --silent --output /dev/null "$REQUEST_URL")
  else
    REQUEST_URL="$ENDPOINT_DOMAIN_NAME$BASE_PATH${path// /%20}"
    status_code=$(curl -k --write-out %{http_code} --silent --output /dev/null "$REQUEST_URL")
  fi

  if [[ "$status_code" == $1 ]]
  then
    SUCCESS=$((SUCCESS + 1))
    echo "$REQUEST_URL: $status_code - Success"
  else
    FAILURE=$((FAILURE + 1))
    echo "$REQUEST_URL: $status_code - Fail"
  fi
}

smokeTest() {
  if [[ ! "$ENDPOINT_DOMAIN_NAME" == http* ]]; then
    ENDPOINT_DOMAIN_NAME="https://$ENDPOINT_DOMAIN_NAME"
  fi

  for path in "${PATHS[@]}"
    do
      doCurl 200
    done

  path="/Endpoint?identifier=$ENDPOINT_ID"
  doCurl 200 $TOKEN

  printResults
}

regressionTest() {
  if [[ ! "$ENDPOINT_DOMAIN_NAME" == http* ]]; then
    ENDPOINT_DOMAIN_NAME="https://$ENDPOINT_DOMAIN_NAME"
  fi

  for path in "${PATHS[@]}"
    do
      doCurl 200
    done

  path="/Endpoint?identifier=$ENDPOINT_ID"
  doCurl 200 $TOKEN

  path="/Endpoint?name=$ENDPOINT_NAME"
  doCurl 200 $TOKEN

  printResults
}

printResults () {
  TOTAL=$((SUCCESS + FAILURE))
  echo "=== TOTAL: $TOTAL | SUCCESS: $SUCCESS | FAILURE: $FAILURE ==="
  if [[ "$FAILURE" -gt 0 ]]; then
  exit 1
  fi
}

# Let's get down to business
ARGS=$(getopt -n $(basename ${0}) \
    -l "endpoint-domain-name:,environment:,token:,base-path:,endpointid:,endpointname:,help" \
    -o "d:e:t:b:ei:en:h" -- "$@")
[ $? != 0 ] && usage
eval set -- "$ARGS"
while true
do
  case "$1" in
    -d|--endpoint-domain-name) ENDPOINT_DOMAIN_NAME=$2;;
    -e|--environment) ENVIRONMENT=$2;;
    -t|--token) TOKEN=$2;;
    -b|--base-path) BASE_PATH=$2;;
    -ei|--endpointid) ENDPOINT_ID=$2;;
    -en|--endpointname) ENDPOINT_NAME=$2;;
    -h|--help) usage "I need a hero! I'm holding out for a hero...";;
    --) shift;break;;
  esac
  shift;
done

if [[ -z "$ENDPOINT_DOMAIN_NAME" || -e "$ENDPOINT_DOMAIN_NAME" ]]; then
  usage "Missing variable K8S_LOAD_BALANCER or option --endpoint-domain-name|-d."
fi

if [[ -z "$ENVIRONMENT" || -e "$ENVIRONMENT" ]]; then
  usage "Missing variable K8S_ENVIRONMENT or option --environment|-e."
fi

if [[ -z "$TOKEN" || -e "$TOKEN" ]]; then
  usage "Missing variable TOKEN or option --token|-t."
fi

if [[ -z "$ENDPOINT_ID" || -e "$ENDPOINT_ID" ]]; then
  usage "Missing variable ENDPOINT_ID or option --endpointid|-ei."
fi

if [[ -z "$ENDPOINT_NAME" || -e "$ENDPOINT_NAME" ]]; then
  usage "Missing variable ENDPOINT_NAME or option --endpointname|-en."
fi

[ $# == 0 ] && usage "No command specified"
COMMAND=$1
shift

case "$COMMAND" in
  s|smoke-test) smokeTest;;
  r|regression-test) regressionTest;;
  *) usage "Unknown command: $COMMAND";;
esac

exit 0
