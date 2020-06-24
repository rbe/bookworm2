FROM alpine:3.12
RUN apk update \
    && apk add git \
    && apk add openjdk11-jdk \
    && apk add maven \
    && apk add docker \
    && apk add docker-compose
#FROM maven:3.6.3-openjdk-11
#RUN apt-get update \
#    && apt-get -y install docker-compose
VOLUME ["/var/local/.m2", "/var/local/source"]
WORKDIR /var/local
