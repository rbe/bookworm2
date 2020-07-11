FROM alpine:latest AS base
RUN apk update
RUN apk add git
RUN apk add openjdk11-jdk \
    && apk add maven
RUN apk add docker \
    && apk add docker-compose
FROM base
VOLUME ["/var/local/.m2", "/var/local/source"]
WORKDIR /var/local
