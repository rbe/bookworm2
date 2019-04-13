#!/usr/bin/env ash

setup_user() {
    local name=$1
    shift
    local groups=$*
    getent passwd ${name}
    if [[ -n "${name}" ]]
    then
        echo "Adding user ${name}"
        adduser -DHh /tmp -s /bin/false -G bookworm ${name}
    fi
    passwd -u ${name}
#    if [[ -n "${groups}" ]]
#    then
#        for g in ${groups}
#        do
#            echo "Adding user ${name} to group ${g}"
#            usermod -G ${g} ${name}
#        done
#    fi
}

addgroup -g 4801 bookworm

setup_user cew
setup_user rbe
setup_user wbh

exit 0
