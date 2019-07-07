FROM alpine:3.9
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"

ENV TZ Europe/Berlin

RUN apk update \
    && apk --no-cache add \
        tzdata \
        openssl \
        certbot-nginx \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" >/etc/timezone \
    && apk del tzdata

ADD docker/rproxy-certbot/.version /

COPY docker/rproxy-certbot/nginx-tls.sh /
COPY docker/rproxy-certbot/certbot-renew.sh /etc/periodic/monthly/certbot-renew
RUN chmod a+rx /nginx-tls.sh \
    && chmod a+rx /etc/periodic/monthly/certbot-renew
