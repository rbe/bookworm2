ARG NGINX_RELEASE
FROM nginx:${NGINX_RELEASE} AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"
ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
RUN apk update
RUN apk --no-cache add tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" >/etc/timezone
RUN apk --no-cache add \
        bash \
        curl \
        openssl \
        certbot-nginx \
    && rm /etc/nginx/conf.d/default.conf* \
    && adduser -u 82 -D -S -G www-data www-data \
    && echo "0 0 * * WED,SUN certbot renew" | crontab -
RUN apk update \
    && apk add --no-cache git \
    && apk add --no-cache make \
    && apk add --no-cache openssl \
    && apk add --no-cache python3 \
    && apk add --no-cache pwgen
RUN git clone https://github.com/michaelklishin/tls-gen /usr/local/tls-gen
COPY *.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/*.sh
RUN mkdir /var/local/rproxy
EXPOSE 80/tcp 443/tcp
VOLUME ["/etc/nginx", "/etc/letsencrypt", "/var/local/rproxy"]
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]

FROM base
COPY nginx.conf /etc/nginx/
COPY tlsredirect.conf /etc/nginx/conf.d/
COPY *.conf.disabled /etc/nginx/conf.d/
COPY index.html /var/local/rproxy/
