FROM centos:latest

RUN yum install -y wget tar openssh-server openssh-clients sysstat sudo which openssl hostname
RUN yum install -y java-1.8.0-openjdk-headless
RUN yum install -y epel-release &&\
    yum install -y jq &&\
    yum install -y python3 &&\
    yum install -y nmap-ncat 

RUN wget https://bootstrap.pypa.io/get-pip.py
RUN python3 get-pip.py
RUN pip3 install awscli

ENV SCALA_VERSION 2.12
ENV KAFKA_VERSION 2.7.1

ENV KAFKA_HOME /opt/kafka_2.12-2.7.1

RUN yum -y update && yum -y install tar gzip wget

RUN curl http://mirrors.gigenet.com/apache/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz | tar -zx -C /opt

RUN mkdir -p /opt/connectors 
ADD msk-data-generator-0.4-jar-with-dependencies.jar /opt/connectors

ADD connect-distributed.properties /opt/connect-distributed.properties

# include all start scripts
ADD start-kafka-connect.sh /opt/start-kafka-connect.sh

RUN mkdir -p /opt/logs
RUN chmod 777 /opt/start-kafka-connect.sh

# cleanup
RUN yum clean all;

# EXPOSE 8083
EXPOSE 8083
USER root

ENTRYPOINT ["/opt/start-kafka-connect.sh"]
#CMD ["start"]
