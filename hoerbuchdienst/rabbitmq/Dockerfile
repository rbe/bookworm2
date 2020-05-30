FROM rabbitmq:3.8.3-management-alpine AS base

RUN rabbitmq-plugins enable --offline rabbitmq_mqtt
RUN rabbitmq-plugins enable --offline rabbitmq_stomp
RUN rabbitmq-plugins enable --offline rabbitmq_federation_management
RUN rabbitmq-plugins enable rabbitmq_federation_management

FROM base AS tls
RUN git clone https://github.com/michaelklishin/tls-gen tls-gen
RUN cd tls-gen/basic
RUN make PASSWORD=bunnies
RUN make verify
RUN make info
RUN ls -l ./result
ADD rabbitmq.conf /etc/rabbitmq

VOLUME ["/var/lib/rabbitmq"]
