FROM openjdk:11-jre-slim
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="Bookworm"

ENV buildNumber=LocalBuild

RUN groupadd -g 4801 bookworm \
    && useradd -u 4801 -g bookworm bookworm

WORKDIR /opt/bookworm/var
RUN mkdir templates \
    && mkdir repository \
    && mkdir wbh \
    && mkdir wbh/hoerbuchkatalog \
    && mkdir wbh/nutzerdaten \
    && mkdir blista
COPY hoerbuchkatalog/ui/src/main/resources/templates templates/

WORKDIR /opt/bookworm/app
COPY hoerbuchkatalog/assembly/target/wbh.bookworm.hoerbuchkatalog.assembly.jar .
COPY hoerbuchkatalog/assembly/src/main/resources/application-production.yml .

WORKDIR /opt/bookworm/conf
COPY hoerbuchkatalog/assembly/src/main/resources/conf/* ./
COPY docker/bookworm-hoerbuchkatalog/secrets.json .

WORKDIR /opt/bookworm
COPY docker/bookworm-hoerbuchkatalog/run.sh .

RUN chown -R root:root /opt/bookworm \
    && chown -R bookworm:bookworm /opt/bookworm/app \
    && chown -R bookworm:bookworm /opt/bookworm/conf \
    && chown root:root /opt/bookworm/var \
    && chown bookworm:bookworm /opt/bookworm/var/*

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
CMD ["bash", "/opt/bookworm/run.sh"]
