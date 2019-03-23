#!/usr/bin/env ash

setup_user() {
    local name=$1
    getent passwd ${name}
    if [[ $? -eq 2 ]]
    then
        addgroup ${name}
        adduser -DHh /tmp -s /bin/false -G ${name} ${name}
    fi
    #/home/${name}/.ssh/authorized_keys
    touch /etc/ssh/authorized_keys_${name}
    chmod 444 /etc/ssh/authorized_keys_${name}
    passwd -u ${name}
}

setup_user cew
cat >>/etc/ssh/authorized_keys_cew <<EOF
ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAIEAwBv7zakSgsO5Y6IPp5nansr3InpMUjHVpLIbxLh8j38YRobX7wFyFEwXmIYPaS1v2hwpGWJa7/lZsBFoV901e57XrLuGoN2OBE7zCkb0D471gFLxX1XvzlInyhXW8fLHOlgQBoRj3ik2r3seMh3xM3FIDANvJ8owQl6p7xJSOC0= ceoffice_rsa-key-20091106
EOF

setup_user rbe
cat >>/etc/ssh/authorized_keys_rbe <<EOF
ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA0QLYbvts23FUxRI6WUMMEN+kVNHMa1H4Agtfe/9+u7fQd/VmPMK5vn57gWfSX3A0iMZgvRvdeKRwbfOjbPAuWQGKWN5yKmJ+v4Q8VH1ryQrsPaBMi939/vs4yrwvceMT5NSm9OUZuNWkYWhX33osYChP2k9NY13S4Ia6yacSQ1YeY3/12XuPibgeS5mnKIFFkSrqtDwo7ms88cXlr1xPie53MAvjGj5eVa9SFBHIXE3RIgnVmx7WbJMTwl9gUQoCIhQFTY/L9vyB2L7GcGopMktnOiabvyKbDN45TZg5oKKoP8BBFVWOesVxhfTqFF0qY0CSbl/fV4/mLU6OrMySSQ== rbemac
EOF

setup_user wbh
cat >>/etc/ssh/authorized_keys_wbh <<EOF
ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAIEAmXh30WCFduH6OjsdIrLT0pT6TXOgKoeBClg8ixkOT4bLd+ePYa0gatsrsZ4DWMPBQBytgHeOH053tsgn9v1jDyLAyv4QTUxNMZjxMHd3ZCCj3jj5sxg0C5dx43egHbysi4G58AuoTIz+CmJ5hOxDA6Qt4QHxesWrVEBV+pi/C/8= wbh-rsa-key-20121105
ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAIEAwBv7zakSgsO5Y6IPp5nansr3InpMUjHVpLIbxLh8j38YRobX7wFyFEwXmIYPaS1v2hwpGWJa7/lZsBFoV901e57XrLuGoN2OBE7zCkb0D471gFLxX1XvzlInyhXW8fLHOlgQBoRj3ik2r3seMh3xM3FIDANvJ8owQl6p7xJSOC0= ceoffice_rsa-key-20091106
ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA0QLYbvts23FUxRI6WUMMEN+kVNHMa1H4Agtfe/9+u7fQd/VmPMK5vn57gWfSX3A0iMZgvRvdeKRwbfOjbPAuWQGKWN5yKmJ+v4Q8VH1ryQrsPaBMi939/vs4yrwvceMT5NSm9OUZuNWkYWhX33osYChP2k9NY13S4Ia6yacSQ1YeY3/12XuPibgeS5mnKIFFkSrqtDwo7ms88cXlr1xPie53MAvjGj5eVa9SFBHIXE3RIgnVmx7WbJMTwl9gUQoCIhQFTY/L9vyB2L7GcGopMktnOiabvyKbDN45TZg5oKKoP8BBFVWOesVxhfTqFF0qY0CSbl/fV4/mLU6OrMySSQ== rbemac
EOF

exit 0
