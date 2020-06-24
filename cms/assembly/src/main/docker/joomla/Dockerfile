ARG JOOMLA_RELEASE
FROM joomla:${JOOMLA_RELEASE} AS base
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"

COPY php.ini /usr/local/etc/php
#COPY www.conf /usr/local/etc/php-fpm.d/www.conf
COPY --chown=www-data approxy /var/www/html/approxy/
RUN find /var/www/html/approxy -print0 -type d | xargs -0 chmod 555 \
    && find /var/www/html/approxy -print0 -type f | xargs -0 chmod 444
VOLUME ["/var/www/html"]
