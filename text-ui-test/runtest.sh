#!/bin/bash

# Make the script executable
chmod +x runtest.sh

# Compile and build the project
./gradlew clean compileJava shadowJar

# Convert files to Unix format (works for macOS/Linux, requires dos2unix installed)
if command -v dos2unix >/dev/null 2>&1; then
    dos2unix EXPECTED-UNIX.TXT
    dos2unix ACTUAL.TXT
else
    echo "dos2unix command not found. Skipping line-ending conversion."
fi

# Update EXPECTED-UNIX.TXT content, replacing 'EXPECTED' with 'ACTUAL'
# Cross-platform compatibility for macOS/Linux
if [[ "$OSTYPE" == "darwin"* ]]; then
    # For macOS
    sed -i '' 's/EXPECTED/ACTUAL/g' EXPECTED-UNIX.TXT
else
    # For Linux and other Unix-like systems
    sed -i 's/EXPECTED/ACTUAL/g' EXPECTED-UNIX.TXT
fi

# Alternative cross-platform approach without using sed -i
# sed 's/EXPECTED/ACTUAL/g' EXPECTED-UNIX.TXT > TEMP.TXT && mv TEMP.TXT EXPECTED-UNIX.TXT

# Run the tests and display the output
./runtest.sh

