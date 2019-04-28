FROM nginx:1.14.2-alpine
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"

ENV buildNumber=LocalBuild

RUN apk update \
    && apk --no-cache \
        add \
        curl \
        nginx-mod-http-headers-more \
    && rm /etc/nginx/conf.d/default.conf*
#    && addgroup -g 82 -S www-data \
#    && adduser -u 82 -D -S -G www-data www-data

WORKDIR /etc/nginx
COPY nginx.conf .
COPY nginx.wbhonline.conf conf.d/wbhonline.conf

WORKDIR /usr/share/nginx/html
COPY index.html .

VOLUME ["/etc/nginx", "/usr/share/nginx/html"]

HEALTHCHECK CMD curl --fail http://localhost:80 || exit 1

CMD ["nginx"]
