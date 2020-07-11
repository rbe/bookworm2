ARG ALPINE_RELEASE
FROM alpine:${ALPINE_RELEASE} AS base
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
    && echo "${TZ}" >/etc/timezone \
    && sed -i'' -E \
        -e 's/^#(HostKey)/\1/' \
        -e 's/^#(PasswordAuthentication)/\1/' \
        -e 's/^#(ListenAddress 0|ListenAddress ::|Port)/\1/' \
        -e 's#(Port ).*#\1 22#' \
        -e 's#(PasswordAuthentication ).*#\1 no#' \
        -e 's/^(Subsystem.*sftp)(.*sftp-server.*)/#\1\2/' \
        /etc/ssh/sshd_config \
    && ssh-keygen -A \
    && ssh-keygen -t rsa -b 4096 -q -N "" -f /etc/ssh/ssh_host_key

FROM base
COPY *_authorized_keys /etc/ssh/keys/
RUN chmod 444 /etc/ssh/keys/*_authorized_keys
COPY setup-ssh-users.sh /usr/local/bin
RUN chmod 700 /usr/local/bin/setup-ssh-users.sh \
        && /usr/local/bin/setup-ssh-users.sh rbe cew \
        && rm /usr/local/bin/setup-ssh-users.sh
COPY sshd_config_2 /var/local
RUN cat /var/local/sshd_config_2 >>/etc/ssh/sshd_config \
        && rm /var/local/sshd_config_2
VOLUME ["/etc/ssh", "/etc/ssh/keys"]
EXPOSE 22
WORKDIR /etc/ssh
CMD ["/usr/sbin/sshd", "-D"]