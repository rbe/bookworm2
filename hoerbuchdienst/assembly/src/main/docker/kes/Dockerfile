ARG KES_RELEASE
FROM minio/kes:${KES_RELEASE} AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"
ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
RUN apk add --no-cache jq
RUN mkdir /var/local/kes
COPY server-config.tmpl.yml /var/local/kes/
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh
EXPOSE 7373/tcp
VOLUME ["/var/local/kes"]
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
