package it.nextworks.nfvmano.configmanager.sb.kafkaRVMAgent.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.nextworks.nfvmano.configmanager.rvmagent.model.StructureKeyValue;

import java.util.List;
import java.util.Objects;

public class KafkaAddPrometheusCollectorResponse extends KafkaGeneralResponse {
    @JsonProperty("collector_id")
    private String collectorId;
    @JsonProperty("agent_id")
    private String rvmAgentId;
    private String host;
    @JsonProperty("prometheus_topic")
    private String prometheusTopic;
    private String port;
    @JsonProperty("node_url_suffix")
    private String nodeUrlSuffix;
    @JsonProperty("prometheus_job")
    private String prometheusJob;
    private String interval;
    private List<StructureKeyValue> labels;
    private List<StructureKeyValue> params;

    public KafkaAddPrometheusCollectorResponse() {
    }

    public KafkaAddPrometheusCollectorResponse(String rvmAgentId, String host, String prometheusTopic, String port, String nodeUrlSuffix, String prometheusJob, String interval, List<StructureKeyValue> labels) {

        this.rvmAgentId = rvmAgentId;
        this.host = host;
        this.prometheusTopic = prometheusTopic;
        this.port = port;
        this.nodeUrlSuffix = nodeUrlSuffix;
        this.prometheusJob = prometheusJob;
        this.interval = interval;
        this.labels = labels;
    }

    public String getCollectorId() {
        return host + ":" + port;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }



    public String getRvmAgentId() {
        return rvmAgentId;
    }

    public void setRvmAgentId(String rvmAgentId) {
        this.rvmAgentId = rvmAgentId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPrometheusTopic() {
        return prometheusTopic;
    }

    public void setPrometheusTopic(String prometheusTopic) {
        this.prometheusTopic = prometheusTopic;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getNodeUrlSuffix() {
        return nodeUrlSuffix;
    }

    public void setNodeUrlSuffix(String nodeUrlSuffix) {
        this.nodeUrlSuffix = nodeUrlSuffix;
    }

    public String getPrometheusJob() {
        return prometheusJob;
    }

    public void setPrometheusJob(String prometheusJob) {
        this.prometheusJob = prometheusJob;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public List<StructureKeyValue> getLabels() {
        return labels;
    }

    public void setLabels(List<StructureKeyValue> labels) {
        this.labels = labels;
    }

    public List<StructureKeyValue> getParams() {
        return params;
    }

    public void setParams(List<StructureKeyValue> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "KafkaAddPrometheusCollectorResponse{" +
                "collectorId='" + collectorId + '\'' +
                ", rvmAgentId='" + rvmAgentId + '\'' +
                ", host='" + host + '\'' +
                ", prometheusTopic='" + prometheusTopic + '\'' +
                ", port='" + port + '\'' +
                ", nodeUrlSuffix='" + nodeUrlSuffix + '\'' +
                ", prometheusJob='" + prometheusJob + '\'' +
                ", interval='" + interval + '\'' +
                ", labels=" + labels +
                ", params=" + params +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KafkaAddPrometheusCollectorResponse that = (KafkaAddPrometheusCollectorResponse) o;
        return Objects.equals(collectorId, that.collectorId) &&
                Objects.equals(rvmAgentId, that.rvmAgentId) &&
                Objects.equals(host, that.host) &&
                Objects.equals(prometheusTopic, that.prometheusTopic) &&
                Objects.equals(port, that.port) &&
                Objects.equals(nodeUrlSuffix, that.nodeUrlSuffix) &&
                Objects.equals(prometheusJob, that.prometheusJob) &&
                Objects.equals(interval, that.interval) &&
                Objects.equals(labels, that.labels) &&
                Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectorId, rvmAgentId, host, prometheusTopic, port, nodeUrlSuffix, prometheusJob, interval, labels, params);
    }

}
