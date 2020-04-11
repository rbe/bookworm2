FROM oracle/graalvm-ce:20.0.0-java11 AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"

ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8

#RUN echo "Europe/Berlin" >/etc/timezone \
#    && rm /etc/localtime \
#    && ln -sf /usr/share/zoneinfo/Europe/Berlin /etc/localtime \
#    && dpkg-reconfigure -f noninteractive tzdata

RUN gu install native-image

FROM base
RUN groupadd myservice \
    && useradd -g myservice myservice
USER myservice
ADD target/dependency/ /usr/local/myservice/
WORKDIR /usr/local/myservice
ARG JAR_FILE
#RUN native-image --no-server --static -jar ${JAR_FILE}
RUN native-image --no-server --static --enable-http -cp ${JAR_FILE}
VOLUME /var/local/myservice
WORKDIR /var/local/myservice
EXPOSE 8080
ENTRYPOINT ["/usr/local/myservice/${JAR_FILE}", "-Djava.library.path=/usr/local/myservice"]
