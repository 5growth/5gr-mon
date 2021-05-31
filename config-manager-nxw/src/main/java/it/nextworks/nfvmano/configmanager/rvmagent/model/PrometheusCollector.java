package it.nextworks.nfvmano.configmanager.rvmagent.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.ext.web.api.validation.ValidationException;
import it.nextworks.nfvmano.configmanager.rvmagent.messages.AddPrometheusCollectorResponse;
import it.nextworks.nfvmano.configmanager.sb.kafkaRVMAgent.model.MessageToRVMAgent;
import it.nextworks.nfvmano.configmanager.utils.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PrometheusCollector implements MessageToRVMAgent, Validated {
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
    @JsonProperty("object_type")
    private String objectType = "add_prometheus_collector";
    private List<StructureKeyValue> params;

    public PrometheusCollector() {
    }

    public PrometheusCollector(String rvmAgentId, String host, String prometheusTopic, String port, String nodeUrlSuffix, String prometheusJob, String interval, List<StructureKeyValue> labels) {
        this.rvmAgentId = rvmAgentId;
        this.host = host;
        this.prometheusTopic = prometheusTopic;
        this.port = port;
        this.nodeUrlSuffix = nodeUrlSuffix;
        this.prometheusJob = prometheusJob;
        this.interval = interval;
        this.labels = labels;
    }

    public Optional<ValidationException> validate() {
        if (rvmAgentId == null) {
            return Optional.of(new ValidationException("PrometheusCollector: alertname cannot be null"));
        }
        if (host == null) {
            return Optional.of(new ValidationException("PrometheusCollector: host cannot be null"));
        }
        return Optional.empty();
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
        return "PrometheusCollector{" +
                "collectorId='" + collectorId + '\'' +
                ", rvmAgentId='" + rvmAgentId + '\'' +
                ", host='" + host + '\'' +
                ", prometheusTopic='" + prometheusTopic + '\'' +
                ", port='" + port + '\'' +
                ", nodeUrlSuffix='" + nodeUrlSuffix + '\'' +
                ", prometheusJob='" + prometheusJob + '\'' +
                ", interval='" + interval + '\'' +
                ", labels=" + labels +
                ", objectType='" + objectType + '\'' +
                ", params=" + params +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrometheusCollector that = (PrometheusCollector) o;
        return Objects.equals(collectorId, that.collectorId) &&
                Objects.equals(rvmAgentId, that.rvmAgentId) &&
                Objects.equals(host, that.host) &&
                Objects.equals(prometheusTopic, that.prometheusTopic) &&
                Objects.equals(port, that.port) &&
                Objects.equals(nodeUrlSuffix, that.nodeUrlSuffix) &&
                Objects.equals(prometheusJob, that.prometheusJob) &&
                Objects.equals(interval, that.interval) &&
                Objects.equals(labels, that.labels) &&
                Objects.equals(objectType, that.objectType) &&
                Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectorId, rvmAgentId, host, prometheusTopic, port, nodeUrlSuffix, prometheusJob, interval, labels, objectType, params);
    }

    public String getCollectorId() {
        return host + ":" + port;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    @JsonIgnore
    public AddPrometheusCollectorResponse getAddPrometheusCollectorResponse(){
        AddPrometheusCollectorResponse addPrometheusCollectorResponse = new AddPrometheusCollectorResponse();
        addPrometheusCollectorResponse.setCollectorId(getCollectorId());
        addPrometheusCollectorResponse.setHost(host);
        addPrometheusCollectorResponse.setInterval(interval);
        addPrometheusCollectorResponse.setLabels(labels);
        addPrometheusCollectorResponse.setNodeUrlSuffix(nodeUrlSuffix);
        addPrometheusCollectorResponse.setPort(port);
        addPrometheusCollectorResponse.setPrometheusJob(prometheusJob);
        addPrometheusCollectorResponse.setPrometheusTopic(prometheusTopic);
        addPrometheusCollectorResponse.setRvmAgentId(rvmAgentId);
        return addPrometheusCollectorResponse;
    }
}
