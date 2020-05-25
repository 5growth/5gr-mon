package it.nextworks.nfvmano.configmanager.rvmagent.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorMessage{
    @JsonProperty("object_type")
    private String objectType;
    @JsonProperty("prometheus_collector_id")
    private String prometheusCollectorId;
    @JsonProperty("url")
    private String url;
    @JsonProperty("error")
    private String error;
    @JsonProperty("prometheus_url_suffix")
    private String prometheusUrlSuffix;

    public ErrorMessage() {
    }

    public ErrorMessage(String objectType, String prometheusCollectorId, String url, String error, String prometheus_url_suffix) {
        this.objectType = objectType;
        this.prometheusCollectorId = prometheusCollectorId;
        this.url = url;
        this.error = error;
        this.prometheusUrlSuffix = prometheus_url_suffix;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getPrometheusCollectorId() {
        return prometheusCollectorId;
    }

    public void setPrometheusCollectorId(String prometheusCollectorId) {
        this.prometheusCollectorId = prometheusCollectorId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPrometheusUrlSuffix() {
        return prometheusUrlSuffix;
    }

    public void setPrometheusUrlSuffix(String prometheusUrlSuffix) {
        this.prometheusUrlSuffix = prometheusUrlSuffix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ErrorMessage that = (ErrorMessage) o;

        if (objectType != null ? !objectType.equals(that.objectType) : that.objectType != null) return false;
        if (prometheusCollectorId != null ? !prometheusCollectorId.equals(that.prometheusCollectorId) : that.prometheusCollectorId != null)
            return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (error != null ? !error.equals(that.error) : that.error != null) return false;
        return prometheusUrlSuffix != null ? prometheusUrlSuffix.equals(that.prometheusUrlSuffix) : that.prometheusUrlSuffix == null;
    }

    @Override
    public int hashCode() {
        int result = objectType != null ? objectType.hashCode() : 0;
        result = 31 * result + (prometheusCollectorId != null ? prometheusCollectorId.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        result = 31 * result + (prometheusUrlSuffix != null ? prometheusUrlSuffix.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "object_type='" + objectType + '\'' +
                ", prometheus_collector_id='" + prometheusCollectorId + '\'' +
                ", url='" + url + '\'' +
                ", error='" + error + '\'' +
                ", prometheus_url_suffix='" + prometheusUrlSuffix + '\'' +
                '}';
    }
}
