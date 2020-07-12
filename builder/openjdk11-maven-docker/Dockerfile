FROM alpine:latest AS base
RUN apk update
RUN apk add --no-cache git
RUN apk add --no-cache openjdk11-jdk \
    && apk add --no-cache maven
RUN apk add --no-cache docker \
    && apk add --no-cache docker-compose
FROM base
VOLUME ["/var/local/.m2", "/var/local/source"]
WORKDIR /var/local
