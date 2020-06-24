ARG NGINX_RELEASE
FROM nginx:${NGINX_RELEASE} AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"
ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
RUN apk update \
    && apk --no-cache add \
        tzdata \
        curl \
        nginx-mod-http-headers-more \
        openssl \
        certbot-nginx \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" >/etc/timezone \
    && rm /etc/nginx/conf.d/default.conf* \
    && adduser -u 82 -D -S -G www-data www-data \
    && echo "0 0 * * WED,SUN certbot renew" | crontab -
RUN apk update \
    && apk add git \
    && apk add make \
    && apk add openssl \
    && apk add python3 \
    && apk add pwgen
RUN git clone https://github.com/michaelklishin/tls-gen /usr/local/tls-gen
COPY selfsigned-*.sh /usr/local/bin/
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/*.sh
RUN mkdir /var/local/rproxy
VOLUME ["/etc/nginx", "/etc/letsencrypt", "/var/local/rproxy"]
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]

FROM base
COPY nginx.conf /etc/nginx/
COPY default_tls_server.conf.disabled /etc/nginx/conf.d/
COPY tlsredirect.conf /etc/nginx/conf.d/
COPY *.conf.disabled /etc/nginx/conf.d/
COPY index.html /var/local/rproxy/
