FROM adoptopenjdk/openjdk14:alpine AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"
ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
RUN apk update
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" >/etc/timezone
#RUN apk add --no-cache libcap \
#    && setcap cap_setgid=ep /bin/busybox
RUN apk add --no-cache busybox-suid
RUN addgroup -g 1000 service \
    && adduser -u 100 -D -S -G service service
RUN apk add --no-cache sudo
RUN echo "service ALL=(ALL) NOPASSWD: /usr/sbin/crond" >/etc/sudoers.d/service
RUN apk --no-cache add bash
COPY --chown=service *.sh /usr/local/bin/
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]

FROM base
RUN find /usr/local/bin -type f -name \*.sh -print0 | xargs -0 chmod 755 \
    && /usr/local/bin/mkdirs.sh
COPY --chown=service wbh.bookworm.hoerbuchkatalog.assembly-${project.version}.jar /usr/local/service.jar
COPY --chown=service application-production.yml /var/local/
COPY --chown=service application-secrets.tmpl.yml /var/local/application-secrets.yml
RUN /usr/local/bin/perms.sh
USER service:service
RUN echo "* */10 * * * /usr/local/bin/cleanup.sh" | crontab -
VOLUME ["/var/local"]
WORKDIR /var/local
EXPOSE 8080/tcp 5005/tcp
ENV HOERBUCHKATALOG_TEMPLATE /var/local/templates
