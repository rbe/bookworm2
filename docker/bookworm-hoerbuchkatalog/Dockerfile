FROM openjdk:11-jre-slim
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="Bookworm"

ENV buildNumber=LocalBuild

ENV TZ Europe/Berlin
RUN echo "Europe/Berlin" >/etc/timezone \
    && rm /etc/localtime \
    && ln -sf /usr/share/zoneinfo/Europe/Berlin /etc/localtime \
    && dpkg-reconfigure -f noninteractive tzdata

RUN groupadd -g 4801 bookworm \
    && useradd -u 4801 -g bookworm bookworm

WORKDIR /opt/bookworm

COPY docker/bookworm-hoerbuchkatalog/mkdirs.sh .
RUN mkdirs.sh

COPY docker/bookworm-hoerbuchkatalog/run.sh .
COPY hoerbuchkatalog/assembly/src/main/resources/application-production.yml .
COPY hoerbuchkatalog/assembly/target/wbh.bookworm.hoerbuchkatalog.assembly.jar app/
COPY hoerbuchkatalog/assembly/src/main/resources/conf/* conf/
COPY docker/bookworm-hoerbuchkatalog/secrets.json conf/
COPY hoerbuchkatalog/ui/src/main/resources/templates var/templates/
COPY docker/bookworm-hoerbuchkatalog/MerklisteMigrator.sh .
COPY hoerbuchkatalog/migrator/target/MerklisteMigrator.jar app/

COPY docker/bookworm-hoerbuchkatalog/perms.sh .
RUN perms.sh

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
CMD ["/opt/bookworm/run.sh"]
