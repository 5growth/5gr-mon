# List of components

## Table of Contents

1. [Monitoring Core](#monitoring-core)
2. [Config Manager](#config-manager)
3. [Exporters](#exporters)
    1. [CDN App exporter](#cdn-app-exporter)
    2. [ODL exporter](#odl-ex)
    3. [ONOS exporter](#onos-ex)
    4. [OpenStack exporter](#os-ex)
    5. [Kubernetes exporter](#kube-ex)
    6. [VM exporter](#vm-ex)
    7. [Basic VM exporter](#basic-vm-ex)
    8. [Optical WIM exporter](#chaul-wim-ex)
    9. [Optical radio WIM exporter](#radio-wim-ex)
4. [App instrumentation example](#app-ex)
5. [Remote virtual machine agent](#rvm-agent)
6. [ELK stack](#elk-stack)
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


## 3. Exporters <a name="exporters"></a>


### 3.1. CDN App exporter <a name="cdn-app-exporter"></a>

ATOS TBC

Folder: `exp-app-cdn-atos`


### 3.2. ODL exporter <a name="odl-ex"></a>

ATOS TBC

Folder: `exp-odl-sonata-atos`


### 3.3. ONOS exporter <a name="onos-ex"></a>

SSSA TBC

Folder: `exp-onos-sssa`


### 3.4. OpenStack exporter <a name="os-ex"></a>

ATOS TBC

Folder: `exp-os-sonata-atos`


### 3.5. Kubernetes exporter <a name="kube-ex"></a>

MIRANTIS TBC

Folder: `exp-vim-kubernetes-mirantis`


### 3.6. VM data exporter <a name="vm-ex"></a>

CTTC TBC

Folder: `exp-vm-cttc`


### 3.7. Basic VM data exporter <a name="basic-vm-ex"></a>

This contains a compilation of general-purpose VM data exporters, appropriate for
general use or as a fallback.

Folder: `exp-vm-nxw`


### 3.8. Optical Crosshaul WIM exporter <a name="chaul-wim-ex"></a>

CTTC TBC

Folder: `exp-wim-cttc`


### 3.9. Optical radio WIM exporter <a name="radio-wim-ex"></a>

SSSA TBC

Folder: `exp-wim-optical-radio-sssa`


## 4. App instrumentation example <a name="app-ex"></a>

This contains an example app instrumented to directly export data to prometheus.

Folder: `example-instrumented-app-nxw`

## 5. Remote virtual machine agent <a name="rvm-agent"></a>
Mirantis TBC

RVM agent features:
- Bash script code execution on remote VM and providing the execution result
- Keepalive mechanism
- RVM agent Prometheus Collectors management (create/delete)
- RVM agent configuration management (avoid config loss after restart)

Folder: `rvm_agent`

## 6. ELK stack <a name="elk-stack"></a>
Telcaria TBC
### 6.1. Kibana Dashboard API Json Generator <a name="dep-kibana-dashboards-telca"></a>
Telcaria TBC

Folder: `dep-kibana-dashboards-telca`

### 6.2. IPFIX to Kafka parser and exporter <a name="exp-ipfix-telca"></a>
Telcaria TBC

An exporter used to collect, parser and transform [IPFIX](https://www.iana.org/assignments/ipfix/ipfix.xhtml) data to JSON format.

Folder: `exp-ipfix-telca`

### 6.3. Packetbeat container <a name="exp-packetbeat-telca"></a>
Telcaria TBC

Packetbeat container works by capturing the network traffic

Folder: `exp-packetbeat-telca`


-------------------
### Project information
Call: H2020-ICT-2019. Topic: ICT-19-2019. Type of action: RIA. Duration: 30 Months. Start date: 1/6/2019
![5GROWTH logo](https://5g-ppp.eu/wp-content/uploads/2019/06/5Growth_rgb_horizontal.png)

<p align="center">
<img src="https://upload.wikimedia.org/wikipedia/commons/b/b7/Flag_of_Europe.svg" width="200px" />
</p>

