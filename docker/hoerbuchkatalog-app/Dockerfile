FROM openjdk:11-jre-slim
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"

ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8

RUN echo "Europe/Berlin" >/etc/timezone \
    && rm /etc/localtime \
    && ln -sf /usr/share/zoneinfo/Europe/Berlin /etc/localtime \
    && dpkg-reconfigure -f noninteractive tzdata \
    && groupadd -g 4801 bookworm \
    && useradd -u 4801 -g bookworm bookworm

ADD .version /

WORKDIR /opt/bookworm
COPY docker/hoerbuchkatalog-app/*.sh bin/
RUN chmod ug+x bin/*.sh \
    && bin/mkdirs.sh
COPY hoerbuchkatalog/assembly/src/main/resources/application-production.yml .
COPY hoerbuchkatalog/assembly/target/wbh.bookworm.hoerbuchkatalog.assembly.jar app/
COPY hoerbuchkatalog/assembly/src/main/resources/conf/* conf/
COPY docker/hoerbuchkatalog-app/secrets.json conf/
COPY hoerbuchkatalog/ui/src/main/resources/templates var/templates/
COPY hoerbuchkatalog/migrator/target/MerklisteMigrator.jar app/
RUN bin/perms.sh

USER bookworm:bookworm

VOLUME [\
    "/opt/bookworm",\
    "/opt/bookworm/var/templates",\
    "/opt/bookworm/var/repository",\
    "/opt/bookworm/var/wbh",\
    "/opt/bookworm/var/blista"\
]

EXPOSE 9080/tcp
ENV HOERBUCHKATALOG_TEMPLATE /opt/bookworm/var/templates
CMD ["/opt/bookworm/bin/run.sh"]
