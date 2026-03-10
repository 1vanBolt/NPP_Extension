#!/usr/bin/env bash
set -euo pipefail

# Manual source compilation without Gradle.
# Requires Forge/FML dependency jars in ./libs

if ! command -v javac >/dev/null 2>&1; then
  echo "[ERROR] javac not found in PATH"
  exit 1
fi

JAVA_MAJOR="$(java -version 2>&1 | awk -F '[\".]' '/version/ {print $2}')"
if [[ "$JAVA_MAJOR" != "1" && "$JAVA_MAJOR" != "8" ]]; then
  echo "[ERROR] Java 8 is required for Forge 1.7.10"
  exit 1
fi

if [[ ! -d libs ]]; then
  echo "[ERROR] Missing ./libs folder with Forge/FML jars"
  exit 1
fi

CLASSPATH=""
while IFS= read -r -d '' jar; do
  if [[ -z "$CLASSPATH" ]]; then
    CLASSPATH="$jar"
  else
    CLASSPATH="$CLASSPATH:$jar"
  fi
done < <(find libs -maxdepth 1 -type f -name '*.jar' -print0)

if [[ -z "$CLASSPATH" ]]; then
  echo "[ERROR] No jars found in ./libs"
  exit 1
fi

mkdir -p build/classes/main
find src/main/java -name '*.java' > build/sources.list

javac -encoding UTF-8 -source 1.8 -target 1.8 -cp "$CLASSPATH" -d build/classes/main @build/sources.list

echo "[OK] Classes compiled to build/classes/main"
