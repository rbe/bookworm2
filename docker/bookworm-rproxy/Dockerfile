FROM nginx:stable-alpine
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="Bookworm"

ENV buildNumber=LocalBuild

RUN apk update \
    && apk --no-cache add --virtual .deps \
        letsencrypt-nosudo

WORKDIR /etc/nginx
COPY docker/bookworm-rproxy/nginx.conf .
COPY docker/bookworm-rproxy/nginx.wbh.conf .

WORKDIR /usr/share/nginx/html
COPY docker/bookworm-rproxy/index.html .

VOLUME ["/etc/nginx", "/usr/share/nginx/html"]

CMD ["nginx"]
