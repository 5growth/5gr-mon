#!/bin/bash
kibana_ip_address=$1
elasticsearch_ip_address=$2
host_ip_address=$3

# Configure and start PostgreSQL
/etc/init.d/postgresql start
#sudo -u postgres createuser eve
#sudo -u postgres createdb dashboards
#sudo -u postgres createdb pipelines
sudo -u postgres psql -f /tmp/script_db.sql
rm /tmp/*.sql

# Modify Dashboards manager service configuration that depends on environment variables and build the project
sed -i -e "s;baseUrl.*;baseUrl: http://$kibana_ip_address:5601;" /tmp/i2-kibana-dashboard-manager/src/main/resources/application.yml
sed -i -e "s;dashboardUrl.*;dashboardUrl: http://$host_ip_address:5601;" /tmp/i2-kibana-dashboard-manager/src/main/resources/application.yml
sed -i -e "s;elasticsearch.*;elasticsearch.ip_port: http://$elasticsearch_ip_address:9200/;" /tmp/i2-kibana-dashboard-manager/src/main/resources/application.yml

mvn clean install -DskipTests -f /tmp/i2-kibana-dashboard-manager
mv /tmp/i2-kibana-dashboard-manager/target/dashboards_manager-0.0.1-SNAPSHOT.jar /usr/bin/dashboard_manager/dashboards_manager.jar && chmod 664 /usr/bin/dashboard_manager/dashboards_manager.jar
rm -r /tmp/i2-kibana-dashboard-manager

# Start Dashboards manager service
echo "Start Dashboard manager"
/usr/bin/java -jar /usr/bin/dashboard_manager/dashboards_manager.jar > /var/log/dashboards_manager.log
#/usr/bin/java -jar /usr/bin/dashboard_manager/dashboards_manager.jar
