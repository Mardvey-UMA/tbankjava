#!/usr/bin/env bash
set -euo pipefail

SR_URL="${SCHEMA_REGISTRY_URL:-http://schema-registry:8081}"
SCHEMA_DIR="${SCHEMA_DIR:-/schemas}"

echo "---> Waiting until Schema Registry ($SR_URL) is up..."
until curl -fsS "$SR_URL/subjects" >/dev/null 2>&1; do
  sleep 1
done
echo "---> Schema Registry reachable at $SR_URL"
echo

declare -A MAPPING=(
  ["WeatherResponseKafkaDTO"]="weather-subscribers"
)

echo "---> Registering local *.avsc → subject according to topic mapping"
for file in "$SCHEMA_DIR"/*.avsc; do
  [ -e "$file" ] || { echo "No .avsc found in $SCHEMA_DIR"; exit 0; }

  base="$(basename "$file" .avsc)"

  if [[ -z "${MAPPING[$base]:-}" ]]; then
    echo "  [WARN] No topic mapping for '$base' → skipping"
    continue
  fi

  topic="${MAPPING[$base]}"
  subj="${topic}-value"

  schema_str=$(jq -Rs . <"$file")

  echo "  → Registering $file as subject '$subj'..."
  curl -sS -X POST \
       -H "Content-Type: application/vnd.schemaregistry.v1+json" \
       --data "{\"schema\":${schema_str}}" \
       "$SR_URL/subjects/$subj/versions" \
    && echo "    [ok] $subj registered" \
    || echo "    [error] failed to register $subj"
done

echo "All done."
