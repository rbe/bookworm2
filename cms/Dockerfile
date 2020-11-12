FROM joomla:3.9.22-php7.4-fpm-alpine
RUN apk update
RUN apk add --no-cache $PHPIZE_DEPS
RUN pecl install xdebug \
    && docker-php-ext-enable xdebug
#RUN apk add php7-pecl-xdebug
RUN apk add composer
