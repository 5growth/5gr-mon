package it.nextworks.nfvmano.configmanager.rvmagent.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InfoExporter {
    private Map<String, List<InfoExporter>> exporters;

    public InfoExporter() {
    }

    public InfoExporter(Map<String, List<InfoExporter>> exporters) {
        this.exporters = exporters;
    }

    public Map<String, List<InfoExporter>> getExporters() {
        return exporters;
    }

    public void setExporters(Map<String, List<InfoExporter>> exporters) {
        this.exporters = exporters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoExporter that = (InfoExporter) o;
        return Objects.equals(exporters, that.exporters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exporters);
    }

    @Override
    public String toString() {
        return "ExporterInfo{" +
                "exporters=" + exporters +
                '}';
    }
}
