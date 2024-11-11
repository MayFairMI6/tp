#!/usr/bin/env bash

# Change to script directory
cd "${0%/*}"

# Go to the project root and build the JAR file
cd ..
./gradlew clean shadowJar

# Go to the test directory
cd text-ui-test

# Run the JAR with input and save the output
java -jar $(find ../build/libs/ -mindepth 1 -print -quit) < input.txt > ACTUAL.TXT

# Prepare the expected file and convert line endings
cp EXPECTED.TXT EXPECTED-UNIX.TXT
dos2unix EXPECTED-UNIX.TXT ACTUAL.TXT

# Remove any remaining \r characters, trailing whitespace, and extra blank lines
sed -i 's/\r$//' EXPECTED-UNIX.TXT ACTUAL.TXT
sed -i 's/[[:space:]]\+$//' EXPECTED-UNIX.TXT ACTUAL.TXT
sed -i '/^$/N;/\n$/D' EXPECTED-UNIX.TXT ACTUAL.TXT

# Compare the files with unified diff for better output
diff -u EXPECTED-UNIX.TXT ACTUAL.TXT

# Check the result of diff
if [ $? -eq 0 ]
then
    echo "Test passed!"
    exit 0
else
    echo "Test failed!"
    exit 1
fi
