FROM nginx:1.14-alpine
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"

ARG ENV_NAME
ARG ENV_TYPE

ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8

RUN apk update \
    && apk --no-cache add \
        tzdata \
        curl \
        git \
        nginx-mod-http-headers-more \
        openssl \
        certbot-nginx \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" >/etc/timezone
    && rm /etc/nginx/conf.d/default.conf* \
    && adduser -u 82 -D -S -G www-data www-data \
    && git clone https://github.com/h5bp/server-configs-nginx.git /etc/nginx/h5bp

ADD .version /

COPY docker/rproxy/${ENV_NAME}/nginx.conf /etc/nginx
COPY docker/rproxy/${ENV_NAME}/wbhonline.conf /etc/nginx/conf.d/
COPY docker/rproxy/${ENV_NAME}/index.html /usr/share/nginx/html
COPY docker/rproxy/${ENV_NAME}/robots.txt /usr/share/nginx/html

COPY docker/rproxy-certbot/nginx-tls.sh /
RUN chmod 500 /nginx-tls.sh
    && openssl rand 80 >/etc/nginx/ssl_session_ticket.key \
    && /nginx-tls.sh self-signed-${ENV_TYPE}

VOLUME ["/etc/letsencrypt", "/etc/nginx", "/usr/share/nginx/html"]

CMD ["nginx"]
