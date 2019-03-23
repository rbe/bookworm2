FROM nginx:stable-alpine
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"

ENV buildNumber=LocalBuild

RUN apk update \
    && apk --no-cache add --virtual .deps \
        letsencrypt-nosudo

VOLUME ["/etc/nginx"]

COPY nginx.conf /etc/nginx/nginx.conf
COPY nginx.wbh.conf /etc/nginx/conf.d/wbh.conf

COPY index.html /usr/share/nginx/html
