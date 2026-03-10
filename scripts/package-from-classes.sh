#!/usr/bin/env bash
set -euo pipefail

OUT_DIR="dist"
TMP_DIR="$OUT_DIR/tmp-jar"
OUT_JAR="$OUT_DIR/npp-extension-1.0.0-manual.jar"

if ! command -v jar >/dev/null 2>&1; then
  echo "[ERROR] tool 'jar' не найден (нужен JDK, не JRE)."
  exit 1
fi

if [[ ! -d build/classes/main ]]; then
  echo "[ERROR] Не найдены скомпилированные классы: build/classes/main"
  echo "Сначала выполни компиляцию (обычно через gradle build)."
  exit 1
fi

mkdir -p "$OUT_DIR"
rm -rf "$TMP_DIR"
mkdir -p "$TMP_DIR"

cp -r build/classes/main/. "$TMP_DIR"/
if [[ -d src/main/resources ]]; then
  cp -r src/main/resources/. "$TMP_DIR"/
fi

(
  cd "$TMP_DIR"
  jar cf "../../$OUT_JAR" .
)

echo "[OK] JAR собран: $OUT_JAR"
