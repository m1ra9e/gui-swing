#!/bin/bash

# Removes temporary project files from the project directory: logs, *.db, *.properties, *.log. 

#CURRENT_DIR=$(cd "`dirname $0`" && pwd)
CURRENT_DIR="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
FUNCTIONS=${CURRENT_DIR}/common_functions.sh

PROJ_DIR=${CURRENT_DIR}/..
LOGS_DIR=${PROJ_DIR}/logs
DB_FILE=${PROJ_DIR}/*.db
PROPERTIES_FILE=${PROJ_DIR}/*.properties
LOG_FILE=${PROJ_DIR}/*.log

TOOLS_LOGS_DIR=${CURRENT_DIR}/logs
TOOLS_DB_FILE=${CURRENT_DIR}/*.db
TOOLS_PROPERTIES_FILE=${CURRENT_DIR}/*.properties
TOOLS_LOG_FILE=${CURRENT_DIR}/*.log

SCRIPT=${CURRENT_DIR}/$(basename "$0")

# import common functions
source "${FUNCTIONS}"

function fnc_check_and_delete_dir() {
    # checks for directory existence and deletes if it exists
    if [ -d "$1" ]; then
        rm -rf "$1"
        fnc_log "removed: $1"
    else
        fnc_log "didn't exist: $1"
    fi
}

function fnc_check_and_delete_file() {
    # checks for file existence and deletes if it exists
    if [ $(find "$1" -maxdepth 1 -wholename "$2" | wc -l) -gt 0 ]; then
        find "$1" -maxdepth 1 -wholename "$2" -delete
        fnc_log "removed: $2"
    else
        fnc_log "didn't exist: $2"
    fi
}

fnc_log "Started: $SCRIPT"

fnc_check_and_delete_file "$PROJ_DIR" "$PROPERTIES_FILE"

fnc_check_and_delete_dir  "$LOGS_DIR"                             &&
fnc_check_and_delete_file "$PROJ_DIR" "$DB_FILE"                  &&
fnc_check_and_delete_file "$PROJ_DIR" "$PROPERTIES_FILE"          &&
fnc_check_and_delete_file "$PROJ_DIR" "$LOG_FILE"                 &&
fnc_check_and_delete_dir  "$TOOLS_LOGS_DIR"                       &&
fnc_check_and_delete_file "$CURRENT_DIR" "$TOOLS_DB_FILE"         &&
fnc_check_and_delete_file "$CURRENT_DIR" "$TOOLS_PROPERTIES_FILE" &&
fnc_check_and_delete_file "$CURRENT_DIR" "$TOOLS_LOG_FILE"        &&
fnc_log "all temporary project files checked for existence and deleted"

# If this script was not launched from another sh-file, then
#   it pauses, prints "press enter to exit" and exit.
if [ -z "$1" ] || [ "$1" != "runs_from_another_sh_file" ]; then
    read -p "press enter to exit..."
    exit 0
fi

