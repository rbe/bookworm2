FROM openjdk:11-jre-slim
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="Bookworm"

ENV buildNumber=LocalBuild

RUN groupadd -g 4801 bookworm \
    && useradd -u 4801 -g bookworm bookworm

WORKDIR /opt/bookworm/app
COPY hoerbuchkatalog/assembly/target/wbh.bookworm.hoerbuchkatalog.assembly.jar .

WORKDIR /opt/bookworm/conf
COPY hoerbuchkatalog/assembly/src/main/resources/conf/* ./
COPY docker/bookworm-hoerbuchkatalog/secrets.json .

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

WORKDIR /opt/bookworm
COPY hoerbuchkatalog/assembly/src/main/resources/application-production.yml .
COPY docker/bookworm-hoerbuchkatalog/run.sh .
RUN chown -R bookworm:bookworm . \
    && chown root:root . \
    && chmod 550 . \
    && chmod 550 run.sh \
    && chmod 550 app \
    && chmod 400 app/* \
    && chmod 660 *.yml \
    && chmod 550 conf \
    && chmod 660 conf/* \
    && chown root:root var \
    && chmod 555 var \
    && chmod 555 var/* \
    && chown root:root var/templates \
    && find var/templates -type d -print0 | xargs -0 chmod 770 \
    && find var/templates -type f -print0 | xargs -0 chmod 660 \
    && chmod 555 var/templates \
    && chown root:root var/wbh \
    && chmod 750 var/wbh/hoerbuchkatalog \
    && chmod 750 var/wbh/nutzerdaten \
    && chown root:root var/repository \
    && chmod 750 var/repository/* \
    && chown root:root var/blista \
    && chmod 750 var/blista/dls

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
CMD ["/opt/bookworm/run.sh"]
