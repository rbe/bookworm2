FROM adoptopenjdk/openjdk14:alpine AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"
ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
RUN apk update
RUN apk --no-cache add tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" >/etc/timezone
#RUN apk add --no-cache libcap \
#    && setcap cap_setgid=ep /bin/busybox
RUN apk add --no-cache sudo
RUN echo "service ALL=(ALL) NOPASSWD: /usr/sbin/crond" >/etc/sudoers.d/service
RUN apk add --no-cache busybox-suid
RUN addgroup -g 1000 service \
    && adduser -u 100 -D -S -G service service
RUN apk --no-cache add bash
COPY --chown=service target/classes/docker-entrypoint.sh /usr/local/bin/
RUN chmod +x -- /usr/local/bin/*
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]

FROM base
ARG JAR_FILE
COPY --chown=service target/dependency/${JAR_FILE} /usr/local/service.jar
COPY --chown=service target/classes/application-shard.tmpl.yml /var/local/
RUN touch /var/local/application-shard.yml
COPY --chown=service target/classes/logback.xml /var/local/
RUN mkdir /var/local/java_debug
RUN find /var/local -type d -print0 | xargs -0 chown service:service \
   && find /var/local -type d -print0 | xargs -0 chmod 755
USER service:service
RUN echo "* */10 * * * /usr/local/bin/cleanup.sh" | crontab -
VOLUME ["/var/local"]
WORKDIR /var/local
EXPOSE 8080/tcp 5005/tcp 1099/tcp
