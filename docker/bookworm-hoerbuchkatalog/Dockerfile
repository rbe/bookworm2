FROM openjdk:11-jre-slim
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="Bookworm"

ENV buildNumber=LocalBuild

RUN groupadd -g 4801 bookworm \
    && useradd -u 4801 -g bookworm bookworm

WORKDIR /opt/bookworm/var
RUN mkdir templates \
    && mkdir repository \
    && mkdir repository/Bestellung \
    && mkdir repository/HtmlEmailTemplate \
    && mkdir repository/Merkliste \
    && mkdir repository/Warenkorb \
    && mkdir wbh \
    && mkdir wbh/hoerbuchkatalog \
    && mkdir wbh/nutzerdaten \
    && mkdir blista \
    && mkdir blista/dls
COPY hoerbuchkatalog/ui/src/main/resources/templates templates/

WORKDIR /opt/bookworm/app
COPY hoerbuchkatalog/assembly/target/wbh.bookworm.hoerbuchkatalog.assembly.jar .
COPY hoerbuchkatalog/assembly/src/main/resources/application-production.yml .

WORKDIR /opt/bookworm/conf
COPY hoerbuchkatalog/assembly/src/main/resources/conf/* ./
COPY docker/bookworm-hoerbuchkatalog/secrets.json .

WORKDIR /opt/bookworm
COPY docker/bookworm-hoerbuchkatalog/run.sh .

RUN chown -R root:root /opt/bookworm \
    && chown -R bookworm:bookworm /opt/bookworm/app \
    && chmod 660 /opt/bookworm/app/*.yml \
    && chown -R bookworm:bookworm /opt/bookworm/conf \
    && chmod 770 /opt/bookworm/conf \
    && chmod 660 /opt/bookworm/conf/* \
    && chown root:root /opt/bookworm/var \
    && chown root:root /opt/bookworm/var/templates \
    && chown bookworm:bookworm /opt/bookworm/var/templates/* \
    && find /opt/bookworm/var/templates/ -type d -print0 | xargs -0 chmod 770 \
    && find /opt/bookworm/var/templates/ -type f -print0 | xargs -0 chmod 660 \
    && chmod 775 /opt/bookworm/var/templates \
    && chown root:root /opt/bookworm/var/wbh \
    && chown bookworm:bookworm /opt/bookworm/var/wbh/* \
    && chmod 770 /opt/bookworm/var/wbh/* \
    && chown root:root /opt/bookworm/var/blista \
    && chown bookworm:bookworm /opt/bookworm/var/blista/* \
    && chown root:root /opt/bookworm/var/repository \
    && chown bookworm:bookworm /opt/bookworm/var/repository/* \
    && chmod 770 /opt/bookworm/var/repository/*

USER bookworm:bookworm

VOLUME [\
    "/opt/bookworm",\
    "/opt/bookworm/var/templates",\
    "/opt/bookworm/var/repository",\
    "/opt/bookworm/var/wbh",\
    "/opt/bookworm/var/blista"\
]

EXPOSE 9080/tcp

ENV HOERBUCHKATALOG_TEMPLATE /opt/bookworm/var/templates
CMD ["bash", "/opt/bookworm/run.sh"]
