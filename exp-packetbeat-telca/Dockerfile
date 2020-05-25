FROM docker.elastic.co/beats/packetbeat:7.6.1
COPY conf/packetbeat.yml /usr/share/packetbeat/packetbeat.yml
USER root
RUN chown root:packetbeat /usr/share/packetbeat/packetbeat.yml
USER packetbeat