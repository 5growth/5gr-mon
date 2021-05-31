
package it.nextworks.nfvmano.configmanager.elkstack.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ELKDashboardDescription {
    @JsonProperty("dashboardTitle")
    private String dashboardTitle = null;

    @JsonProperty("ns_id")
    private String nsId = null;

    @JsonProperty("dashboard_type")
    private String dashboardType = null;

    public ELKDashboardDescription() {

    }

    public ELKDashboardDescription(String dashboardTitle, String nsId, String dashboardType) {
        this.dashboardTitle = dashboardTitle;
        this.nsId = nsId;
        this.dashboardType = dashboardType;
    }

    public String getDashboardTitle() {
        return dashboardTitle;
    }

    public void setDashboardTitle(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
    }

    public String getNsId() {
        return nsId;
    }

    public void setNsId(String nsId) {
        this.nsId = nsId;
    }

    public String getDashboardType() {
        return dashboardType;
    }

    public void setDashboardType(String dashboardType) {
        this.dashboardType = dashboardType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ELKDashboardDescription that = (ELKDashboardDescription) o;
        return Objects.equals(dashboardTitle, that.dashboardTitle) && Objects.equals(nsId, that.nsId) && Objects.equals(dashboardType, that.dashboardType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dashboardTitle, nsId, dashboardType);
    }

    @Override
    public String toString() {
        return "ELKDashboardDescription{" +
                "dashboardTitle='" + dashboardTitle + '\'' +
                ", nsId='" + nsId + '\'' +
                ", dashboardType='" + dashboardType + '\'' +
                '}';
    }
}

