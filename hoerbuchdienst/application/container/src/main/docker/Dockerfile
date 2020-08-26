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
COPY --chown=myservice target/dependency/${JAR_FILE} /usr/local/hoerbuchdienst/hoerbuchdienst.jar
COPY --chown=myservice src/main/docker/application-shard.tmpl.yml /var/local/
COPY --chown=myservice src/main/docker/logback.xml /var/local/
RUN chown -R myservice:myservice /var/local
USER myservice
VOLUME ["/var/local"]
WORKDIR /var/local
ENV MICRONAUT_CONFIG_FILES ""

#https://sematext.com/blog/jvm-performance-tuning/

#-XX:+UseG1GC -XX:+UseStringDeduplication
#-XX:+PrintStringDeduplicationStatistics
#-XX:+UseStringCache
#-XX:+UseCompressedStrings
#-XX:+OptimizeStringConcat

#-XX:+UseGCLogFileRotation
#-XX:NumberOfGCLogFiles=<number of log files>
#-XX:GCLogFileSize=<file size>[unit]
#-Xlog:gc=info:file=/var/local/gc.log:time,uptime,pid:filecount=5,filesize=4096
#-Xlog:gc*
#-Xloggc:/path/to/gc.log

#-XX:+HeapDumpOnOutOfMemoryError
#-XX:HeapDumpPath=./java_pid<pid>.hprof
#-XX:OnOutOfMemoryError="<cmd args>;<cmd args>"
#-XX:+UseGCOverheadLimit

# G1
#-XX:+UseG1GC

# Shenandoah
#-XX:+UnlockExperimentalVMOptions
#-XX:+UseShenanodoahC
#-XX:ShenandoahGCHeuristics=adaptive|static|compact

# ZGC, https://wiki.openjdk.java.net/display/zgc/Main#Main-Configuration&Tuning
#-XX:+UnlockExperimentalVMOptions
#-XX:+UseZGC

ENTRYPOINT ["java",\
    "-Xms1536m", "-Xmx1536m",\
    "-Xlog:gc*",\
    "-XX:+UnlockExperimentalVMOptions",\
    "-XX:+UseZGC",\
    "-Dlogback.configurationFile=/var/local/logback.xml",\
    "-Dmicronaut.environments=prod",\
    "-Dmicronaut.config.files=/var/local/application-shard.yml",\
    "-jar", "/usr/local/hoerbuchdienst/hoerbuchdienst.jar"]
