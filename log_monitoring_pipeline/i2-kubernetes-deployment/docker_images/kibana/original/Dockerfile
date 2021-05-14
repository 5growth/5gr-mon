FROM ubuntu:xenial

# Install packages
RUN apt update
RUN apt-get update
RUN apt-get install -y openjdk-8-jre openjdk-8-jdk curl nano git iputils-ping apt-transport-https wget nginx jq maven

# Set JAVA_HOME
RUN sed -i '/^JAVA_HOME.*/d' /etc/environment && echo JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd4 | tee -a /etc/environment > /dev/null

# Install ELK Stack
RUN wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | apt-key add -
ADD https://artifacts.elastic.co/downloads/kibana/kibana-7.5.0-amd64.deb /tmp
RUN dpkg -i /tmp/kibana-7.5.0-amd64.deb
RUN rm /tmp/*.deb

# Configure Kibana
RUN sed -i -e 's/#server.name: "your-hostname"/server.name: kibana/' /etc/kibana/kibana.yml
RUN sed -i -e 's/#elasticsearch.username: "kibana"/elasticsearch.username: elastic/' /etc/kibana/kibana.yml
RUN sed -i -e 's/#elasticsearch.password: "pass"/elasticsearch.password: changeme/' /etc/kibana/kibana.yml
RUN echo "xpack.security.enabled: false" | tee -a /etc/kibana/kibana.yml > /dev/null

# Entrypoint script
COPY files/entrypoint.sh /
RUN chmod +x entrypoint.sh
