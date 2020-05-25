package it.nextworks.nfvmano.configmanager.sb.kafkaRVMAgent.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KafkaDeletePrometheusCollector implements MessageToRVMAgent {
    @JsonProperty("object_type")
    private String objectType = "delete_prometheus_collector";
    @JsonProperty("agent_id")
    private String rvmAgentId;
    @JsonProperty("collector_id")
    private String collectorId;

    public KafkaDeletePrometheusCollector() {
    }

    public KafkaDeletePrometheusCollector(String objectType, String rvmAgentId, String collectorId) {
        this.objectType = objectType;
        this.rvmAgentId = rvmAgentId;
        this.collectorId = collectorId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
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

        KafkaDeletePrometheusCollector that = (KafkaDeletePrometheusCollector) o;

        if (objectType != null ? !objectType.equals(that.objectType) : that.objectType != null) return false;
        if (rvmAgentId != null ? !rvmAgentId.equals(that.rvmAgentId) : that.rvmAgentId != null) return false;
        return collectorId != null ? collectorId.equals(that.collectorId) : that.collectorId == null;
    }

    @Override
    public int hashCode() {
        int result = objectType != null ? objectType.hashCode() : 0;
        result = 31 * result + (rvmAgentId != null ? rvmAgentId.hashCode() : 0);
        result = 31 * result + (collectorId != null ? collectorId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DeletePrometheusCollector{" +
                "objectType='" + objectType + '\'' +
                ", rvmAgentId='" + rvmAgentId + '\'' +
                ", collectorId='" + collectorId + '\'' +
                '}';
    }
}
