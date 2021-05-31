package it.nextworks.nfvmano.configmanager.sb.grafana.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class DashboardTime {
    String timeFrom;
    String timeTo;

    public DashboardTime() {
    }

    public DashboardTime(String timeFrom, String timeTo) {
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
    }

    @JsonProperty("from")
    public String getTimeFrom() {
        return timeFrom;
    }

    @JsonProperty("from")
    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    @JsonProperty("to")
    public String getTimeTo() {
        return timeTo;
    }

    @JsonProperty("to")
    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    @Override
    public String toString() {
        return "DashboardTime{" +
                "timeFrom='" + timeFrom + '\'' +
                ", timeTo='" + timeTo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardTime that = (DashboardTime) o;
        return Objects.equals(timeFrom, that.timeFrom) && Objects.equals(timeTo, that.timeTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeFrom, timeTo);
    }
}
