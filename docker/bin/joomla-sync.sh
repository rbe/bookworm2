#!/bin/bash

set -o nounset
set -o errexit

cd /var/lib/docker/volumes

#sudo cp -a wbhonline_joomla_joomlatools_files/_data/* wbhonline_joomla_html/_data/joomlatools_files
sudo rsync -avz \
  wbhonline_joomla_joomlatools_files/_data/ \
  wbhonline_joomla_html/_data/joomlatools_files/

#sudo cp -a wbhonline_joomla_images/_data/* wbhonline_joomla_html/_data/images
sudo rsync -avz \
  wbhonline_joomla_images/_data/ \
  wbhonline_joomla_html/_data/images/

exit 0
