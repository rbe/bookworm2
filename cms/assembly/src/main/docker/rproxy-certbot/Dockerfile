ARG ALPINE_RELEASE
FROM alpine:${ALPINE_RELEASE} AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"
ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
RUN apk update \
    && apk --no-cache add \
        tzdata \
        openssl \
        certbot-nginx \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" >/etc/timezone

FROM base
COPY nginx-tls.sh /usr/local/bin
RUN chmod 500 /usr/local/bin/nginx-tls.sh \
    && echo "0 0 * * WED,SUN /usr/local/bin/nginx-tls.sh renew" | crontab -
CMD ["crond", "-f", "-L", "/dev/stdout"]
