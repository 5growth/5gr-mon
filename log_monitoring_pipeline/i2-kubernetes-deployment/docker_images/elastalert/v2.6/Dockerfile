FROM bitsensor/elastalert:3.0.0-beta.0

# Copy config files
COPY files/elastalert.yaml /opt/elastalert/config.yaml
COPY files/elastalert_config.json /opt/elastalert/config.json

USER node
# Set Timezone
RUN TZ=Europe/Madrid

USER root
# Set Timezone
RUN TZ=Europe/Madrid
# Install jq for json in command line
RUN apk update
RUN apk add jq
# Copy command script for rule action
RUN adduser node root
COPY files/comando.sh /opt/elastalert/rules
RUN chmod +x /opt/elastalert/rules/comando.sh
