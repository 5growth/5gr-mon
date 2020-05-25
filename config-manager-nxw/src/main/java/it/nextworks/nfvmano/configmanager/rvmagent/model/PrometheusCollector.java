package it.nextworks.nfvmano.configmanager.rvmagent.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.ext.web.api.validation.ValidationException;
import it.nextworks.nfvmano.configmanager.rvmagent.messages.AddPrometheusCollectorResponse;
import it.nextworks.nfvmano.configmanager.sb.kafkaRVMAgent.model.MessageToRVMAgent;
import it.nextworks.nfvmano.configmanager.utils.Validated;

import java.util.List;
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
    private List<PrometheusLabel> labels;
    @JsonProperty("object_type")
    private String objectType = "add_prometheus_collector";

    public PrometheusCollector() {
    }

    public PrometheusCollector(String rvmAgentId, String host, String prometheusTopic, String port, String nodeUrlSuffix, String prometheusJob, String interval, List<PrometheusLabel> labels) {
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

    public List<PrometheusLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<PrometheusLabel> labels) {
        this.labels = labels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrometheusCollector that = (PrometheusCollector) o;

        if (rvmAgentId != null ? !rvmAgentId.equals(that.rvmAgentId) : that.rvmAgentId != null) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        if (prometheusTopic != null ? !prometheusTopic.equals(that.prometheusTopic) : that.prometheusTopic != null) return false;
        if (port != null ? !port.equals(that.port) : that.port != null) return false;
        if (nodeUrlSuffix != null ? !nodeUrlSuffix.equals(that.nodeUrlSuffix) : that.nodeUrlSuffix != null)
            return false;
        if (prometheusJob != null ? !prometheusJob.equals(that.prometheusJob) : that.prometheusJob != null)
            return false;
        if (interval != null ? !interval.equals(that.interval) : that.interval != null) return false;
        return labels != null ? labels.equals(that.labels) : that.labels == null;
    }

    @Override
    public int hashCode() {
        int result = rvmAgentId != null ? rvmAgentId.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (prometheusTopic != null ? prometheusTopic.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (nodeUrlSuffix != null ? nodeUrlSuffix.hashCode() : 0);
        result = 31 * result + (prometheusJob != null ? prometheusJob.hashCode() : 0);
        result = 31 * result + (interval != null ? interval.hashCode() : 0);
        result = 31 * result + (labels != null ? labels.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PrometheusCollector{" +
                "agent_id='" + rvmAgentId + '\'' +
                ", host='" + host + '\'' +
                ", topic='" + prometheusTopic + '\'' +
                ", port='" + port + '\'' +
                ", node_url_suffix='" + nodeUrlSuffix + '\'' +
                ", prometheus_job='" + prometheusJob + '\'' +
                ", interval='" + interval + '\'' +
                ", labels=" + labels +
                '}';
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
