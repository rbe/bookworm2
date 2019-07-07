FROM nginx:1.14.2-alpine
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"

ARG ENV_NAME

ENV TZ Europe/Berlin

RUN apk update \
    && apk --no-cache add \
        tzdata \
        curl \
        nginx-mod-http-headers-more \
        openssl \
        certbot-nginx \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" >/etc/timezone \
    && apk del tzdata \
    && rm /etc/nginx/conf.d/default.conf* \
    && adduser -u 82 -D -S -G www-data www-data

ADD docker/rproxy/.version /

COPY docker/rproxy/${ENV_NAME}/nginx.conf /etc/nginx
COPY docker/rproxy/${ENV_NAME}/wbhonline.conf /etc/nginx/conf.d/wbhonline.conf
COPY docker/rproxy/${ENV_NAME}/index.html /usr/share/nginx/html
COPY docker/rproxy-certbot/nginx-tls.sh /
RUN chmod a+rx /nginx-tls.sh \
    && /nginx-tls.sh self-signed

VOLUME ["/etc/letsencrypt", "/etc/nginx", "/usr/share/nginx/html"]

CMD ["nginx"]
