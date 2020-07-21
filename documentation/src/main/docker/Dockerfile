FROM alpine:latest AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
RUN apk --no-cache add \
        nodejs \
        npm \
        asciidoc asciidoctor \
    && gem install rdoc --no-document \
    && gem install prawn \
    && gem install prawn-svg \
    && gem install prawn-templates \
    && gem install rouge \
    && gem update asciidoctor
