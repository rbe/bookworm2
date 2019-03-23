FROM openjdk:11-jre-slim
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"

ENV buildNumber=LocalBuild

VOLUME ["/var/templates", "/var/wbh"]

WORKDIR /opt/bookworm

COPY assembly/target/wbh.bookworm.hoerbuchkatalog.assembly.jar .
COPY assembly/src/main/resources/conf conf
COPY assembly/src/main/resources/application*.yml ./

COPY assembly/var/wbh/hoerbuchkatalog/Gesamt*.dat ./var/wbh/hoerbuchkatalog/
COPY assembly/var/wbh/nutzerdaten/*.csv ./var/wbh/nutzerdaten/
COPY ui/src/main/resources/templates /var/templates

ENV HOERBUCHKATALOG_TEMPLATE /var/templates

HEALTHCHECK NONE

EXPOSE 9080/tcp

CMD ["java", \
        "-Xms2G", "-Xmx2G", \
        "-jar", "wbh.bookworm.hoerbuchkatalog.assembly.jar", \
            "--spring.profiles.active=development", \
            "--spring.config.additional-location=conf/" \
]
