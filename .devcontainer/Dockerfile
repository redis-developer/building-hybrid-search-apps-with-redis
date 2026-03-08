FROM mcr.microsoft.com/devcontainers/java:1-21-bookworm

ARG RIOT_VERSION=4.3.0

RUN export DEBIAN_FRONTEND=noninteractive \
    && rm -f /etc/apt/sources.list.d/yarn.list \
    && apt-get update \
    && apt-get install -y nodejs npm maven curl git unzip \
    && ARCH="$(dpkg --print-architecture)" \
    && if [ "$ARCH" = "amd64" ]; then RIOT_ARCH="x86_64"; \
       elif [ "$ARCH" = "arm64" ]; then RIOT_ARCH="aarch64"; \
       else echo "Unsupported architecture: $ARCH" && exit 1; fi \
    && curl -fsSL "https://github.com/redis/riot/releases/download/v${RIOT_VERSION}/riot-standalone-${RIOT_VERSION}-linux-${RIOT_ARCH}.zip" -o /tmp/riot.zip \
    && unzip -q /tmp/riot.zip -d /opt \
    && ln -sf "/opt/riot-standalone-${RIOT_VERSION}-linux-${RIOT_ARCH}/bin/riot" /usr/local/bin/riot \
    && mkdir -p /home/vscode/.m2/repository /home/vscode/.npm \
    && chown -R vscode:vscode /home/vscode/.m2 /home/vscode/.npm \
    && rm -f /tmp/riot.zip \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && echo "alias ll='ls -la'" >> /etc/bash.bashrc
