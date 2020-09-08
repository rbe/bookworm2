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
ARG JAR_FILE
COPY --chown=service target/dependency/${JAR_FILE} /usr/local/service.jar
COPY --chown=service src/main/docker/application-shard.tmpl.yml /var/local/
COPY --chown=service src/main/docker/logback.xml /var/local/
RUN mkdir /var/local/java_debug
RUN chown -R service:service /var/local \
   && chmod -R 755 /var/local/java_debug
USER service
VOLUME ["/var/local"]
WORKDIR /var/local
ENV MICRONAUT_CONFIG_FILES ""
ENTRYPOINT ["java",\
    "-Xms1536m", "-Xmx1536m",\
    "-XX:+HeapDumpOnOutOfMemoryError",\
    "-XX:HeapDumpPath=/var/local/java_debug",\
    "-XX:+UnlockExperimentalVMOptions",\
    "-XX:+UseZGC",\
    "-Xlog:gc=info,stats:file=/var/local/java_debug/gc.log:time,uptime,pid:filecount=16,filesize=128M",\
    "-Dlogback.configurationFile=/var/local/logback.xml",\
    "-Dmicronaut.environments=prod",\
    "-Dmicronaut.config.files=/var/local/application-shard.yml",\
    "-jar", "/usr/local/service.jar"]
