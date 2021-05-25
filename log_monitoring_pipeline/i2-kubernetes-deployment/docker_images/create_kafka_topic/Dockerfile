FROM ubuntu:xenial

# Install packages
RUN apt-get update
RUN apt-get install -y openjdk-8-jdk curl locales nano git iputils-ping

# Install Python 3.5.2
RUN apt-get install -y build-essential checkinstall
RUN apt-get install -y libreadline-gplv2-dev libncursesw5-dev libssl-dev libsqlite3-dev tk-dev libgdbm-dev libc6-dev libbz2-dev
ADD https://www.python.org/ftp/python/3.5.2/Python-3.5.2.tgz /usr/src
RUN cd /usr/src && tar xzf /usr/src/Python-3.5.2.tgz
#WORKDIR /usr/src/Python-3.5.2

RUN cd /usr/src/Python-3.5.2 \
	&& ./configure --enable-optimizations \
	&& make altinstall

WORKDIR /

# Install pip3 8.1.1
RUN apt-get install -y python3-pip
RUN pip3 install --upgrade pip==8.1.1

# Build locales database
RUN sed -i -e 's/# en_US.UTF-8 UTF-8/en_US.UTF-8 UTF-8/' /etc/locale.gen && \
    dpkg-reconfigure --frontend=noninteractive locales && \
    update-locale LANG=en_US.UTF-8
ENV LANG en_US.UTF-8

# Install Python libraries
RUN export LC_ALL="en_US.UTF-8"
RUN export LC_CTYPE="en_US.UTF-8"

COPY files/requirements.txt /
RUN pip3 install -r requirements.txt


# Copy files
COPY files/create_kafka_topic.py /

# Entrypoint script
COPY files/update_hosts.sh /
RUN chmod +x update_hosts.sh
COPY files/entrypoint.sh /
RUN chmod +x entrypoint.sh
ENTRYPOINT /bin/bash entrypoint.sh
