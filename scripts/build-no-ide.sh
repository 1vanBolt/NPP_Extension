#!/usr/bin/env bash
set -euo pipefail

REQUIRED_GRADLE="2.14.1"
PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
LOCAL_GRADLE="$PROJECT_DIR/.gradle-bin/gradle-${REQUIRED_GRADLE}/bin/gradle"

if ! command -v java >/dev/null 2>&1; then
  echo "[ERROR] java не найдена в PATH"
  exit 1
fi

if ! command -v javac >/dev/null 2>&1; then
  echo "[ERROR] javac не найдена в PATH"
  exit 1
fi

JAVA_VERSION_RAW="$(java -version 2>&1 | head -n 1)"
JAVAC_VERSION_RAW="$(javac -version 2>&1)"
JAVA_MAJOR="$(java -version 2>&1 | awk -F '[\".]' '/version/ {print $2}')"

echo "[INFO] $JAVA_VERSION_RAW"
echo "[INFO] $JAVAC_VERSION_RAW"

if [[ "$JAVA_MAJOR" != "1" && "$JAVA_MAJOR" != "8" ]]; then
  echo "[ERROR] Для Forge 1.7.10 нужен Java 8."
  echo "Подсказка: export JAVA_HOME=/path/to/jdk8 && export PATH=\"$JAVA_HOME/bin:$PATH\""
  exit 1
fi

if [[ -x "$LOCAL_GRADLE" ]]; then
  GRADLE_CMD="$LOCAL_GRADLE"
else
  if ! command -v gradle >/dev/null 2>&1; then
    echo "[ERROR] gradle не найден."
    echo "Запусти ./scripts/use-gradle-2.14.1.sh чтобы скачать совместимую версию."
    exit 1
  fi

  INSTALLED_GRADLE="$(gradle -v | awk '/Gradle / {print $2; exit}')"
  if [[ "$INSTALLED_GRADLE" != "$REQUIRED_GRADLE" ]]; then
    echo "[ERROR] Нужен Gradle $REQUIRED_GRADLE, а найден $INSTALLED_GRADLE."
    echo "Запусти ./scripts/use-gradle-2.14.1.sh (скачает локально в .gradle-bin)."
    exit 1
  fi
  GRADLE_CMD="gradle"
fi

echo "[INFO] Using Gradle: $($GRADLE_CMD -v | awk '/Gradle / {print $2; exit}')"
echo "[INFO] Очистка и сборка..."
"$GRADLE_CMD" clean build

echo "[OK] Готово: build/libs/npp-extension-1.0.0.jar"
