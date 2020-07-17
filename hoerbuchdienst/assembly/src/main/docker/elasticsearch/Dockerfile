FROM docker.elastic.co/elasticsearch/elasticsearch:6.8.7
#FROM docker.elastic.co/elasticsearch/elasticsearch:7.6.1
LABEL maintainer="Ralf Bensmann <ralf@art-of-coding.eu>"
LABEL vendor="Art of Coding UG"
LABEL project="wbhonline"
ENV discovery.type=single-node
ENV cluster.name=elasticsearch-docker
ENV node.name=es01
EXPOSE 9200/tcp 9300/tcp
