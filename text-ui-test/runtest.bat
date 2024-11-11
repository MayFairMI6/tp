@echo off
REM Change to script directory
cd /d %~dp0

REM Go to the project root and build the JAR file
cd ..
call gradlew clean shadowJar

REM Go to the test directory
cd text-ui-test

REM Run the JAR with input and save the output
for /f %%i in ('dir /b ..\build\libs\*.jar') do (
    java -jar ..\build\libs\%%i < input.txt > ACTUAL.TXT
    goto :done
)
:done

REM Prepare the expected file
copy /Y EXPECTED.TXT EXPECTED-UNIX.TXT

REM Convert line endings to Unix format using PowerShell
powershell -Command "Get-Content EXPECTED-UNIX.TXT | ForEach-Object {$_ -replace '`r`n', '`n'} | Set-Content EXPECTED-UNIX.TXT"
powershell -Command "Get-Content ACTUAL.TXT | ForEach-Object {$_ -replace '`r`n', '`n'} | Set-Content ACTUAL.TXT"

REM Remove lines with underscores
powershell -Command "Get-Content EXPECTED-UNIX.TXT | Where-Object {$_ -notmatch '^_+$'} | Set-Content EXPECTED-CLEAN.TXT"
powershell -Command "Get-Content ACTUAL.TXT | Where-Object {$_ -notmatch '^_+$'} | Set-Content ACTUAL-CLEAN.TXT"

REM Compare the cleaned files
fc /W EXPECTED-CLEAN.TXT ACTUAL-CLEAN.TXT > NUL
if %errorlevel% equ 0 (
    echo Test passed!
    exit /b 0
) else (
    echo Test failed!
    exit /b 1
)
