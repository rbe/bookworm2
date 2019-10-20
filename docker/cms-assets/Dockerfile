FROM alpine:3.10
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"

ENV TZ Europe/Berlin
ENV LANG en_US.UTF-8

RUN apk update \
    && apk --no-cache add \
        tzdata \
        coreutils \
        openssh \
    && cp /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo "${TZ}" >/etc/timezone
    && sed -i'' -E \
        -e 's/^#(HostKey)/\1/' \
        -e 's/^#(PasswordAuthentication)/\1/' \
        -e 's/^#(ListenAddress 0|ListenAddress ::|Port)/\1/' \
        -e 's#(Port ).*#\1 22#' \
        -e 's#(PasswordAuthentication ).*#\1 no#' \
        -e 's/^(Subsystem.*sftp.*)(sftp-server.*)/#\1\2/' \
        /etc/ssh/sshd_config \
    && ssh-keygen -A \
    && ssh-keygen -t rsa -b 4096 -q -N "" -f /etc/ssh/ssh_host_key \
    && addgroup -g 82 -S www-data \
    && adduser -u 82 -D -S -G www-data www-data

ADD docker/cms-assets/.version /

COPY docker/sshkeys/mysql_authorized_keys /etc/ssh/keys/
COPY docker/sshkeys/www-data_authorized_keys /etc/ssh/keys/
COPY docker/cms-assets/sshd_config_2 .
COPY docker/bin/setup-ssh-users.sh .

RUN chmod 700 setup-ssh-users.sh \
    && ./setup-ssh-users.sh rbe cew mysql wbh \
    && rm setup-ssh-users.sh \
    && passwd -u www-data \
    && cat sshd_config_2 >>sshd_config \
    && rm sshd_config_2 \
    && chmod 444 /etc/ssh/keys/*_authorized_keys

WORKDIR /var/www/html

VOLUME ["/etc/ssh", "/etc/ssh/keys", "/var/www/html"]

EXPOSE 22
CMD ["/usr/sbin/sshd", "-D"]
