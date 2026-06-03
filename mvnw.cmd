@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM
@REM This script downloads and runs Maven if not already cached.
@REM Requires PowerShell for downloading.
@REM ----------------------------------------------------------------------------

@SET __MVNW_ERROR__=

@REM Read properties
@FOR /F "usebackq tokens=1* delims==" %%A IN ("%~dp0\.mvn\wrapper\maven-wrapper.properties") DO @(
    IF "%%~A"=="distributionUrl" SET "MVNW_distributionUrl=%%~B"
)

@REM Calculate Maven home path
@SET "MAVEN_USER_HOME=%USERPROFILE%\.m2"
@SET "MVNW_distributionUrlName=%MVNW_distributionUrl%"
@FOR %%I IN ("%MVNW_distributionUrl%") DO @SET "MVNW_distributionUrlName=%%~nI"
@SET "MVNW_distributionUrlNameMain=%MVNW_distributionUrlName:.zip=%"
@SET "MVNW_distributionUrlNameMain=%MVNW_distributionUrlNameMain:.tar.gz=%"
@SET "MAVEN_HOME=%MAVEN_USER_HOME%\wrapper\dists\%MVNW_distributionUrlNameMain%"
@SET "MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd"

@REM Check if Maven is already downloaded
@IF EXIST "%MAVEN_CMD%" goto :run_maven

@REM Download Maven
@ECHO Downloading Maven from %MVNW_distributionUrl% ...
@IF NOT EXIST "%MAVEN_HOME%" @MKDIR "%MAVEN_HOME%"
@SET "MVNW_TMP=%TEMP%\mvnw-%RANDOM%"
@IF NOT EXIST "%MVNW_TMP%" @MKDIR "%MVNW_TMP%"

@powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; " ^
    "$zip = '%MVNW_TMP%\maven.zip'; " ^
    "(New-Object System.Net.WebClient).DownloadFile('%MVNW_distributionUrl%', $zip); " ^
    "Expand-Archive -Path $zip -DestinationPath '%MVNW_TMP%\extracted' -Force; " ^
    "$d = Get-ChildItem '%MVNW_TMP%\extracted' -Directory | Select-Object -First 1; " ^
    "Copy-Item -Path \"$($d.FullName)\*\" -Destination '%MAVEN_HOME%' -Recurse -Force; " ^
    "Remove-Item -Recurse -Force '%MVNW_TMP%'"

@IF NOT EXIST "%MAVEN_CMD%" (
    @ECHO.
    @ECHO ERROR: Maven download/extraction failed.
    @ECHO Please download Maven manually from:
    @ECHO   %MVNW_distributionUrl%
    @ECHO Extract it to: %MAVEN_HOME%
    @ECHO.
    @SET __MVNW_ERROR__=1
    @GOTO :mvnw_end
)

:run_maven
@"%MAVEN_CMD%" %*

:mvnw_end
@IF %__MVNW_ERROR__% NEQ 0 @EXIT /B 1
