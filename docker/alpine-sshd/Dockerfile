FROM alpine:3.9
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"

ENV buildNumber=LocalBuild

ENV TZ Europe/Berlin

RUN apk update \
    && apk --no-cache \
        add \
        coreutils \
        openssh \
    && sed -i'' -E \
        -e 's/^#(HostKey)/\1/' \
        -e 's/^#(PasswordAuthentication)/\1/' \
        -e 's/^#(ListenAddress 0|ListenAddress ::|Port)/\1/' \
        /etc/ssh/sshd_config \
    && sed -i'' -E \
        -e 's#(Port ).*#\1 22#' \
        -e 's#(PasswordAuthentication ).*#\1 no#' \
        /etc/ssh/sshd_config \
    && sed -i'' -E \
        -e 's/^(Subsystem.*sftp)(.*sftp-server.*)/#\1\2/' \
        /etc/ssh/sshd_config \
    && ssh-keygen -A \
    && ssh-keygen -t rsa -b 4096 -q -N "" -f /etc/ssh/ssh_host_key \
    && addgroup -g 82 -S www-data \
    && adduser -u 82 -D -S -G www-data www-data

WORKDIR /

VOLUME ["/etc/ssh", "/etc/ssh/keys"]

EXPOSE 22

CMD ["/usr/sbin/sshd", "-D"]
