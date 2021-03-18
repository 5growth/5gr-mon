### Project information
5GROWTH is funded by the European Union’s Research and Innovation Programme Horizon 2020 under Grant Agreement no. 856709


Call: H2020-ICT-2019. Topic: ICT-19-2019. Type of action: RIA. Duration: 30 Months. Start date: 1/6/2019


<p align="center">
<img src="https://upload.wikimedia.org/wikipedia/commons/b/b7/Flag_of_Europe.svg" width="100px" />
</p>

<p align="center">
<img src="https://5g-ppp.eu/wp-content/uploads/2019/06/5Growth_rgb_horizontal.png" width="300px" />
</p>
 



# List of components

## Table of Contents

1. [Monitoring Core](#monitoring-core)
2. [Config Manager](#config-manager)
3. [Remote virtual machine agent](#rvm-agent)
4. [ELK stack](#elk-stack)
    1. [Kibana Dashboard API Json Generator](#dep-kibana-dashboards-telca)
    2. [IPFIX to Kafka parser and exporter](#exp-ipfix-telca)
    3. [Packetbeat container](#exp-packetbeat-telca)

## 1. Monitoring Core <a name="monitoring-core"></a>

This contains scripts and configuration file usable to deploy the basic infrastructure
needed for monitoring.

Folder: `mon-core`


## 2. Config Manager <a name="config-manager"></a>

The Prometheus Config Manager acts as a relay point enabling the configuration of a
Prometheus + Alertmanager + Grafana deployment from a single RESTful HTTP endpoint.

Folder: `config-manager-nxw`



## 3. Remote virtual machine agent <a name="rvm-agent"></a>
Mirantis TBC

RVM agent features:
- Bash script code execution on remote VM and providing the execution result
- Keepalive mechanism
- RVM agent Prometheus Collectors management (create/delete)
- RVM agent configuration management (avoid config loss after restart)

Folder: `rvm_agent`

## 4. ELK stack <a name="elk-stack"></a>
Telcaria TBC
### 4.1. Kibana Dashboard API Json Generator <a name="dep-kibana-dashboards-telca"></a>
Telcaria TBC

Folder: `dep-kibana-dashboards-telca`

### 4.2. IPFIX to Kafka parser and exporter <a name="exp-ipfix-telca"></a>
Telcaria TBC

An exporter used to collect, parser and transform [IPFIX](https://www.iana.org/assignments/ipfix/ipfix.xhtml) data to JSON format.

Folder: `exp-ipfix-telca`

### 4.3. Packetbeat container <a name="exp-packetbeat-telca"></a>
Telcaria TBC

Packetbeat container works by capturing the network traffic

Folder: `exp-packetbeat-telca`


