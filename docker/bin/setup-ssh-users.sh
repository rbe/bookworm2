#!/usr/bin/env ash

setup_user() {
    local name=$1
    shift
    local groups=$*
    getent passwd ${name}
    if [[ -n "${name}" ]]
    then
        echo "Adding group ${name}"
        addgroup ${name}
        echo "Adding user ${name}"
        adduser -DHh /tmp -s /bin/false -G ${name} ${name}
    fi
    passwd -u ${name}
    passwd -u ${name}
    if [[ -n "${groups}" ]]
    then
        for g in ${groups}
        do
            echo "Adding user ${name} to group ${g}"
            usermod -G ${g} ${name}
        done
    fi
}

addgroup -g 4801 bookworm

setup_user cew bookworm
setup_user rbe bookworm
setup_user wbh bookworm

exit 0
