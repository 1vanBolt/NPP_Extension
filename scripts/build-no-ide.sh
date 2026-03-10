#!/usr/bin/env bash
set -euo pipefail

if ! command -v java >/dev/null 2>&1; then
  echo "[ERROR] java не найдена в PATH"
  exit 1
fi

if ! command -v javac >/dev/null 2>&1; then
  echo "[ERROR] javac не найдена в PATH"
  exit 1
fi

if ! command -v gradle >/dev/null 2>&1; then
  echo "[ERROR] gradle не найден в PATH"
  echo "Установите Gradle 6.x и попробуйте снова."
  exit 1
fi

JAVA_VERSION_RAW="$(java -version 2>&1 | head -n 1)"
JAVAC_VERSION_RAW="$(javac -version 2>&1)"

echo "[INFO] $JAVA_VERSION_RAW"
echo "[INFO] $JAVAC_VERSION_RAW"

JAVA_MAJOR="$(java -version 2>&1 | awk -F '[\".]' '/version/ {print $2}')"
if [[ "$JAVA_MAJOR" != "1" && "$JAVA_MAJOR" != "8" ]]; then
  echo "[ERROR] Для Forge 1.7.10 нужен Java 8."
  echo "Подсказка: export JAVA_HOME=/path/to/jdk8 && export PATH=\"$JAVA_HOME/bin:$PATH\""
  exit 1
fi

echo "[INFO] Очистка и сборка..."
gradle clean build

echo "[OK] Готово: build/libs/npp-extension-1.0.0.jar"
