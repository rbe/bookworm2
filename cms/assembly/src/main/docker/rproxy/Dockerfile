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
        git \
        openssl \
        certbot-nginx \
    && rm /etc/nginx/conf.d/default.conf* \
    && adduser -u 82 -D -S -G www-data www-data \
    && git clone https://github.com/h5bp/server-configs-nginx.git /etc/nginx/h5bp
COPY selfsigned-*.sh /usr/local/bin/
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/*.sh
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]

FROM base
COPY nginx.conf /etc/nginx
COPY tlsredirect.conf /etc/nginx/conf.d/
COPY *.conf.disabled /etc/nginx/conf.d/
COPY index.html /usr/share/nginx/html
RUN openssl rand 80 >/etc/nginx/ssl_session_ticket.key
VOLUME ["/etc/nginx", "/etc/letsencrypt", "/usr/share/nginx/html"]
