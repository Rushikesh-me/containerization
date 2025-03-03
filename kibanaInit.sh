#!/bin/sh
echo "Reading token from file..."

TOKEN_FILE="/token/kibana-token.txt"

# Loop until the token file is found
while [ ! -f "$TOKEN_FILE" ]; do
  echo "Token file not found. Retrying in 10 seconds..."
  sleep 10
done

echo "Token file found. Reading token..."


TOKEN=$(cat /token/kibana-token.txt)
if [ -z "$TOKEN" ]; then
  echo "Token not found. Exiting."
  exit 1
fi
echo "Token found. Injecting into kibana.yml..."
KIBANA_YML="/usr/share/kibana/config/kibana.yml"

if grep -q "^elasticsearch.serviceAccountToken:" "$KIBANA_YML"; then
  echo "Replacing existing token in kibana.yml..."
  sed -i "s|^elasticsearch.serviceAccountToken:.*|elasticsearch.serviceAccountToken: \"$TOKEN\"|" "$KIBANA_YML"
else
  echo "Adding token to kibana.yml..."
  echo "elasticsearch.serviceAccountToken: \"$TOKEN\"" >> "$KIBANA_YML"
fi

echo "Starting Kibana..."
exec /usr/share/kibana/bin/kibana
