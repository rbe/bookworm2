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
RUN addgroup service \
    && adduser -D -S -G service service

FROM base
ARG JAR_FILE
COPY --chown=service target/dependency/${JAR_FILE} /usr/local/service.jar
COPY --chown=service src/main/docker/application-shard.tmpl.yml /var/local/
RUN touch /var/local/application-shard.yml
COPY --chown=service src/main/docker/logback.xml /var/local/
RUN mkdir /var/local/java_debug
RUN find /var/local -type d -print0 | xargs -0 chown service:service \
   && find /var/local -type d -print0 | xargs -0 chmod 755
USER service
VOLUME ["/var/local"]
WORKDIR /var/local
ENV MICRONAUT_CONFIG_FILES ""
ENTRYPOINT ["java",\
    "-Xms1536m", "-Xmx1536m",\
    "-XX:+UseCompressedOops",\
    "-XX:+HeapDumpOnOutOfMemoryError",\
    "-XX:HeapDumpPath=/var/local/java_debug",\
    "-XX:ErrorFile=/var/local/java_debug/java_error_%p.log",\
    "-XX:+UnlockExperimentalVMOptions",\
    "-XX:+UseZGC",\
    "-Xlog:gc=info,gc+stats:file=/var/local/java_debug/gc.log:time,uptime,pid:filecount=16,filesize=128M",\
    "-Dlogback.configurationFile=/var/local/logback.xml",\
    "-Dmicronaut.environments=prod",\
    "-Dmicronaut.config.files=/var/local/application-shard.yml",\
    "-jar", "/usr/local/service.jar"]
