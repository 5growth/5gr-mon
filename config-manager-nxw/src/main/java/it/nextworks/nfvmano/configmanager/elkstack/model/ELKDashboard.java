
package it.nextworks.nfvmano.configmanager.elkstack.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ELKDashboard {
    @JsonProperty("dashboardTitle")
    private String dashboardTitle = null;

    @JsonProperty("ns_id")
    private String nsId = null;

    @JsonProperty("dashboard_type")
    private String dashboardType = null;

    @JsonProperty("dashboardId")
    private String dashboardId = null;

    @JsonProperty("url")
    private String url = null;


    public ELKDashboard() {

    }

    public ELKDashboard(ELKDashboardDescription description){
        this.dashboardTitle = description.getDashboardTitle();
        this.dashboardType = description.getDashboardType();
        this.nsId = description.getNsId();
    }

    public ELKDashboard(String dashboardTitle, String nsId, String dashboardType, String dashboardId, String url) {
        this.dashboardTitle = dashboardTitle;
        this.nsId = nsId;
        this.dashboardType = dashboardType;
        this.dashboardId = dashboardId;
        this.url = url;
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

    public String getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(String dashboardId) {
        this.dashboardId = dashboardId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ELKDashboard that = (ELKDashboard) o;
        return Objects.equals(dashboardTitle, that.dashboardTitle) && Objects.equals(nsId, that.nsId) && Objects.equals(dashboardType, that.dashboardType) && Objects.equals(dashboardId, that.dashboardId) && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dashboardTitle, nsId, dashboardType, dashboardId, url);
    }

    @Override
    public String toString() {
        return "ELKDashboard{" +
                "dashboardTitle='" + dashboardTitle + '\'' +
                ", nsId='" + nsId + '\'' +
                ", dashboardType='" + dashboardType + '\'' +
                ", dashboardId='" + dashboardId + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public ELKDashboard dashboardId(String dashboardId) {
        this.dashboardId = dashboardId;
        return this;
    }

    public ELKDashboard url(String dashboardId) {
        this.dashboardId = dashboardId;
        return this;
    }
}

