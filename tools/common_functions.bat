rem Contains common functions

@echo off

set "CURRENT_DIR=%~dp0"
set "CLEAN_TEMP_FILES_SCRIPT=%CURRENT_DIR%clean_temp_files.bat"

set "FUNCTION_NAME=%~1"
set "FUNCTION_ARGUMENT_1=%~2"

call :%FUNCTION_NAME%
goto exit

:fnc_log
    rem logging with datetime in default format 
    echo %date% %time% ^| %FUNCTION_ARGUMENT_1%
    echo.
    goto:eof

:fnc_clean_temp_files
    rem runs script for remove temporary project files
    call "%CLEAN_TEMP_FILES_SCRIPT%" runs_from_another_bat_file 
    goto:eof

:exit
    exit /b
