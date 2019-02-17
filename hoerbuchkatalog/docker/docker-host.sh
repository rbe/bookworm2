#!/usr/bin/env bash

sudo pacman -Syu --noconfirm nfs-utils
sudo systemctl enable rpcbind
sudo systemctl start rpcbind
sudo bash -c 'cat >>/etc/fstab <<EOF
46.38.248.210:/voln80726a1	/mnt/backup nfs rw,rsize=1048576,wsize=1048576  0   0
EOF'

getent passwd cew
if [[ $? -eq 2 ]]
then
    groupadd cew
    useradd -m -d /home/cew -s /bin/bash -g cew cew
    mkdir /home/cew/.ssh
    cat >>/home/cew/.ssh/authorized_keys <<EOF
ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAIEAwBv7zakSgsO5Y6IPp5nansr3InpMUjHVpLIbxLh8j38YRobX7wFyFEwXmIYPaS1v2hwpGWJa7/lZsBFoV901e57XrLuGoN2OBE7zCkb0D471gFLxX1XvzlInyhXW8fLHOlgQBoRj3ik2r3seMh3xM3FIDANvJ8owQl6p7xJSOC0= ceoffice_rsa-key-20091106
EOF
    chown -R cew:cew /home/cew
    chmod -R 700 /home/cew/.ssh
    chmod -R 500 /home/cew/.ssh/*
    cat >> /etc/sudoers.d/cew <<EOF
cew ALL=(ALL) NOPASSWD: ALL
EOF
fi

getent passwd rbe
if [[ $? -eq 2 ]]
then
    groupadd rbe
    useradd -m -d /home/rbe -s /bin/bash -g rbe rbe
    mkdir /home/rbe/.ssh
    cat >>/home/rbe/.ssh/authorized_keys <<EOF
ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA0QLYbvts23FUxRI6WUMMEN+kVNHMa1H4Agtfe/9+u7fQd/VmPMK5vn57gWfSX3A0iMZgvRvdeKRwbfOjbPAuWQGKWN5yKmJ+v4Q8VH1ryQrsPaBMi939/vs4yrwvceMT5NSm9OUZuNWkYWhX33osYChP2k9NY13S4Ia6yacSQ1YeY3/12XuPibgeS5mnKIFFkSrqtDwo7ms88cXlr1xPie53MAvjGj5eVa9SFBHIXE3RIgnVmx7WbJMTwl9gUQoCIhQFTY/L9vyB2L7GcGopMktnOiabvyKbDN45TZg5oKKoP8BBFVWOesVxhfTqFF0qY0CSbl/fV4/mLU6OrMySSQ== rbemac
EOF
    chown -R rbe:rbe /home/rbe
    chmod -R 700 /home/rbe/.ssh
    chmod -R 500 /home/rbe/.ssh/*
    cat >> /etc/sudoers.d/rbe <<EOF
rbe ALL=(ALL) NOPASSWD: ALL
EOF
fi

# TODO In Container für SSH mit Zugriff auf die Volumes
getent passwd bookworm
if [[ $? -eq 2 ]]
then
    groupadd bookworm
    useradd -m -d /home/bookworm -s /bin/bash -g bookworm bookworm
    usermod -G bookworm cew
    usermod -G bookworm rbe
    cat >>/home/bookworm/.ssh/authorized_keys <<EOF
ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAIEAmXh30WCFduH6OjsdIrLT0pT6TXOgKoeBClg8ixkOT4bLd+ePYa0gatsrsZ4DWMPBQBytgHeOH053tsgn9v1jDyLAyv4QTUxNMZjxMHd3ZCCj3jj5sxg0C5dx43egHbysi4G58AuoTIz+CmJ5hOxDA6Qt4QHxesWrVEBV+pi/C/8= wbh-rsa-key-20121105
EOF
    chown -R bookworm:bookworm /home/rbe
    chmod -R 700 /home/rbe/.ssh
    chmod -R 500 /home/rbe/.ssh/*
fi

exit 0
