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

# Install packages
RUN apt-get install -y openjdk-8-jre apt-transport-https wget jq

# Set JAVA_HOME
RUN sed -i '/^JAVA_HOME.*/d' /etc/environment && echo JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd4 | tee -a /etc/environment > /dev/null

# Install ELK Stack
RUN wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | apt-key add -
ADD https://artifacts.elastic.co/downloads/logstash/logstash-7.5.0.deb /tmp
RUN dpkg -i /tmp/logstash-7.5.0.deb
RUN rm /tmp/*.deb

# Create /usr/bin/logstash_pipeline_manager directory
RUN mkdir -p /usr/bin/logstash_pipeline_manager
RUN chmod 755 /usr/bin/logstash_pipeline_manager
RUN chown root:root /usr/bin/logstash_pipeline_manager

# Copy auxiliary scripts
COPY files/create_logstash_pipeline.sh /usr/bin/logstash_pipeline_manager/create_logstash_pipeline.sh
COPY files/delete_logstash_pipeline.sh /usr/bin/logstash_pipeline_manager/delete_logstash_pipeline.sh
COPY files/logstash_pipeline_manager.py /usr/bin/logstash_pipeline_manager/logstash_pipeline_manager.py
RUN chmod +x /usr/bin/logstash_pipeline_manager/create_logstash_pipeline.sh
RUN chmod +x /usr/bin/logstash_pipeline_manager/delete_logstash_pipeline.sh
RUN chmod +x /usr/bin/logstash_pipeline_manager/logstash_pipeline_manager.py

# Configure Logstash
RUN rm /etc/logstash/pipelines.yml && touch /etc/logstash/pipelines.yml
RUN sed -i -e 's/# http.host: "127.0.0.1"/http.host: "0.0.0.0"/' /etc/logstash/logstash.yml
RUN sed -i -e 's/# config.reload.automatic: false/config.reload.automatic: true/' /etc/logstash/logstash.yml
RUN sed -i -e 's/# config.reload.interval: 3s/config.reload.interval: 1s/' /etc/logstash/logstash.yml
RUN sed -i -e 's/#xpack.monitoring.enabled: false/xpack.monitoring.enabled: true/' /etc/logstash/logstash.yml
RUN sed -i -e 's/#xpack.monitoring.elasticsearch.username: logstash_system/xpack.monitoring.elasticsearch.username: elastic/' /etc/logstash/logstash.yml
RUN sed -i -e 's/#xpack.monitoring.elasticsearch.password: password/xpack.monitoring.elasticsearch.password: changeme/' /etc/logstash/logstash.yml

# Copy run_logstash.sh script
COPY files/run_logstash.sh /usr/bin/logstash_pipeline_manager
RUN chmod +x /usr/bin/logstash_pipeline_manager/run_logstash.sh

# Copy dummy pipelines
COPY files/pipelines.yml /etc/logstash
RUN mkdir -p /etc/logstash/conf.d
COPY files/test.conf /etc/logstash/conf.d

# Entrypoint script
COPY files/update_hosts.sh /
RUN chmod +x update_hosts.sh
COPY files/entrypoint.sh /
RUN chmod +x entrypoint.sh
