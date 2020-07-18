FROM alpine:latest AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"
ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
RUN apk --no-cache add \
        tzdata \
        pwgen \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" >/etc/timezone
#RUN apk add --no-cache netcat-openbsd
RUN apk add --no-cache \
    python3 py3-pip bash \
  && pip3 install --upgrade pip
RUN apk add --no-cache clamav clamav-libunrar
COPY *.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/*.sh
EXPOSE 3310/tcp
VOLUME ["/data"]
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
HEALTHCHECK --start-period=300s CMD ping.sh

FROM base
COPY testfiles/* /var/local/
COPY *.conf /etc/clamav/
