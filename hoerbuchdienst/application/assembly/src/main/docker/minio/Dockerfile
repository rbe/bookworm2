ARG MINIO_RELEASE
FROM minio/minio:${MINIO_RELEASE} AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"
ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8

FROM base
RUN mkdir /var/local/minio
RUN apk add pwgen \
    && pwgen -BCn 20 1 >/var/local/minio/access_key \
    && pwgen -BCn 40 1 >/var/local/minio/secret_key
VOLUME ["/var/local/minio", "/miniodata"]
COPY docker-entrypoint.sh /usr/local/bin
RUN chmod +x /usr/local/bin/docker-entrypoint.sh
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
CMD ["server", "--address=:9000", "/miniodata"]
