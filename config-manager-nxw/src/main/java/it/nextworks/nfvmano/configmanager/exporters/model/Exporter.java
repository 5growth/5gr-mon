/*
* Copyright 2018 Nextworks s.r.l.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package it.nextworks.nfvmano.configmanager.exporters.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Exporter {

    @JsonProperty("exporterId")
    private String exporterId = null;
    @JsonProperty("name")
    private String name = null;
    @JsonProperty("endpoint")
    private List<Endpoint> endpoint = null;
    @JsonProperty("collectionPeriod")
    private Integer collectionPeriod = null;
    @JsonProperty("nsId")
    private String nsId;
    @JsonProperty("vnfdId")
    private String vnfdId;
    @JsonProperty("honor_labels")
    private boolean honorLabels = false;
    @JsonProperty("honor_timestamps")
    private boolean honorTimestamps = false;
    @JsonProperty("metrics_path")
    private String metricsPath = "/metrics";
    @JsonProperty("instance")
    private String instance;
    private String exporter;
    @JsonProperty("params_string")
    private String paramsString;
    @JsonProperty("destination_vnf")
    private String destinationVnf;

    public Exporter() {

    }

    public Exporter exporterId(String exporterId) {
        this.exporterId = exporterId;
        return this;
    }

    /**
     * the ID of the exporter. It is also the name assigned to the corresponding scrape job
     *
     * @return exporterId
     **/
    public String getExporterId() {
        return exporterId;
    }

    public void setExporterId(String exporterId) {
        this.exporterId = exporterId;
    }

    public Exporter name(String name) {
        this.name = name;
        return this;
    }

    public String getNsId() {
        return nsId;
    }

    public void setNsId(String nsId) {
        this.nsId = nsId;
    }

    public String getVnfdId() {
        return vnfdId;
    }

    public void setVnfdId(String vnfdId) {
        this.vnfdId = vnfdId;
    }

    /**
     * Human-readable description of the exporter, e.g. \&quot;NSI-XXX_web-server_VM-XXX\&quot;
     *
     * @return name
     **/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Exporter endpoint(List<Endpoint> endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public Exporter addEndpointItem(Endpoint endpointItem) {
        if (this.endpoint == null) {
            this.endpoint = new ArrayList<>();
        }
        this.endpoint.add(endpointItem);
        return this;
    }

    /**
     * The list of endpoints of the instances of this job
     *
     * @return endpoint
     **/
    public List<Endpoint> getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(List<Endpoint> endpoint) {
        this.endpoint = endpoint;
    }

    public Exporter collectionPeriod(Integer collectionPeriod) {
        this.collectionPeriod = collectionPeriod;
        return this;
    }

    /**
     * the interval (in milliseconds) between scrapes
     *
     * @return collectionPeriod
     **/
    public Integer getCollectionPeriod() {
        return collectionPeriod;
    }

    public void setCollectionPeriod(Integer collectionPeriod) {
        this.collectionPeriod = collectionPeriod;
    }

    public boolean getHonorLabels() {
        return honorLabels;
    }

    public void setHonorLabels(boolean honorLabels) {
        this.honorLabels = honorLabels;
    }

    public boolean getHonorTimestamps() {
        return honorTimestamps;
    }

    public void setHonorTimestamps(boolean honorTimestamps) {
        this.honorTimestamps = honorTimestamps;
    }

    public String getMetricsPath() {
        return metricsPath;
    }

    public void setMetricsPath(String metricsPath) {
        this.metricsPath = metricsPath;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public void setExporter(String exporter) {
        this.exporter = exporter;
    }

    public String getExporter() {
        return exporter;
    }

    public void setDestinationVnf(String destinationVnf) {
        this.destinationVnf = destinationVnf;
    }

    public String getDestinationVnf() {
        return destinationVnf;
    }


    public void setParamsString(String paramsString) {
        this.paramsString = paramsString;
    }

    public String getParamsString() {
        return paramsString;
    }

    @Override
    public String toString() {
        return "Exporter{" +
                "exporterId='" + exporterId + '\'' +
                ", name='" + name + '\'' +
                ", endpoint=" + endpoint +
                ", collectionPeriod=" + collectionPeriod +
                ", nsId='" + nsId + '\'' +
                ", vnfdId='" + vnfdId + '\'' +
                ", honorLabels=" + honorLabels +
                ", honorTimestamps=" + honorTimestamps +
                ", metricsPath='" + metricsPath + '\'' +
                ", instance='" + instance + '\'' +
                ", exporter='" + exporter + '\'' +
                ", paramsString='" + paramsString + '\'' +
                ", destinationVnf='" + destinationVnf + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exporter exporter1 = (Exporter) o;
        return honorLabels == exporter1.honorLabels &&
                honorTimestamps == exporter1.honorTimestamps &&
                Objects.equals(exporterId, exporter1.exporterId) &&
                Objects.equals(name, exporter1.name) &&
                Objects.equals(endpoint, exporter1.endpoint) &&
                Objects.equals(collectionPeriod, exporter1.collectionPeriod) &&
                Objects.equals(nsId, exporter1.nsId) &&
                Objects.equals(vnfdId, exporter1.vnfdId) &&
                Objects.equals(metricsPath, exporter1.metricsPath) &&
                Objects.equals(instance, exporter1.instance) &&
                Objects.equals(exporter, exporter1.exporter) &&
                Objects.equals(paramsString, exporter1.paramsString) &&
                Objects.equals(destinationVnf, exporter1.destinationVnf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exporterId, name, endpoint, collectionPeriod, nsId, vnfdId, honorLabels, honorTimestamps, metricsPath, instance, exporter, paramsString, destinationVnf);
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

