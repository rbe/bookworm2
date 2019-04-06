FROM alpine:3.9

ENV VAULT_VERSION 1.1.0

RUN apk --no-cache add --virtual .deps \
      bash \
      ca-certificates \
      curl \
    && curl -Lo /tmp/vault.zip \
         https://releases.hashicorp.com/vault/${VAULT_VERSION}/vault_${VAULT_VERSION}_linux_amd64.zip \
    && mkdir /vault \
    && mkdir /vault/config \
    && mkdir /vault/policies \
    && mkdir /vault/data \
    && mkdir /vault/logs \
    && unzip /tmp/vault.zip -d /vault \
    && rm /tmp/vault.zip \
    && chmod 555 /vault

ENV PATH="PATH=$PATH:$PWD/vault"

WORKDIR /vault
COPY vault/config/vault-config.json config/vault-config.json

VOLUME ["/vault"]

EXPOSE 8200

ENTRYPOINT ["vault"]
