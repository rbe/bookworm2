ARG RABBITMQ_RELEASE
FROM rabbitmq:${RABBITMQ_RELEASE} AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"
ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8
RUN apk --no-cache add \
        tzdata \
        pwgen \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" >/etc/timezone
RUN rabbitmq-plugins enable --offline rabbitmq_federation
RUN rabbitmq-plugins enable --offline rabbitmq_federation_management
COPY *.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/*.sh
RUN echo "* 0/30 * * * rabbitmq-export.sh" | crontab -
EXPOSE 4369/tcp 5671/tcp 5672/tcp 15672/tcp 25672/tcp 15671/tcp

FROM base
COPY rabbitmq.conf /etc/rabbitmq/rabbitmq.conf.disabled
COPY definitions.json /etc/rabbitmq
RUN mv /etc/rabbitmq/rabbitmq.conf.disabled /etc/rabbitmq/rabbitmq.conf
VOLUME ["/etc/rabbitmq", "/var/lib/rabbitmq"]
ENTRYPOINT ["/usr/local/bin/my-docker-entrypoint.sh"]
CMD ["rabbitmq-server"]
