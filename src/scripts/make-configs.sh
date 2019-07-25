#! /usr/bin/env bash

usage() {
cat <<EOF

$0 [options]

Generate configurations for local development.

Options
     --debug               Enable debugging
 -h, --help                Print this help and exit.
     --secrets-conf <file> The configuration file with secrets!

Secrets Configuration
 This bash file is sourced and expected to set the following variables
 - KEYSTORE_PASSWORD

$1
EOF
exit 1
}

REPO=$(cd $(dirname $0)/../.. && pwd)
SECRETS="$REPO/secrets.conf"
PROFILE=dev
MARKER=$(date +%s)
ARGS=$(getopt -n $(basename ${0}) \
    -l "debug,help,secrets-conf:" \
    -o "h" -- "$@")
[ $? != 0 ] && usage
eval set -- "$ARGS"
while true
do
  case "$1" in
    --debug) set -x;;
    -h|--help) usage "halp! what this do?";;
    --secrets-conf) SECRETS="$2";;
    --) shift;break;;
  esac
  shift;
done

echo "Loading secrets: $SECRETS"
[ ! -f "$SECRETS" ] && usage "File not found: $SECRETS"
. $SECRETS

MISSING_SECRETS=false
[ -z "$KEYSTORE_PASSWORD" ] && echo "Missing configuration: KEYSTORE_PASSWORD" && MISSING_SECRETS=true
[ $MISSING_SECRETS == true ] && usage "Missing configuration secrets, please update $SECRETS"

makeConfig() {
  local project="$1"
  local profile="$2"
  local target="$REPO/$project/config/application-${profile}.properties"
  [ -f "$target" ] && mv -v $target $target.$MARKER
  grep -E '(.*= *unset)' "$REPO/$project/src/main/resources/application.properties" \
    | grep -Ev '(^server\.ssl\.|^ssl\.)' \
    > "$target"
cat >> "$target" <<EOF
# Server SSL
server.ssl.key-store=file:target/certs/system/DVP-DVP-NONPROD.jks
server.ssl.key-alias=internal-sys-dev
server.ssl.key-store-password=$KEYSTORE_PASSWORD
server.ssl.trust-store=file:target/certs/system/DVP-NONPROD-truststore.jks
server.ssl.trust-store-password=$KEYSTORE_PASSWORD
server.ssl.client-auth=want
# Client SSL
ssl.key-store=file:target/certs/system/DVP-DVP-NONPROD.jks
ssl.key-store-password=$KEYSTORE_PASSWORD
ssl.client-key-password=$KEYSTORE_PASSWORD
ssl.use-trust-store=true
ssl.trust-store=file:target/certs/system/DVP-NONPROD-truststore.jks
ssl.trust-store-password=$KEYSTORE_PASSWORD
EOF
}

configValue() {
  local project="$1"
  local profile="$2"
  local key="$3"
  local value="$4"
  local target="$REPO/$project/config/application-${profile}.properties"
  local escapedValue=$(echo $value | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')
  sed -i "s/^$key=.*/$key=$escapedValue/" $target
}

checkForUnsetValues() {
  local project="$1"
  local profile="$2"
  local target="$REPO/$project/config/application-${profile}.properties"
  echo "checking $target"
  grep -E '(.*= *unset)' "$target"
  [ $? == 0 ] && echo "Failed to populate all unset values" && exit 1
  diff -q $target $target.$MARKER
  [ $? == 0 ] && rm -v $target.$MARKER
}

whoDis() {
  local me=$(git config --global --get user.name)
  [ -z "$me" ] && me=$USER
  echo $me
}

sendMoarSpams() {
  local spam=$(git config --global --get user.email)
  [ -z "$spam" ] && spam=$USER@aol.com
  echo $spam
}

makeConfig provider-directory $PROFILE

configValue provider-directory $PROFILE well-known.capabilities "context-standalone-patient, launch-ehr, permission-offline, permission-patient"
configValue provider-directory $PROFILE well-known.response-type-supported "code, refresh_token"
configValue provider-directory $PROFILE well-known.scopes-supported "patient/DiagnosticReport.read, patient/Patient.read, offline_access"


checkForUnsetValues provider-directory $PROFILE
