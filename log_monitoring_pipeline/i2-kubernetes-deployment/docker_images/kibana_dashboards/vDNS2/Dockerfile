FROM ubuntu:xenial

# Install packages
RUN apt-get update
RUN apt-get install -y openjdk-8-jre openjdk-8-jdk curl nano git iputils-ping apt-transport-https wget nginx jq maven

# Set JAVA_HOME
RUN sed -i '/^JAVA_HOME.*/d' /etc/environment && echo JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd4 | tee -a /etc/environment > /dev/null

# Create /usr/bin/dashboard_manager directory
RUN mkdir -p /usr/bin/dashboard_manager/
RUN chmod 755 /usr/bin/dashboard_manager/
RUN chown root:root /usr/bin/dashboard_manager/

# Create dashboard handler
COPY files/i2-kibana-dashboard-manager /tmp/i2-kibana-dashboard-manager

# Install and configure PostgreSQL
COPY files/script_db.sql /tmp
RUN apt-get install -y postgresql postgresql-contrib python3-psycopg2 sudo
#RUN /etc/init.d/postgresql start
#RUN sudo -u postgres createuser eve
#RUN sudo -u postgres createdb dashboards
#RUN sudo -u postgres createdb pipelines
#RUN sudo -u postgres psql -f /tmp/script_db.sql
# Test to access DB: sudo -u postgres psql eve -h 127.0.0.1 -d dashboards

# Configure Dashboards manager service
RUN mvn clean install -DskipTests -f /tmp/i2-kibana-dashboard-manager/kibana-dashboards
RUN sed -i -e 's/password.*/password: changeme/' /tmp/i2-kibana-dashboard-manager/src/main/resources/application.yml
RUN sed -i -e 's/dashboard-owner-enabled.*/dashboard-owner-enabled: false/' /tmp/i2-kibana-dashboard-manager/src/main/resources/application.yml

# Entrypoint script
COPY files/entrypoint.sh /
RUN chmod +x entrypoint.sh
