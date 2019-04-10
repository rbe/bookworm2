FROM alpine:3.9

ENV VAULT_VERSION 1.1.0
ENV PATH "/opt/vault:$PATH"

RUN apk --no-cache add --virtual .deps \
      libcap \
      bash \
      ca-certificates \
      curl \
    && cd /tmp \
    && curl -LO https://releases.hashicorp.com/vault/${VAULT_VERSION}/vault_${VAULT_VERSION}_linux_amd64.zip \
    && curl -LO https://releases.hashicorp.com/vault/${VAULT_VERSION}/vault_${VAULT_VERSION}_SHA256SUMS \
    && curl -LO https://releases.hashicorp.com/vault/${VAULT_VERSION}/vault_${VAULT_VERSION}_SHA256SUMS.sig \
    && grep vault_${VAULT_VERSION}_linux_amd64.zip vault_${VAULT_VERSION}_SHA256SUMS | sha256sum -c \
    && mkdir /opt/vault \
    && mkdir /opt/vault/config \
    && mkdir /opt/vault/policies \
    && mkdir /opt/vault/logs \
    && unzip vault_${VAULT_VERSION}_linux_amd64.zip -d /opt/vault \
    && rm vault_${VAULT_VERSION}_linux_amd64.zip \
    && chmod 500 /opt/vault \
    && chmod 500 /opt/vault/vault \
    && mkdir /var/vault \
    && mkdir /var/vault/data \
    && chmod 700 /var/vault \
    && chmod 700 /var/vault/data \
    && setcap cap_ipc_lock=+ep $(readlink -f $(which vault))

WORKDIR /opt/vault/config
COPY vault-config.json .

RUN addgroup -S vault \
    && adduser -DG vault vault \
    && chown -R vault:vault /opt/vault \
    && chown -R vault:vault /var/vault

#USER vault:vault

WORKDIR /opt/vault

VOLUME ["/opt/vault"]

EXPOSE 8200

ENTRYPOINT ["/opt/vault/vault"]
