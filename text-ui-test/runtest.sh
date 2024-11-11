#!/usr/bin/env bash

# Exit immediately if a command exits with a non-zero status
set -e

# Function to display messages
echo_msg() {
    echo "========================================"
    echo "$1"
    echo "========================================"
}

# Function to install dos2unix if not already installed
install_dos2unix() {
    if ! command -v dos2unix &> /dev/null
    then
        echo_msg "dos2unix not found. Installing dos2unix..."
        # Update package lists
        sudo apt-get update -y
        # Install dos2unix
        sudo apt-get install dos2unix -y
        echo_msg "dos2unix installed successfully."
    else
        echo_msg "dos2unix is already installed."
    fi
}

# Change to the directory where the script is located
cd "$(dirname "$0")"

# Install dos2unix
install_dos2unix

# Navigate to the parent directory
cd ..

# Clean previous builds and create a shadow JAR
echo_msg "Cleaning previous builds and creating shadow JAR..."
./gradlew clean shadowJar

# Navigate to the test directory
cd text-ui-test

# Execute the JAR with input and redirect output to ACTUAL.TXT
echo_msg "Running the application and capturing output..."
java -jar "$(find ../build/libs/ -mindepth 1 -print -quit)" < input.txt > ACTUAL.TXT

# Prepare EXPECTED-UNIX.TXT by copying and converting line endings
echo_msg "Preparing expected output file..."
cp EXPECTED.TXT EXPECTED-UNIX.TXT
dos2unix EXPECTED-UNIX.TXT ACTUAL.TXT

# Compare the expected and actual outputs
echo_msg "Comparing expected and actual outputs..."
if diff EXPECTED-UNIX.TXT ACTUAL.TXT > diff_output.txt
then
    echo "Test passed!"
    exit 0
else
    echo "Test failed! Differences:"
    cat diff_output.txt
    exit 1
fi
