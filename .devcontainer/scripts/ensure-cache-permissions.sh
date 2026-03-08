#!/usr/bin/env bash
set -euo pipefail

TARGET_USER="${1:-vscode}"
USER_HOME="$(getent passwd "$TARGET_USER" 2>/dev/null | cut -d: -f6 || true)"
if [ -z "$USER_HOME" ]; then
  USER_HOME="/home/${TARGET_USER}"
fi

M2_DIR="${USER_HOME}/.m2"
NPM_DIR="${USER_HOME}/.npm"

run_as_root() {
  if [ "$(id -u)" -eq 0 ]; then
    "$@"
  elif command -v sudo >/dev/null 2>&1; then
    sudo -n "$@"
  else
    "$@"
  fi
}

run_as_root mkdir -p "${M2_DIR}/repository" "${NPM_DIR}"

user_can_write() {
  if [ "$(id -un)" = "${TARGET_USER}" ]; then
    test -w "${M2_DIR}/repository" && test -w "${NPM_DIR}"
  elif command -v sudo >/dev/null 2>&1; then
    sudo -n -u "${TARGET_USER}" test -w "${M2_DIR}/repository" && sudo -n -u "${TARGET_USER}" test -w "${NPM_DIR}"
  else
    return 1
  fi
}

if ! user_can_write; then
  run_as_root chown -R "${TARGET_USER}:${TARGET_USER}" "${M2_DIR}" "${NPM_DIR}"
fi
