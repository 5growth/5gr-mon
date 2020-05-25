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

package it.nextworks.nfvmano.configmanager.dashboards.kibana.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public class DashboardVisualization {
    @JsonProperty("id")
    private String id = null;
    @JsonProperty("title")
    private String title = null;
    @JsonProperty("visualizationType")
    private String visualizationType = null;
    @JsonProperty("aggregationType")
    private String aggregationType = null;
    @JsonProperty("metricNameX")
    private String metricNameX = null;
    @JsonProperty("metricNameY")
    private String metricNameY = null;

    public DashboardVisualization title(String title) {
        this.title = title;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getVisualizationType() {
        return visualizationType;
    }

    public String getAggregationType() {
        return aggregationType;
    }
    /**
     * Get title
     *
     * @return title
     **/
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DashboardVisualization aggregationType(String aggregationType) {
        this.aggregationType = aggregationType;
        return this;
    }

    /**
     * the aggregationType whose value the graph in the panel should show.
     * <p>
     * See https://prometheus.io/docs/prometheus/latest/aggregationTypeing/basics/ for details
     *
     * @return aggregationType
     **/
    public String getQuery() {
        return aggregationType;
    }

    public void setQuery(String aggregationType) {
        this.aggregationType = aggregationType;
    }

    public DashboardVisualization metricNameX(String metricNameX) {
        this.metricNameX = metricNameX;
        return this;
    }

    /**
     * the metric in the X axis
     *
     * @return metricNameX
     **/
    public String getMetricNameX() {
        return metricNameX;
    }

    public void setMetricNameX(String metricNameX) {
        this.metricNameX = metricNameX;
    }

    public String getMetricNameY() {
        return metricNameY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DashboardVisualization dashboardPanel = (DashboardVisualization) o;
        return Objects.equals(this.id, dashboardPanel.id) &&
                Objects.equals(this.title, dashboardPanel.title) &&
                Objects.equals(this.visualizationType, dashboardPanel.visualizationType) &&
                Objects.equals(this.aggregationType, dashboardPanel.aggregationType) &&
                Objects.equals(this.metricNameX, dashboardPanel.metricNameX) &&
                Objects.equals(this.metricNameY, dashboardPanel.metricNameY);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, aggregationType, metricNameX, metricNameY);
    }

    @Override
    public String toString() {

        return "class DashboardPanel {\n" +
                "    id: " + toIndentedString(id) + "\n" +
                "    title: " + toIndentedString(title) + "\n" +
                "    type: " + toIndentedString(visualizationType) + "\n" +
                "    aggregationType: " + toIndentedString(aggregationType) + "\n" +
                "    metricNameX: " + toIndentedString(metricNameX) + "\n" +
                "    metricNameY: " + toIndentedString(metricNameY) + "\n" +
                "}";
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

