#!/usr/bin/env bash

ENDPOINT_DOMAIN_NAME="$K8S_LOAD_BALANCER"
ENVIRONMENT="$K8S_ENVIRONMENT"
TOKEN="$TOKEN"
BASE_PATH="$BASE_PATH"
IDENTIFIER="$IDENTIFIER"
NAME="$NAME"
GIVEN="$GIVEN"


#Put Health endpoints here if you got them
PATHS=(/actuator/health \
/openapi.json \
/openapi.yaml)

SUCCESS=0

FAILURE=0

# New phone who this?
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
    --IDENTIFIER=1285621557
    --GIVEN=Michael
    --NAME=Klingerman

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

  # Happy Path Practitioner
  path="/Practitioner?identifier=$IDENTIFIER"
  doCurl 200 $TOKEN

  # Happy Path PractitionerRole
  path="/PractitionerRole?identifier=$IDENTIFIER"
  doCurl 200 $TOKEN

  path="/Location?identifier=$IDENTIFIER"
  doCurl 200 $TOKEN

  # Single unknown parameter check for smoke test
  path="/practitioner?id=$IDENTIFIER"
  doCurl 500 $TOKEN

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

  # Happy Path Practitioner by identifier
  path="/Practitioner?identifier=$IDENTIFIER"
  doCurl 200

  # Happy Path Practitioner by family and given
  path="/Practitioner?family=$NAME&given=$GIVEN"
  doCurl 200 $TOKEN

  # Happy Path PractitionerRole by identifier
  path="/PractitionerRole?identifier=$IDENTIFIER"
  doCurl 200

  # Happy Path PractitionerRole by family and given
  path="/PractitionerRole?family=$NAME&given=$GIVEN"
  doCurl 200 $TOKEN

  # Happy Path Location by name
  path="/Location?name=$NAME"
  doCurl 200 $TOKEN

  # Happy Path Location by identifier
  path="/Location?identifier=$IDENTIFIER"
  doCurl 200


  # Happy Path Location by address-city
  path="/Location?address-city=Melbourne"
  doCurl 200 $TOKEN

  # Happy Path Location by address-postalcode
  path="/Location?address-postalcode=32937"
  doCurl 200 $TOKEN

  # Happy Path Location by address-state
  path="/Location?address-state=Florida"
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
    -l "endpoint-domain-name:,environment:,token:,base-path:,name:,given:,identifier:,help" \
    -o "d:e:t:b:v:p:h" -- "$@")
[ $? != 0 ] && usage
eval set -- "$ARGS"
while true
do
  case "$1" in
    -d|--endpoint-domain-name) ENDPOINT_DOMAIN_NAME=$2;;
    -e|--environment) ENVIRONMENT=$2;;
    -t|--token) TOKEN=$2;;
    -b|--base-path) BASE_PATH=$2;;
    -n|--name) NAME=$2;;
    -g|--given) GIVEN=$2;;
    -i|--identifier) IDENTIFIER=$2;;
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

if [[ -z "$NAME" || -e "$NAME" ]]; then
  usage "Missing variable NAME or option --name|-n."
fi

if [[ -z "$GIVEN" || -e "$GIVEN" ]]; then
  usage "Missing variable GIVEN or option --given|-g."
fi

if [[ -z "$TOKEN" || -e "$TOKEN" ]]; then
  usage "Missing variable TOKEN or option --token|-t."
fi

if [[ -z "$IDENTIFIER" || -e "$IDENTIFIER" ]]; then
  usage "Missing variable IDENTIFIER or option --identifier|-i."
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
