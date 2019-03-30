FROM nginx:stable-alpine
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="Bookworm"

ENV buildNumber=LocalBuild

RUN apk update \
    && apk --no-cache add --virtual .deps \
        letsencrypt-nosudo

VOLUME ["/etc/nginx"]

COPY docker/bookworm-rproxy/nginx.conf /etc/nginx/nginx.conf
COPY docker/bookworm-rproxy/nginx.wbh.conf /etc/nginx/conf.d/wbh.conf
COPY docker/bookworm-rproxy/index.html /usr/share/nginx/html

CMD ["nginx"]
