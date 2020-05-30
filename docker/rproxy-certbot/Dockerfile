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
    && echo "${TZ}" >/etc/timezone \
    &&
    echo "0 0 * * WED,SUN /nginx-tls.sh renew" | crontab -

ADD .version /

COPY docker/rproxy-certbot/nginx-tls.sh /
RUN chmod 500 /nginx-tls.sh

CMD ["crond", "-f", "-L", "/dev/stdout"]
