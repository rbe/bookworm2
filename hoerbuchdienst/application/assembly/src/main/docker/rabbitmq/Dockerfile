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
COPY rabbitmq-*.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/*.sh

FROM base
COPY rabbitmq.conf /etc/rabbitmq/rabbitmq.conf.disabled
COPY definitions.json /etc/rabbitmq
ARG RABBITMQ_HOSTNAME
RUN sed -i'' \
        -e "s#RABBITMQ_HOSTNAME#${RABBITMQ_HOSTNAME}#g" \
        /etc/rabbitmq/rabbitmq.conf.disabled \
    && mv /etc/rabbitmq/rabbitmq.conf.disabled /etc/rabbitmq/rabbitmq.conf
VOLUME ["/etc/rabbitmq", "/var/lib/rabbitmq"]
