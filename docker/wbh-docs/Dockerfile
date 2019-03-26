FROM alpine:latest
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"

RUN apk --no-cache add --virtual .deps \
        git \
        nodejs npm \
        asciidoc asciidoctor \
    && gem install rdoc --no-document \
    && gem install prawn --version 2.1.0 \
    && gem install prawn-svg --version 0.26.0 \
    && gem install prawn-templates --version 0.0.4 \
    && gem update asciidoctor
    && git clone https://bitbucket.org/artofcoding/asciidocserver

VOLUME ["/opt/asciidocserver"]
WORKDIR /opt/asciidocserver

EXPOSE 80
EXPOSE 443

CMD ["npm", "run"]
