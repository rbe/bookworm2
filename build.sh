#!/usr/bin/env bash

PROJECT=~/project/wbh.bookworm
BRANCH=develop

#        --single-branch --branch develop --depth 1 \
if [[ ! -d ${PROJECT} ]]
then
    git clone \
        git@bitbucket.org:artofcoding/bookworm2.git \
        ${PROJECT}
fi

git pull --rebase --autostash \
    && ./mvnw -P aoc.platform,bookworm.hoerbuchkatalog,bookworm.documentation,bookworm.assembly \
              clean package \
    && cd docker \
    && ./docker-build-hoerbuchkatalog.sh 1

exit 0
