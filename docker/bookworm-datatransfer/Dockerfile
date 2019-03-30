FROM alpine:latest
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="Bookworm"

ENV buildNumber=LocalBuild

# https://wiki.alpinelinux.org/wiki/Alpine_Linux_package_management
# https://www.cyberciti.biz/faq/10-alpine-linux-apk-command-examples/
RUN apk update \
    && apk --no-cache add --virtual .deps \
        coreutils \
        openssh \
    && sed -i'' -E \
        -e 's/^#(PermitRootLogin|HostKey)/\1/' \
        -e 's/^#(ListenAddress 0|ListenAddress ::|Port)/\1/' \
        /etc/ssh/sshd_config \
    && sed -i'' -E \
        -e 's#^(PermitRootLogin ).*#\1 without-password#' \
        -e 's#(ListenAddress ).*#\1 0.0.0.0#' \
        -e 's#(Port ).*#\1 22#' \
        /etc/ssh/sshd_config \
    && sed -i'' -E \
        -e 's/^(Subsystem.*sftp.*sftp-server.*)/#\1/' \
        /etc/ssh/sshd_config \
    && ssh-keygen -A \
    && ssh-keygen -t rsa -b 4096 -q -N "" -f /etc/ssh/ssh_host_key

WORKDIR /etc/ssh
COPY docker/var/authorized_keys_rbe .
COPY docker/var/authorized_keys_cew .
COPY docker/var/authorized_keys_wbh .
COPY docker/bookworm-datatransfer/setup-datatransfer-users.sh .
COPY docker/bookworm-datatransfer/sshd_config_2 .
RUN chmod 700 setup-datatransfer-users.sh \
        && ./setup-datatransfer-users.sh \
        && rm setup-datatransfer-users.sh \
        && cat sshd_config_2 >>sshd_config \
        && rm sshd_config_2 \
        && chmod 444 /etc/ssh/authorized_keys_*

VOLUME ["/etc/ssh"]

EXPOSE 22

CMD ["/usr/sbin/sshd", "-D", "-e"]
