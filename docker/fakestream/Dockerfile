FROM apache/nifi:latest

LABEL maintainer="Brian Law <bpl.law@gmail.com>"

USER root

RUN apt-get -y update && \
    apt-get -y install python3-dev && \
    apt-get -y install python3-pip && \
    apt-get -y install vim

COPY requirements.txt /tmp/requirements.txt

RUN pip3 install -r /tmp/requirements.txt

#COPY ../scripts /tmp/scripts

#RUN chown nifi:nifi /tmp/scripts/*