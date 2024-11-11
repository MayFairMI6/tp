@echo off
cd ..
call gradlew clean shadowJar

cd text-ui-test
java -jar ..\build\libs\*.jar < input.txt > ACTUAL.TXT

REM Convert EXPECTED.TXT and ACTUAL.TXT to Unix line endings
powershell -Command "(Get-Content EXPECTED.TXT) -replace '\r\n', '`n' | Set-Content EXPECTED-UNIX.TXT"
powershell -Command "(Get-Content ACTUAL.TXT) -replace '\r\n', '`n' | Set-Content ACTUAL-UNIX.TXT"

fc /W EXPECTED-UNIX.TXT ACTUAL-UNIX.TXT
if %errorlevel% equ 0 (
    echo Test passed!
    exit /b 0
) else (
    echo Test failed!
    exit /b 1
)
