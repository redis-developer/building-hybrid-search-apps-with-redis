#!/bin/sh

set -eu

SEARCH_API_URL="${SEARCH_API:-http://localhost:8081/search}"

# In Codespaces, point the frontend to the forwarded backend URL.
if [ -n "${CODESPACE_NAME:-}" ] && [ -n "${GITHUB_CODESPACES_PORT_FORWARDING_DOMAIN:-}" ]; then
  SEARCH_API_URL="https://${CODESPACE_NAME}-8081.${GITHUB_CODESPACES_PORT_FORWARDING_DOMAIN}/search"
fi

cat << EOF > /usr/share/nginx/html/scripts/apis.js
const searchAPI = "${SEARCH_API_URL}"
EOF
