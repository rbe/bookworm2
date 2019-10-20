FROM alpine:3.10
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

ADD docker/rproxy-certbot/.version /

COPY docker/rproxy-certbot/nginx-tls.sh /
COPY docker/rproxy-certbot/certbot-renew.sh /etc/periodic/monthly/certbot-renew
RUN chmod 500 /nginx-tls.sh \
    && chmod 500 /etc/periodic/monthly/certbot-renew

CMD ["crond", "-f", "-L", "/dev/stdout"]
