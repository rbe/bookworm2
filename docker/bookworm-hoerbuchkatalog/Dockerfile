FROM openjdk:11-jre-slim
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="Bookworm"

ENV buildNumber=LocalBuild
ENV HOERBUCHKATALOG_TEMPLATE /var/bookworm/templates

WORKDIR /opt/bookworm
COPY hoerbuchkatalog/assembly/target/wbh.bookworm.hoerbuchkatalog.assembly.jar .
COPY hoerbuchkatalog/assembly/src/main/resources/conf conf
COPY hoerbuchkatalog/assembly/src/main/resources/application*.yml ./

WORKDIR /var/bookworm
RUN mkdir /var/bookworm/templates \
    && mkdir /var/bookworm/repository \
    && mkdir /var/bookworm/wbh \
    && mkdir /var/bookworm/wbh/hoerbuchkatalog \
    && mkdir /var/bookworm/wbh/nutzerdaten \
    && mkdir /var/bookworm/blista
    && ln -s /var/bookworm /opt/bookworm/var
COPY hoerbuchkatalog/ui/src/main/resources/templates templates/

VOLUME [\
    "/opt/bookworm",\
    "/var/bookworm/templates",\
    "/var/bookworm/repository",\
    "/var/bookworm/wbh",\
    "/var/bookworm/blista"\
]

EXPOSE 9080/tcp

CMD [\
    "java",\
        "-Xms2g", "-Xmx2g",\
        "-jar", "wbh.bookworm.hoerbuchkatalog.assembly.jar",\
            "--spring.profiles.active=development",\
            "--spring.config.additional-location=conf/"\
]
