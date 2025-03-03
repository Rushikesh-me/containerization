#!/bin/sh
set -x

echo "Installing jq..."
apk add --no-cache jq

echo "Waiting for Elasticsearch..."
until curl -s -u elastic:changeme http://elasticsearch:9200 >/dev/null; do
  sleep 5
done

echo "Generating Kibana Service Account Token..."
RESPONSE=$(curl -s -X POST -u elastic:changeme -H "Content-Type: application/json" \
  http://elasticsearch:9200/_security/service/elastic/kibana/credential/token)

echo "$RESPONSE" | jq -r ".token.value" > /token/kibana-token.txt

echo "Token saved to /token/kibana-token.txt"

exit 0
