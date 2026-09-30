#!/usr/bin/env bash
set -euo pipefail
repo_dir="$(cd "$(dirname "$0")/.." && pwd)"
(cd "$repo_dir" && bash gradlew shadowJar)
test_dir="$(mktemp -d)"
trap 'rm -rf "$test_dir"' EXIT
(cd "$test_dir" && java -jar "$repo_dir/build/libs/spendswift.jar" \
    < "$repo_dir/text-ui-test/input.txt") | tr -d '\r' > "$test_dir/actual.txt"
tr -d '\r' < "$repo_dir/text-ui-test/EXPECTED.TXT" > "$test_dir/expected.txt"
diff -u "$test_dir/expected.txt" "$test_dir/actual.txt"
echo "CLI smoke test passed."
