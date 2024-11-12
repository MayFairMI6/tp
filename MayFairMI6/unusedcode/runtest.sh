#!/usr/bin/env bash

# Change to script directory
cd "${0%/*}"

# Function to install dos2unix depending on the OS
install_dos2unix() {
    case "$(uname -s)" in
       Darwin)
         echo "Mac OS detected. Installing dos2unix using Homebrew..."
         brew install dos2unix
         ;;
       Linux)
         echo "Linux OS detected."
         if command -v apt-get &> /dev/null; then
             echo "Using APT package manager to install dos2unix..."
             sudo apt-get update && sudo apt-get install -y dos2unix
         elif command -v yum &> /dev/null; then
             echo "Using YUM package manager to install dos2unix..."
             sudo yum install -y dos2unix
         elif command -v zypper &> /dev/null; then
             echo "Using Zypper package manager to install dos2unix..."
             sudo zypper install -y dos2unix
         else
             echo "No supported package manager found. Please install dos2unix manually."
             exit 1
         fi
         ;;
       *)
         echo "Unsupported OS. Please install dos2unix manually."
         exit 1
         ;;
    esac
}

# Check for dos2unix and install if not found
if ! command -v dos2unix &> /dev/null; then
    install_dos2unix
fi

cd ..
./gradlew clean shadowJar
cd text-ui-test
java -jar $(find ../build/libs/ -mindepth 1 -print -quit) < input.txt > ACTUAL.TXT
cp EXPECTED.TXT EXPECTED-UNIX.TXT
dos2unix EXPECTED-UNIX.TXT ACTUAL.TXT

# Compare the files, ignoring specific lines
diff <(grep -v '__________________________________________________' EXPECTED-UNIX.TXT) <(grep -v '__________________________________________________' ACTUAL.TXT)
if [ $? -eq 0 ]
then
    echo "Test passed!"
    exit 0
else
    echo "Test failed!"
    exit 1
fi
