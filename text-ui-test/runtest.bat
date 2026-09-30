@echo off
setlocal
set "REPO_DIR=%~dp0.."
pushd "%REPO_DIR%"
call gradlew.bat shadowJar
if errorlevel 1 exit /b 1
set "JAR_FILE=%CD%\build\libs\spendswift.jar"
set "INPUT_FILE=%CD%\text-ui-test\input.txt"
set "EXPECTED_FILE=%CD%\text-ui-test\EXPECTED.TXT"
set "TEST_DIR=%TEMP%\spendswift-test-%RANDOM%-%RANDOM%"
mkdir "%TEST_DIR%" || exit /b 1
pushd "%TEST_DIR%"
java -jar "%JAR_FILE%" < "%INPUT_FILE%" > actual.txt
if errorlevel 1 (set "RESULT=1") else (
    fc /W actual.txt "%EXPECTED_FILE%" >NUL
    if errorlevel 1 (set "RESULT=1") else (set "RESULT=0")
)
popd
rmdir /s /q "%TEST_DIR%"
popd
if "%RESULT%"=="0" (echo CLI smoke test passed.) else (echo CLI smoke test failed.)
exit /b %RESULT%
