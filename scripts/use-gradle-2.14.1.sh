#!/usr/bin/env bash
set -euo pipefail

VERSION="2.14.1"
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
DEST_DIR="$ROOT_DIR/.gradle-bin"
ZIP_PATH="$DEST_DIR/gradle-${VERSION}-bin.zip"
UNPACK_DIR="$DEST_DIR/gradle-${VERSION}"
URL="https://services.gradle.org/distributions/gradle-${VERSION}-bin.zip"

mkdir -p "$DEST_DIR"

if [[ -x "$UNPACK_DIR/bin/gradle" ]]; then
  echo "[OK] Gradle ${VERSION} уже доступен: $UNPACK_DIR/bin/gradle"
  exit 0
fi

echo "[INFO] Скачивание $URL"
if command -v curl >/dev/null 2>&1; then
  curl -fL "$URL" -o "$ZIP_PATH"
elif command -v wget >/dev/null 2>&1; then
  wget -O "$ZIP_PATH" "$URL"
else
  echo "[ERROR] Нужен curl или wget для скачивания Gradle"
  exit 1
fi

echo "[INFO] Распаковка..."
unzip -q -o "$ZIP_PATH" -d "$DEST_DIR"

echo "[OK] Готово: $UNPACK_DIR/bin/gradle"
echo "[INFO] Теперь можно запустить: ./scripts/build-no-ide.sh"
