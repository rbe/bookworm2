ARG MINIO_RELEASE
FROM minio/minio:${MINIO_RELEASE} AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"
ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
RUN mkdir /var/local/minio
RUN apk add --no-cache pwgen \
    && pwgen -BCn 20 1 >/var/local/minio/access_key \
    && pwgen -BCn 40 1 >/var/local/minio/secret_key
COPY docker-entrypoint.sh /usr/local/bin
RUN chmod +x /usr/local/bin/docker-entrypoint.sh
EXPOSE 9000/tcp
VOLUME ["/var/local/minio", "/miniodata"]
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
CMD ["server", "--address=:9000", "/miniodata"]
