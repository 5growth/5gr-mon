package it.nextworks.nfvmano.configmanager.sb.prometheusPushGateway.model;

import groovy.transform.Synchronized;

import java.util.Objects;

public class MetricsObject {
    private String metrics;
    private Integer getCount = 4;

    public MetricsObject() {
    }

    public MetricsObject(String metrics) {
        this.metrics = metrics;
    }

    @Synchronized
    public String getMetrics() {
        getCount --;
        return metrics;
    }

    @Synchronized
    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    @Synchronized
    public Integer getGetCount() {
        return getCount;
    }

    @Synchronized
    public void setGetCount(Integer getCount) {
        this.getCount = getCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricsObject that = (MetricsObject) o;
        return Objects.equals(metrics, that.metrics) &&
                Objects.equals(getCount, that.getCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metrics, getCount);
    }

    @Override
    public String toString() {
        return "MetricsObject{" +
                "metrics='" + metrics + '\'' +
                ", getCount=" + getCount +
                '}';
    }
}
