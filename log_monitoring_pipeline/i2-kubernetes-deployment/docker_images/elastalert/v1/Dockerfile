FROM bitsensor/elastalert:3.0.0-beta.0

# Create working folder
RUN mkdir -p /opt/elastalert/rules

# Copy config files
COPY files/elastalert.yaml /opt/elastalert/config.yaml
COPY files/elastalert.yaml /opt/elastalert-server/config.yaml

COPY files/elastalert_config.json /opt/elastalert/config.json
COPY files/elastalert_config.json /opt/elastalert-server/config.json

COPY files/filebeat_rule.yaml /opt/elastalert/rules/filebeat_rule.yaml
