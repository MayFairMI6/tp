#!/bin/bash

# Make sure we're in the correct directory by checking for gradlew
if [[ ! -f "./gradlew" && ! command -v gradle &> /dev/null ]]; then
    echo "Gradle wrapper (gradlew) or Gradle is not found. Please install Gradle or navigate to the correct directory."
    exit 1
fi

# Compile and build the project
if [[ -f "./gradlew" ]]; then
    ./gradlew clean compileJava shadowJar
else
    gradle clean compileJava shadowJar
fi

# Check if dos2unix is installed and convert files if they exist
if command -v dos2unix >/dev/null 2>&1; then
    [[ -f "EXPECTED-UNIX.TXT" ]] && dos2unix EXPECTED-UNIX.TXT || echo "EXPECTED-UNIX.TXT not found. Skipping."
    [[ -f "ACTUAL.TXT" ]] && dos2unix ACTUAL.TXT || echo "ACTUAL.TXT not found. Skipping."
else
    echo "dos2unix command not found. Skipping line-ending conversion."
fi

# Check if EXPECTED-UNIX.TXT exists before running sed
if [[ -f "EXPECTED-UNIX.TXT" ]]; then
    if [[ "$OSTYPE" == "darwin"* ]]; then
        sed -i '' 's/EXPECTED/ACTUAL/g' EXPECTED-UNIX.TXT
    else
        sed -i 's/EXPECTED/ACTUAL/g' EXPECTED-UNIX.TXT
    fi
else
    echo "EXPECTED-UNIX.TXT not found. Cannot perform sed operation."
fi

# Run the tests
if [[ -f "./runtest.sh" ]]; then
    ./runtest.sh
else
    echo "runtest.sh script not found."
    exit 1
fi
