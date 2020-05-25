package it.nextworks.nfvmano.configmanager.sb.kafkaRVMAgent.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class KafkaDeletePrometheusCollectorResponse extends KafkaGeneralResponse {
    @JsonProperty("agent_id")
    private String rvmAgentId;
    @JsonProperty("collector_id")
    private String collectorId;
    @JsonProperty("status")
    private String status;

    public KafkaDeletePrometheusCollectorResponse() {
    }

    public KafkaDeletePrometheusCollectorResponse(String rvmAgentId, String collectorId, String status) {
        this.rvmAgentId = rvmAgentId;
        this.collectorId = collectorId;
        this.status = status;
    }

    public String getRvmAgentId() {
        return rvmAgentId;
    }

    public void setRvmAgentId(String rvmAgentId) {
        this.rvmAgentId = rvmAgentId;
    }

    public String getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KafkaDeletePrometheusCollectorResponse that = (KafkaDeletePrometheusCollectorResponse) o;
        return Objects.equals(rvmAgentId, that.rvmAgentId) &&
                Objects.equals(collectorId, that.collectorId) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rvmAgentId, collectorId, status);
    }

    @Override
    public String toString() {
        return "KafkaDeletePrometheusCollectorResponse{" +
                "rvmAgentId='" + rvmAgentId + '\'' +
                ", collectorId='" + collectorId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
