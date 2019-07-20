#!/bin/bash

DOCKER_BACKUP_DIR="/var/lib/docker/backup"
CONTAINER_BACKUP_DIR="${DOCKER_BACKUP_DIR}/joomla"

mkdir -p ${CONTAINER_BACKUP_DIR}
chmod 770 ${CONTAINER_BACKUP_DIR}
rm -f ${CONTAINER_BACKUP_DIR}/*

db_container="wbhonline_joomla-db_1"
db_database="joomla"
db_tar="${CONTAINER_BACKUP_DIR}/mysql-${db_database}-$(date +%Y%m%d_%H%M%S).sql.gz"
echo "Dumping MySQL database ${db_container}/${db_database}"
docker exec ${db_container} \
    /usr/bin/mysqldump ${db_database} \
    | gzip -9 >${db_tar}
chmod 660 ${db_tar}
chgrp bookworm ${db_tar}

cms_container="wbhonline_joomla_1"
cms_container_backup="${cms_container}_container"
cms_tar="${CONTAINER_BACKUP_DIR}/${cms_container_backup}-$(date +%Y%m%d_%H%M%S).tar"
echo "Committing ${cms_container} container as ${cms_container_backup}"
docker commit --pause=false ${cms_container} ${cms_container_backup}
echo "Saving container to ${cms_tar}"
docker save -o ${cms_tar} ${cms_container_backup}
echo "Compressing ${cms_tar}"
gzip -9 ${cms_tar}
chmod 660 ${cms_tar}.gz
chgrp bookworm ${cms_tar}.gz

volume_tar="${cms_container}_volume-$(date +%Y%m%d_%H%M%S).tar.gz"
echo "Backing up volumes from ${cms_container} to ${volume_tar}"
docker run --rm \
    --volumes-from ${cms_container} \
    -v ${CONTAINER_BACKUP_DIR}:/backup \
    alpine:3.9 \
    tar \
        --exclude=var/www/html/{images,joomlatools-files}/* \
        -czf /backup/${volume_tar} \
        /var/www/html
sudo chmod 660 ${CONTAINER_BACKUP_DIR}/${volume_tar}
sudo chgrp bookworm ${CONTAINER_BACKUP_DIR}/${volume_tar}

exit 0
