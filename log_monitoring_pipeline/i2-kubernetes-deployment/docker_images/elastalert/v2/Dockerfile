FROM bitsensor/elastalert:3.0.0-beta.0

# Copy config files
COPY files/elastalert.yaml /opt/elastalert/config.yaml
COPY files/elastalert_config.json /opt/elastalert/config.json

# TODO: this can be done by using K8s ConfigMaps, no need to build ???
