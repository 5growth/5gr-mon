
package it.nextworks.nfvmano.configmanager.elkstack.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class ELKAlertDescription {
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

    @JsonProperty("dashboardTitle")
    private String dashboardTitle = null;

    @JsonProperty("target")
    private String target = null;

    @JsonProperty("kind")
    private String kind = null;

    public ELKAlertDescription() {
    }

    public ELKAlertDescription(String alertName, List<String> labels, String index, String query, String severity, String alertFor, String dashboardTitle, String target, String kind) {
        this.alertName = alertName;
        this.labels = labels;
        this.index = index;
        this.query = query;
        this.severity = severity;
        this.alertFor = alertFor;
        this.dashboardTitle = dashboardTitle;
        this.target = target;
        this.kind = kind;
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

    public String getDashboardTitle() {
        return dashboardTitle;
    }

    public void setDashboardTitle(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ELKAlertDescription that = (ELKAlertDescription) o;
        return Objects.equals(alertName, that.alertName) && Objects.equals(labels, that.labels) && Objects.equals(index, that.index) && Objects.equals(query, that.query) && Objects.equals(severity, that.severity) && Objects.equals(alertFor, that.alertFor) && Objects.equals(dashboardTitle, that.dashboardTitle) && Objects.equals(target, that.target) && Objects.equals(kind, that.kind);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alertName, labels, index, query, severity, alertFor, dashboardTitle, target, kind);
    }

    @Override
    public String toString() {
        return "ELKAlertDescription{" +
                "alertName='" + alertName + '\'' +
                ", labels=" + labels +
                ", index='" + index + '\'' +
                ", query='" + query + '\'' +
                ", severity='" + severity + '\'' +
                ", alertFor='" + alertFor + '\'' +
                ", dashboardTitle='" + dashboardTitle + '\'' +
                ", target='" + target + '\'' +
                ", kind='" + kind + '\'' +
                '}';
    }
}

