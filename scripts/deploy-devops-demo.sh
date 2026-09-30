#!/usr/bin/env bash
# Installs a new application jar and restarts the service.
# Copy to /usr/local/bin/deploy-devops-demo.sh, owned by root, mode 0755.
# CI users may run ONLY this script with sudo (see scripts/sudoers-devops-demo).
set -euo pipefail

JAR="${1:?usage: deploy-devops-demo.sh /path/to/devops-demo.jar}"

if [[ ! -f "$JAR" ]]; then
  echo "No such file: $JAR" >&2
  exit 1
fi

install -o devops-demo -g devops-demo -m 0644 "$JAR" /opt/devops-demo/app.jar
systemctl restart devops-demo
systemctl is-active --quiet devops-demo
echo "Deployed $(basename "$JAR") and restarted devops-demo"
