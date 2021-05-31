
package it.nextworks.nfvmano.configmanager.elkstack.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class ELKAlert {
    @JsonProperty("alertId")
    private String alertId = null;

    @JsonProperty("alertName")
    private String alertName = null;

    @JsonProperty("labels")
    private List<String> labels = null;

    @JsonProperty("index")
    private String index = null;

    @JsonProperty("query")
    private String query = null;

    @JsonProperty("severity")
    private String severity = null;

    @JsonProperty("for")
    private String alertFor = null;

    @JsonProperty("target")
    private String target = null;

    @JsonProperty("kind")
    private String kind = null;

    public ELKAlert() {
    }

    public ELKAlert(ELKAlertDescription description) {
        this.alertName = description.getAlertName();
        this.labels = description.getLabels();
        this.index = description.getIndex();
        this.query = description.getQuery();
        this.severity = description.getSeverity();
        this.alertFor = description.getAlertFor();
        this.target = description.getTarget();
        this.kind = description.getKind();
    }

    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getAlertFor() {
        return alertFor;
    }

    public void setAlertFor(String alertFor) {
        this.alertFor = alertFor;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ELKAlert elkAlert = (ELKAlert) o;
        return Objects.equals(alertId, elkAlert.alertId) && Objects.equals(alertName, elkAlert.alertName) && Objects.equals(labels, elkAlert.labels) && Objects.equals(index, elkAlert.index) && Objects.equals(query, elkAlert.query) && Objects.equals(severity, elkAlert.severity) && Objects.equals(alertFor, elkAlert.alertFor) && Objects.equals(target, elkAlert.target) && Objects.equals(kind, elkAlert.kind);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alertId, alertName, labels, index, query, severity, alertFor, target, kind);
    }

    @Override
    public String toString() {
        return "ELKAlert{" +
                "alertId='" + alertId + '\'' +
                ", alertName='" + alertName + '\'' +
                ", labels=" + labels +
                ", index='" + index + '\'' +
                ", query='" + query + '\'' +
                ", severity='" + severity + '\'' +
                ", alertFor='" + alertFor + '\'' +
                ", target='" + target + '\'' +
                ", kind='" + kind + '\'' +
                '}';
    }

    public ELKAlert alertId(String alertId) {
        this.alertId = alertId;
        return this;
    }
}

