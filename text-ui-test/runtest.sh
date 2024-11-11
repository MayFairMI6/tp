#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Function to print error messages
error_exit() {
    echo "Error: $1"
    exit 1
}

# 1. Check for Gradle Wrapper or Gradle Installation
if [[ -f "./gradlew" ]]; then
    GRADLE_CMD="./gradlew"
elif command -v gradle >/dev/null 2>&1; then
    GRADLE_CMD="gradle"
else
    error_exit "Gradle wrapper (gradlew) not found and Gradle is not installed. Please install Gradle or add gradlew to your project."
fi

echo "Using Gradle command: $GRADLE_CMD"

# 2. Clean and Build the Project
echo "Cleaning and building the project..."
$GRADLE_CMD clean compileJava shadowJar || error_exit "Gradle build failed."

# 3. Convert Line Endings to Unix Format
if command -v dos2unix >/dev/null 2>&1; then
    echo "Converting line endings to Unix format..."
    for file in EXPECTED-UNIX.TXT ACTUAL.TXT; do
        if [[ -f "$file" ]]; then
            dos2unix "$file" || error_exit "Failed to convert $file to Unix format."
        else
            echo "Warning: $file not found. Skipping conversion."
        fi
    done
else
    echo "dos2unix not found. Skipping line-ending conversion."
fi

# 4. Run the Application or Test to Generate ACTUAL.TXT
# Replace the following line with the actual command to run your application/tests.
# For example, if your JAR is named app.jar and takes input from a file or arguments:
# java -jar build/libs/app.jar < input.txt > ACTUAL.TXT

# Example placeholder command (modify as needed):
echo "Running the application to generate ACTUAL.TXT..."
java -jar build/libs/your-app.jar > ACTUAL.TXT || error_exit "Failed to run the application."

# 5. Compare ACTUAL.TXT with EXPECTED-UNIX.TXT
if [[ -f "EXPECTED-UNIX.TXT" && -f "ACTUAL.TXT" ]]; then
    echo "Comparing ACTUAL.TXT with EXPECTED-UNIX.TXT..."
    diff -u EXPECTED-UNIX.TXT ACTUAL.TXT > diff_output.txt
    if [[ $? -eq 0 ]]; then
        echo "Test passed! ACTUAL.TXT matches EXPECTED-UNIX.TXT."
        rm diff_output.txt
        exit 0
    else
        echo "Test failed! Differences found:"
        cat diff_output.txt
        exit 1
    fi
else
    error_exit "One or both of EXPECTED-UNIX.TXT and ACTUAL.TXT are missing."
fi

