FROM openjdk:11-jre-slim
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="Bookworm"

ENV buildNumber=LocalBuild

WORKDIR /opt/bookworm/var
RUN mkdir templates \
    && mkdir repository \
    && mkdir wbh \
    && mkdir wbh/hoerbuchkatalog \
    && mkdir wbh/nutzerdaten \
    && mkdir blista
COPY hoerbuchkatalog/ui/src/main/resources/templates templates/

WORKDIR /opt/bookworm
COPY hoerbuchkatalog/assembly/target/wbh.bookworm.hoerbuchkatalog.assembly.jar .
COPY hoerbuchkatalog/assembly/src/main/resources/conf conf
COPY hoerbuchkatalog/assembly/src/main/resources/application*.yml ./

EXPOSE 9080/tcp

VOLUME [\
    "/opt/bookworm",\
    "/opt/bookworm/var/templates",\
    "/opt/bookworm/var/repository",\
    "/opt/bookworm/var/wbh",\
    "/opt/bookworm/var/blista"\
]

ENV HOERBUCHKATALOG_TEMPLATE /opt/bookworm/var/templates
CMD [\
    "java",\
        "-Xms2g", "-Xmx2g",\
        "-jar", "wbh.bookworm.hoerbuchkatalog.assembly.jar",\
            "--spring.profiles.active=production",\
            "--spring.config.additional-location=conf/"\
]
