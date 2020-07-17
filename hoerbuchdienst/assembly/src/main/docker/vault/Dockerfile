ARG VAULT_RELEASE
FROM library/vault:${VAULT_RELEASE} AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"
ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
COPY vault-config.json /vault/config/
COPY kes-policy.hcl /vault/config
COPY *.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/*.sh
EXPOSE 8200/tcp
VOLUME ["/vault/config", "/vault/logs", "/vault/file"]
CMD ["/bin/sh", "/usr/local/bin/vault-command.sh"]
