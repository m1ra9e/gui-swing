rem Build and run (without tests)

rem just skip tests execution; this also gives some warnings about what versions you can use each flag with
rem mvn clean package -DskipTests

rem this skip compilation tests as well as execution
rem mvn clean package -Dmaven.test.skip=true
rem mvn clean package -Dmaven.test.skip

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
call mvn -f "%POM_FILE%" clean package -DskipTests          &
call "%FUNCTION%" fnc_log "maven done successfully"

set "TARGET_DIR=%PROJ_DIR%\target"
set "JAR_FILE=%TARGET_DIR%\vehicle-*.jar"

rem getting the full name of jar-file
for %%f in ("%JAR_FILE%") do (
    set "JAR_FILE=%TARGET_DIR%\%%~nxf"
)

set "JAVA_OPTS=-Xmx1024m"
set "JAVA_OPTS=%JAVA_OPTS% -Xms256m"
set "JAVA_OPTS=%JAVA_OPTS% -XX:-OmitStackTraceInFastThrow"

set "JAVA_CMD=java %JAVA_OPTS% -jar ^"%JAR_FILE%^""

%JAVA_CMD%                                                  &
call "%FUNCTION%" fnc_log "%JAR_FILE% started successfully" &
call "%FUNCTION%" fnc_clean_temp_files                      &
call "%FUNCTION%" fnc_log "temp files removed successfully" &
pause                                                       &
exit 0

call "%FUNCTION%" fnc_log "run of %JAR_FILE% failed" &
pause                                                &
exit 1
