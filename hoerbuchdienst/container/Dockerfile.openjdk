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
    && useradd -G myservice myservice
USER myservice
#ADD target/lib /usr/local/myservice/lib
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/local/myservice/myservice.jar

VOLUME "/data"

WORKDIR /data
ENTRYPOINT ["java", "-jar", "/usr/local/myservice/myservice.jar"]
