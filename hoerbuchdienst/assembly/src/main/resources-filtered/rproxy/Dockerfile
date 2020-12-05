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
RUN addgroup -g 1000 service \
    && adduser -u 100 -D -S -G service service
RUN adduser -u 82 -D -S -G www-data www-data
RUN apk --no-cache add bash
RUN apk --no-cache add curl
RUN apk add --no-cache git
RUN apk add --no-cache pwgen
RUN apk --no-cache add openssl
RUN apk --no-cache add certbot-nginx
RUN rm /etc/nginx/conf.d/default.conf*
RUN echo "0 0 * * WED,SUN certbot renew" | crontab -
#RUN apk add --no-cache make
#RUN apk add --no-cache python3
#RUN git clone https://github.com/michaelklishin/tls-gen /usr/local/tls-gen
COPY *.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/*.sh
RUN mkdir /var/local/rproxy
VOLUME ["/etc/letsencrypt", "/var/local/rproxy"]
EXPOSE 80/tcp 443/tcp
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]

FROM base
RUN openssl rand 80 >/etc/nginx/ssl_session_ticket.key
COPY nginx.conf /etc/nginx/
COPY tlsredirect.conf /etc/nginx/conf.d/
COPY *.conf.disabled /etc/nginx/conf.d/
RUN mkdir /etc/nginx/stream.d
COPY *.stream.disabled /etc/nginx/stream.d/
COPY robots.txt /var/local/rproxy/
COPY index.html /var/local/rproxy/
COPY favicon.ico /var/local/rproxy/
WORKDIR /etc/nginx
