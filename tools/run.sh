#!/bin/bash

# Build and run (without tests)

# just skip tests execution; this also gives some warnings about what versions you can use each flag with
# mvn clean package -DskipTests

# this skip compilation tests as well as execution
# mvn clean package -Dmaven.test.skip=true
# mvn clean package -Dmaven.test.skip

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

fnc_clean_temp_files                           &&
fnc_log "temp files removed successfully"      &&
mvn -f "${POM_FILE}" clean package -DskipTests &&
fnc_log "maven done successfully"

TARGET_DIR=${PROJ_DIR}/target
JAR_FILE_TEMPLATE=vehicle-*.jar
JAR_FILE=${TARGET_DIR}/${JAR_FILE_TEMPLATE}

# getting the full name of jar-file
for F in "$TARGET_DIR"/*; do
    BASE_NAME=$(basename "${F}")
    if [[ $BASE_NAME == $JAR_FILE_TEMPLATE ]] ; then
        JAR_FILE=${TARGET_DIR}/${BASE_NAME}
        break
    fi
done

JAVA_OPTS=" -Xmx1024m"
JAVA_OPTS+=" -Xms256m"
JAVA_OPTS+=" -XX:-OmitStackTraceInFastThrow"

## doesn't work because it can't handle spaces in the path:
##   JAVA_CMD="java ${JAVA_OPTS} -jar ""${JAR_FILE}"""
##
## that's why didn't use one of these variants:
##   exec ${JAVA_CMD}                     &&
##   ${JAVA_CMD}                          &&

java ${JAVA_OPTS} -jar "${JAR_FILE}"      &&
fnc_log "$JAR_FILE started successfully"  &&
fnc_clean_temp_files                      &&
fnc_log "temp files removed successfully" &&
read -p "press enter to exit..."          &&
exit 0

fnc_log "run of $JAR_FILE failed" &&
read -p "press enter to exit..."  &&
exit 1

