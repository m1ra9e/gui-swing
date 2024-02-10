rem Removes temporary project files from the project directory: logs, *.db, *.properties, *.log. 

@echo off

chcp 1251

set "CURRENT_DIR=%~dp0"
set "SCRIPT=%0"
set "FUNCTION=%CURRENT_DIR%common_functions.bat"

set "PROJ_DIR=%CURRENT_DIR%.."
set "LOGS_DIR=%PROJ_DIR%\logs"
set "DB_FILE=%PROJ_DIR%\*.db"
set "PROPERTIES_FILE=%PROJ_DIR%\*.properties"
set "LOG_FILE=%PROJ_DIR%\*.log"

set "TOOLS_DIR=%PROJ_DIR%\tools"
set "TOOLS_LOGS_DIR=%TOOLS_DIR%\logs"
set "TOOLS_DB_FILE=%TOOLS_DIR%\*.db"
set "TOOLS_PROPERTIES_FILE=%TOOLS_DIR%\*.properties"
set "TOOLS_LOG_FILE=%TOOLS_DIR%\*.log"

call "%FUNCTION%" fnc_log "Started: %SCRIPT%"

call :fnc_check_and_delete_dir  "%LOGS_DIR%"              &
call :fnc_check_and_delete_file "%DB_FILE%"               &
call :fnc_check_and_delete_file "%PROPERTIES_FILE%"       &
call :fnc_check_and_delete_file "%LOG_FILE%"              &
call :fnc_check_and_delete_dir  "%TOOLS_LOGS_DIR%"        &
call :fnc_check_and_delete_file "%TOOLS_DB_FILE%"         &
call :fnc_check_and_delete_file "%TOOLS_PROPERTIES_FILE%" &
call :fnc_check_and_delete_file "%TOOLS_LOG_FILE%"        &
call "%FUNCTION%" fnc_log "all temporary project files checked for existence and deleted"

call :fnc_check_and_delete_dir  "%TOOLS_LOGS_DIR%"        &
call "%FUNCTION%" fnc_log "all temporary project files checked for existence and deleted"

rem goes to the end of file if this script was launched from another bat-file
if [%1]==[runs_from_another_bat_file] (
    goto :eof
)

rem It pauses and prints "Press any key to continue...". 
pause
exit 0

rem ---f_u_n_c_t_i_o_n_s---

:fnc_check_and_delete_dir
    rem checks for directory existence and deletes if it exists
    if exist "%~1\" (
        rmdir /s /q "%~1"
        call "%FUNCTION%" fnc_log "removed: %~1"
    ) else (
        call "%FUNCTION%" fnc_log "didn't exist: %~1"
    )
    exit /B 0

:fnc_check_and_delete_file
    rem checks for file existence and deletes if it exists
    if exist "%~1" (
        rem it is file
        del /q /f "%~1"
        call "%FUNCTION%" fnc_log "removed: %~1"
    ) else (
        call "%FUNCTION%" fnc_log "didn't exist: %~1"
    )
    exit /B 0
