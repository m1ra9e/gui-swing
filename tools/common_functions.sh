#!/bin/bash

# Contains common functions

#CURRENT_DIR=$(cd "`dirname $0`" && pwd)
CURRENT_DIR="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
CLEAN_TEMP_FILES_SCRIPT=${CURRENT_DIR}/clean_temp_files.sh

DATE_FORMAT='+%Y-%m-%d %H:%M:%S'

function fnc_get_date() {
    # get datetime in given format 
    echo $(date -u "${DATE_FORMAT}") "UTC |"
}

function fnc_log() {
    # logging with datetime
    local ex=$?
    echo -e $(fnc_get_date) "${1}"
}

function fnc_clean_temp_files() {
    # runs script for remove temporary project files
    "${CLEAN_TEMP_FILES_SCRIPT}" runs_from_another_sh_file
}

