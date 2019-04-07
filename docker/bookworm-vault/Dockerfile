FROM alpine:3.9

ENV VAULT_VERSION 1.1.0
ENV PATH "/opt/vault:$PATH"

RUN apk --no-cache add --virtual .deps \
      libcap \
      bash \
      ca-certificates \
      curl \
    && curl -Lo /tmp/vault.zip \
         https://releases.hashicorp.com/vault/${VAULT_VERSION}/vault_${VAULT_VERSION}_linux_amd64.zip \
    && mkdir /opt/vault \
    && mkdir /opt/vault/config \
    && mkdir /opt/vault/policies \
    && mkdir /opt/vault/logs \
    && unzip /tmp/vault.zip -d /opt/vault \
    && rm /tmp/vault.zip \
    && chmod 555 /opt/vault \
    && mkdir /var/vault \
    && mkdir /var/vault/data \
    && chmod 700 /var/vault \
    && setcap cap_ipc_lock=+ep $(readlink -f /opt/vault/vault)

WORKDIR /opt/vault/config
COPY vault-config.json .

RUN addgroup -S vault \
    && adduser -DG vault vault \
    && chown -R vault:vault /opt/vault \
    && chown -R vault:vault /var/vault

USER vault:vault

WORKDIR /opt/vault

VOLUME ["/opt/vault"]

EXPOSE 8200

ENTRYPOINT ["/opt/vault/vault"]
