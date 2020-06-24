ARG OPENJDK_RELEASE
FROM openjdk:${OPENJDK_RELEASE} AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"
ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
RUN echo "${TZ}" >/etc/timezone \
    && rm /etc/localtime \
    && ln -sf /usr/share/zoneinfo/Europe/Berlin /etc/localtime \
    && dpkg-reconfigure -f noninteractive tzdata \
    && groupadd bookworm \
    && useradd -g bookworm bookworm

FROM base
WORKDIR /opt/bookworm
COPY *.sh bin/
RUN chmod ug+x bin/*.sh \
    && bin/mkdirs.sh
COPY application-production.yml .
COPY wbh.bookworm.hoerbuchkatalog.assembly-${project.version}.jar app/wbh.bookworm.hoerbuchkatalog.assembly.jar
COPY conf/* conf/
COPY secrets.json conf/
COPY templates/* var/templates/
RUN bin/perms.sh

USER bookworm:bookworm

VOLUME [\
    "/opt/bookworm",\
    "/opt/bookworm/var/templates",\
    "/opt/bookworm/var/repository",\
    "/opt/bookworm/var/wbh",\
    "/opt/bookworm/var/blista"\
]

EXPOSE 8080/tcp
ENV HOERBUCHKATALOG_TEMPLATE /opt/bookworm/var/templates
ENTRYPOINT ["/opt/bookworm/bin/docker-entrypoint.sh"]
