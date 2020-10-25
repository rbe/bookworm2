FROM adoptopenjdk/openjdk14:alpine-slim AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"
ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
RUN apk update
RUN apk --no-cache add tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" >/etc/timezone
RUN addgroup service \
    && adduser -D -S -G service service
RUN apk --no-cache add bash

FROM base
COPY --chown=service *.sh /usr/local/bin/
RUN find /usr/local/bin -type f -name \*.sh -print0 | xargs -0 chmod 755 \
    && /usr/local/bin/mkdirs.sh
COPY --chown=service wbh.bookworm.hoerbuchkatalog.assembly-${project.version}.jar /usr/local/service.jar
COPY --chown=service application-production.yml /var/local/
COPY --chown=service application-secrets.tmpl.yml /var/local/application-secrets.yml
COPY --chown=service templates/ /var/local/templates/
RUN /usr/local/bin/perms.sh
USER service:service
VOLUME ["/var/local"]
WORKDIR /var/local
EXPOSE 8080/tcp 5005/tcp
ENV HOERBUCHKATALOG_TEMPLATE /var/local/templates
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
