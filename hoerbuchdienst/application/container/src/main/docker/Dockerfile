FROM adoptopenjdk/openjdk14:alpine-slim AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"

ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
RUN apk update
RUN apk --no-cache add tzdata \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" >/etc/timezone
RUN addgroup myservice \
    && adduser -D -S -G myservice myservice

FROM base
USER myservice
ARG JAR_FILE
COPY --chown=myservice target/dependency/${JAR_FILE} /usr/local/hoerbuchdienst/hoerbuchdienst.jar
COPY --chown=myservice src/main/docker/application-shard.tmpl.yml /var/local/
COPY --chown=myservice src/main/docker/logback.xml /var/local/
VOLUME ["/var/local"]
WORKDIR /var/local
ENV MICRONAUT_CONFIG_FILES ""
ENTRYPOINT ["java",\
    "-Xms1024m", "-Xmx1536m",\
    "-Xlog:gc*",\
    "-XX:+UnlockExperimentalVMOptions",\
    "-XX:+UseZGC",\
    "-Dlogback.configurationFile=/var/local/logback.xml",\
    "-Dmicronaut.environments=prod",\
    "-Dmicronaut.config.files=/var/local/application-shard.yml",\
    "-jar", "/usr/local/hoerbuchdienst/hoerbuchdienst.jar"]
