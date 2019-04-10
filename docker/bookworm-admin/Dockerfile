FROM alpine:3.9
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="Bookworm"

ENV buildNumber=LocalBuild

RUN apk update \
    && apk --no-cache \
        add \
        coreutils \
        openssh \
    && apk --no-cache \
        add --virtual .deps \
        shadow \
    && sed -i'' -E \
        -e 's/^#(PermitRootLogin|HostKey)/\1/' \
        -e 's/^#(ListenAddress 0|ListenAddress ::|Port)/\1/' \
        /etc/ssh/sshd_config \
    && sed -i'' -E \
        -e 's#^(PermitRootLogin ).*#\1 without-password#' \
        -e 's#(Port ).*#\1 22#' \
        /etc/ssh/sshd_config \
    && sed -i'' -E \
        -e 's/^(Subsystem.*sftp.*sftp-server.*)/#\1/' \
        /etc/ssh/sshd_config \
    && ssh-keygen -A \
    && ssh-keygen -t rsa -b 4096 -q -N "" -f /etc/ssh/ssh_host_key

WORKDIR /etc/ssh/keys
COPY docker/bookworm-admin/keys/*_authorized_keys ./

WORKDIR /etc/ssh
COPY docker/bookworm-admin/sshd_config_2 .
COPY docker/bin/setup-ssh-users.sh .
RUN chmod 700 setup-ssh-users.sh \
        && ./setup-ssh-users.sh \
        && rm setup-ssh-users.sh \
        && cat sshd_config_2 >>sshd_config \
        && rm sshd_config_2 \
        && chmod 444 /etc/ssh/keys/*_authorized_keys \
        && apk del .deps

WORKDIR /

VOLUME ["/etc/ssh", "/etc/ssh/keys"]

EXPOSE 22

CMD ["/usr/sbin/sshd", "-D"]
