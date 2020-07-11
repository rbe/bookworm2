ARG MC_RELEASE
FROM minio/mc:${MC_RELEASE} AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"
ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
RUN apk add pwgen
RUN mkdir -p /var/local/mc/policy
COPY *.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/*.sh
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]

FROM base
COPY policy/*.json /var/local/mc/policy/
