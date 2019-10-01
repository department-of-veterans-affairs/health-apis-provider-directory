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
 - PPMS_URL
 - VLER_PRV_KEY
 - VLER_PUB_KEY
 - VLER_URL

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
[ -z "$PPMS_URL" ] && echo "Missing configuration: PPMS_URL" && MISSING_SECRETS=true
[ -z "$VLER_PRV_KEY" ] && echo "Missing configuration: VLER_PRV_KEY" && MISSING_SECRETS=true
[ -z "$VLER_PUB_KEY" ] && echo "Missing configuration: VLER_PUB_KEY" && MISSING_SECRETS=true
[ -z "$VLER_URL" ] && echo "Missing configuration: VLER_URL" && MISSING_SECRETS=true
[ $MISSING_SECRETS == true ] && usage "Missing configuration secrets, please update $SECRETS"

makeConfig() {
  local project="$1"
  local profile="$2"
  local target="$REPO/$project/config/application-${profile}.properties"
  [ -f "$target" ] && mv -v $target $target.$MARKER
  grep -E '(.*= *unset)' "$REPO/$project/src/main/resources/application.properties" \
    > "$target"
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

configValue provider-directory $PROFILE capability.contact.name "$(whoDis)"
configValue provider-directory $PROFILE capability.contact.email "$(sendMoarSpams)"
configValue provider-directory $PROFILE capability.security.token-endpoint https://fake.com/token
configValue provider-directory $PROFILE capability.security.authorize-endpoint https://fake.com/authorize
configValue provider-directory $PROFILE ppms.url "$PPMS_URL"
configValue provider-directory $PROFILE vler.prvkey "$VLER_PRV_KEY"
configValue provider-directory $PROFILE vler.pubkey "$VLER_PUB_KEY"
configValue provider-directory $PROFILE vler.url "$VLER_URL"
configValue provider-directory $PROFILE provider-directory.url http://localhost:8080
configValue provider-directory $PROFILE well-known.capabilities "context-standalone-patient, launch-ehr, permission-offline, permission-patient"
configValue provider-directory $PROFILE well-known.response-type-supported "code, refresh_token"
configValue provider-directory $PROFILE well-known.scopes-supported "patient/DiagnosticReport.read, patient/Patient.read, offline_access"

checkForUnsetValues provider-directory $PROFILE
