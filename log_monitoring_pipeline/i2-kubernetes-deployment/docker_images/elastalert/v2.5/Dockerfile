FROM bitsensor/elastalert:3.0.0-beta.0

# Copy config files
COPY files/elastalert.yaml /opt/elastalert/config.yaml
COPY files/elastalert_config.json /opt/elastalert/config.json
# Install jq for json in command line
USER root
RUN apk update
RUN apk add jq
