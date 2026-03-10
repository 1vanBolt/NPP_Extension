#!/usr/bin/env bash
set -euo pipefail

MC_VERSION="1.7.10"
MANIFEST_URL="https://launchermeta.mojang.com/mc/game/version_manifest_v2.json"

if ! command -v python >/dev/null 2>&1; then
  echo "[ERROR] python is required"
  exit 1
fi

if ! command -v curl >/dev/null 2>&1; then
  echo "[ERROR] curl is required"
  exit 1
fi

GRADLE_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
CACHE_BASE="$GRADLE_HOME/caches/minecraft/net/minecraft"

python - <<'PY' "$MANIFEST_URL" "$MC_VERSION" "$CACHE_BASE"
import json
import os
import pathlib
import sys
import urllib.request

manifest_url, mc_version, cache_base = sys.argv[1], sys.argv[2], sys.argv[3]

with urllib.request.urlopen(manifest_url) as r:
    manifest = json.load(r)

version_url = None
for item in manifest.get('versions', []):
    if item.get('id') == mc_version:
        version_url = item.get('url')
        break

if not version_url:
    raise SystemExit(f"[ERROR] Version {mc_version} not found in manifest")

with urllib.request.urlopen(version_url) as r:
    ver = json.load(r)

client = ver['downloads']['client']['url']
server = ver['downloads']['server']['url']

paths = {
    client: [
        f"{cache_base}/minecraft/{mc_version}/minecraft-{mc_version}.jar",
        f"{cache_base}/client/{mc_version}/client-{mc_version}.jar",
    ],
    server: [
        f"{cache_base}/minecraft_server/{mc_version}/minecraft_server-{mc_version}.jar",
        f"{cache_base}/server/{mc_version}/server-{mc_version}.jar",
    ],
}

for url, targets in paths.items():
    data = urllib.request.urlopen(url).read()
    for target in targets:
        p = pathlib.Path(target)
        p.parent.mkdir(parents=True, exist_ok=True)
        p.write_bytes(data)
        print(f"[OK] wrote {p}")
PY
