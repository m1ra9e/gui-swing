#!/bin/bash

# Build and test (removes temporary project files before and after tests)

clear

export LANG=ru_RU.UTF-8
export LANGUAGE=ru
export LC_CTYPE=ru_RU.UTF-8

# export TZ=UTC

CURRENT_DIR=$(cd "`dirname $0`" && pwd)
SCRIPT=${CURRENT_DIR}/$(basename "$0")
FUNCTIONS=${CURRENT_DIR}/common_functions.sh

# import common functions
source "${FUNCTIONS}"

fnc_log "Started: $SCRIPT"

PROJ_DIR=${CURRENT_DIR}/..
POM_FILE=${PROJ_DIR}/pom.xml

fnc_clean_temp_files                      &&
fnc_log "temp files removed successfully" &&
mvn -f "${POM_FILE}" clean test           &&
fnc_log "maven done successfully"         &&
fnc_clean_temp_files                      &&
fnc_log "temp files removed successfully" &&
read -p "press enter to exit..."          &&
exit 0

fnc_log "maven error"            &&
read -p "press enter to exit..." &&
exit 1

