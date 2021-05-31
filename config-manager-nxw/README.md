### 5Growth Configuration Manager

### Installation

In the `config-manager-nxw` folder, run `mvn clean package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true` (requires jdk >=8, mvn >=3.0.0)

## Configuration

```
In the same folder as above, in subfolder `src/main/resources` you can find a `config.properties` file.
This contains the configuration for this application.

server.port=8989		Port of  monitoring platform service
server.ip=192.168.1.1           IP address of  monitoring platform service


rvmagent.identifiermode=hostname    rvmagent identifier mode

mode "hostname"    rvmagent identifier mode is based on hostname
mode "ID"          rvmagent identifier mode is set by configuration namager

prometheus.config=5growth.5gr-mon/mon-core/prometheus/prometheus.yml
prometheus.alertRules=5growth.5gr-mon/mon-core/prometheus/alert.rules
prometheus.alertManager=5growth.5gr-mon/mon-core/alertmanager/alertmanager.yml
logstash.config=5growth.5gr-mon/mon-core/logstash/logstash.conf
prometheus.calculateRules=5growth.5gr-mon/mon-core/prometheus/calculate.rules

These options are paths to configuration files.

grafana.host=mon_grafana                        IP address of  Grafana
grafana.port=3000                               Port of Grafana
grafana.token=                                  token for access to Grafana

prometheus.host=mon_prometheus                  IP address of  Prometheus
prometheus.port=9090                            Port of Prometheus

alertmanager.host=mon_alertmanager              IP address of  AlertManager
alertmanager.port=9093                          Port of AlertManager

# levels: TRACE, DEBUG, INFO, WARN, ERROR
logging.level=INFO                              Level of logs
prometheus.PushGateway.group.id=prometheus      Kafka's group id for Prometheus PushGateway   
prometheus.PushGateway.topic=prometheus         Kafka's topic for Prometheus PushGateway
kafka.bootstrap.server=192.168.100.220:9092     Kafka's bootstrap server

elkstack.host=127.0.0.1                          IP address of Log pipeline namager
elkstack.port=8987                               Port of Log pipeline namager
```