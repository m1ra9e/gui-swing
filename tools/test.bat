rem Build and test (removes temporary project files before and after tests)

cls

@echo off

chcp 1251

set "LANG=ru_RU.UTF-8"
set "LANGUAGE=ru"
set "LC_CTYPE=ru_RU.UTF-8"

set "TZ=UTC"

set "CURRENT_DIR=%~dp0"
set "SCRIPT=%0"
set "FUNCTION=%CURRENT_DIR%common_functions.bat"

call "%FUNCTION%" fnc_log "Started: %SCRIPT%"

set "PROJ_DIR=%CURRENT_DIR%.."
set "POM_FILE=%PROJ_DIR%\pom.xml"

call "%FUNCTION%" fnc_clean_temp_files                      &
call "%FUNCTION%" fnc_log "temp files removed successfully" &
call mvn -f "%POM_FILE%" clean test                         &
call "%FUNCTION%" fnc_log "maven done successfully"         &
call "%FUNCTION%" fnc_clean_temp_files                      &
call "%FUNCTION%" fnc_log "temp files removed successfully" &
pause                                                       &
exit 0

call "%FUNCTION%" fnc_log "maven error" &
pause                                   &
exit 1
