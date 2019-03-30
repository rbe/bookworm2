#!/usr/bin/env ash

setup_user() {
    local name=$1
    getent passwd ${name}
    if [[ $? -eq 2 ]]
    then
        addgroup ${name}
        adduser -DHh /tmp -s /bin/false -G ${name} ${name}
    fi
    passwd -u ${name}
}

setup_user cew
setup_user rbe
setup_user wbh

exit 0
