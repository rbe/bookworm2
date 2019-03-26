FROM openjdk:11-jre-slim
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"

ENV buildNumber=LocalBuild

WORKDIR /opt/bookworm
COPY hoerbuchkatalog/assembly/target/wbh.bookworm.hoerbuchkatalog.assembly.jar .
COPY hoerbuchkatalog/assembly/src/main/resources/conf conf
COPY hoerbuchkatalog/assembly/src/main/resources/application*.yml ./
COPY hoerbuchkatalog/ui/src/main/resources/templates /var/wbh/templates

RUN    mkdir /var/bookworm/templates \
    && mkdir /var/bookworm/repository \
    && mkdir /var/bookworm/hoerbuchkatalog \
    && mkdir /var/bookworm/nutzerdaten \
    && mkdir /var/bookworm/blista

VOLUME [
    "/opt/bookworm",
    "/var/bookworm/templates",
    "/var/bookworm/repository",
    "/var/bookworm/wbh",
    "/var/bookworm/blista"
]

EXPOSE 9080/tcp

ENV HOERBUCHKATALOG_TEMPLATE /var/bookworm/templates
CMD [
    "java", \
        "-Xms2G", "-Xmx2G", \
        "-jar", "wbh.bookworm.hoerbuchkatalog.assembly.jar", \
            "--spring.profiles.active=development", \
            "--spring.config.additional-location=conf/" \
]
