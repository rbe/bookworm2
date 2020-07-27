FROM openjdk:11-jre-slim AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"

ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
RUN echo "Europe/Berlin" >/etc/timezone \
    && rm /etc/localtime \
    && ln -sf /usr/share/zoneinfo/Europe/Berlin /etc/localtime \
    && dpkg-reconfigure -f noninteractive tzdata

FROM base
RUN groupadd myservice \
    && useradd -g myservice myservice
USER myservice
ARG JAR_FILE
COPY --chown=myservice target/dependency/${JAR_FILE} /usr/local/hoerbuchdienst/hoerbuchdienst.jar
COPY --chown=myservice src/main/docker/application-shard.yml /var/local/
VOLUME ["/var/local"]
WORKDIR /var/local
ENV MICRONAUT_CONFIG_FILES ""
#-Dlogback.configurationFile=/path/to/config.xml
ENTRYPOINT ["java", "-Xms2G", "-Xmx2G", "-Dmicronaut.environments=prod", "-Dmicronaut.config.files=/var/local/application-shard.yml", "-jar", "/usr/local/hoerbuchdienst/hoerbuchdienst.jar"]
