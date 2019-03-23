#!/usr/bin/env bash

sudo pacman --noconfirm -Syu

# Docker needs iptables 1.8.0 for "docker run -p"
sudo pacman --noconfirm -U \
    https://archive.archlinux.org/repos/2018/11/15/core/os/x86_64/iptables-1:1.8.0-1-x86_64.pkg.tar.xz

sudo pacman --noconfirm -S snapper

sudo pacman --noconfirm -S nfs-utils
sudo systemctl enable rpcbind
sudo systemctl start rpcbind
sudo bash -c 'cat >>/etc/fstab <<EOF
46.38.248.210:/voln80726a1	/mnt/backup nfs rw,rsize=1048576,wsize=1048576  0   0
EOF'

setup_user_w_sudo() {
    local name=$1
    getent passwd ${name}
    if [[ $? -eq 2 ]]
    then
        groupadd ${name}
        useradd -m -d /home/${name} -s /bin/bash -g ${name} ${name}
    fi
    mkdir /home/${name}/.ssh
    touch /home/${name}/.ssh/authorized_keys
    chown -R ${name}:${name} /home/${name}
    chmod -R 700 /home/${name}/.ssh
    chmod -R 500 /home/${name}/.ssh/*
    cat >> /etc/sudoers.d/${name} <<EOF
${name} ALL=(ALL) NOPASSWD: ALL
EOF
}

setup_user_w_sudo cew
cat >>/home/cew/.ssh/authorized_keys <<EOF
ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAIEAwBv7zakSgsO5Y6IPp5nansr3InpMUjHVpLIbxLh8j38YRobX7wFyFEwXmIYPaS1v2hwpGWJa7/lZsBFoV901e57XrLuGoN2OBE7zCkb0D471gFLxX1XvzlInyhXW8fLHOlgQBoRj3ik2r3seMh3xM3FIDANvJ8owQl6p7xJSOC0= ceoffice_rsa-key-20091106
EOF

setup_user_w_sudo rbe
cat >>/home/rbe/.ssh/authorized_keys <<EOF
ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA0QLYbvts23FUxRI6WUMMEN+kVNHMa1H4Agtfe/9+u7fQd/VmPMK5vn57gWfSX3A0iMZgvRvdeKRwbfOjbPAuWQGKWN5yKmJ+v4Q8VH1ryQrsPaBMi939/vs4yrwvceMT5NSm9OUZuNWkYWhX33osYChP2k9NY13S4Ia6yacSQ1YeY3/12XuPibgeS5mnKIFFkSrqtDwo7ms88cXlr1xPie53MAvjGj5eVa9SFBHIXE3RIgnVmx7WbJMTwl9gUQoCIhQFTY/L9vyB2L7GcGopMktnOiabvyKbDN45TZg5oKKoP8BBFVWOesVxhfTqFF0qY0CSbl/fV4/mLU6OrMySSQ== rbemac
EOF

# TODO In Container für SSH mit Zugriff auf die Volumes
setup_user_w_sudo bookworm
cat >>/home/bookworm/.ssh/authorized_keys <<EOF
ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAIEAmXh30WCFduH6OjsdIrLT0pT6TXOgKoeBClg8ixkOT4bLd+ePYa0gatsrsZ4DWMPBQBytgHeOH053tsgn9v1jDyLAyv4QTUxNMZjxMHd3ZCCj3jj5sxg0C5dx43egHbysi4G58AuoTIz+CmJ5hOxDA6Qt4QHxesWrVEBV+pi/C/8= wbh-rsa-key-20121105
EOF

exit 0
