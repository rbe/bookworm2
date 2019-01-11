FROM openjdk:11.0.1-jdk-slim-stretch
MAINTAINER Ralf Bensmann <ralf@art-of-coding.eu>
ENV buildNumber=LocalBuild
RUN echo Build number is $buildNumber
LABEL vendor="Art of Coding UG"
WORKDIR /opt/hoerbuchkatalog
COPY assembly/target/wbh.bookworm.hoerbuchkatalog.assembly.jar .
COPY assembly/src/main/resources/conf conf
COPY assembly/src/main/resources/application*.yml ./
COPY assembly/var/hoerbuchkatalog/Gesamt*.dat ./var/hoerbuchkatalog/Gesamt.dat
COPY ui/src/main/resources/templates ./templates
VOLUME /templates
ENV HOERBUCHKATALOG_TEMPLATE ./templates
HEALTHCHECK NONE
EXPOSE 9080/tcp
RUN ls -lR
CMD ["java", \
    "-Xms1G", "-Xmx1G", \
    "-jar", "wbh.bookworm.hoerbuchkatalog.assembly.jar", \
    "--spring.profiles.active=development", \
    "--spring.config.additional-location=conf/" \
]
