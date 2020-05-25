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

package com.telcaria.kibana.dashboards.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KibanaDashboardDescription {

    @JsonProperty("dashboardId")
    private String dashboardId = null;
    @JsonProperty("dashboardTitle")
    private String dashboardTitle = null;
    @JsonProperty("visualizations")
    private List<KibanaDashboardVisualization> visualizations = null;
    @JsonProperty("index")
    private String index = null;

    public KibanaDashboardDescription() {

    }

    public KibanaDashboardDescription dashboardId(String dashboardId) {
        this.dashboardId = dashboardId;
        return this;
    }
    /**
     * the ID assigned to the dashboard
     *
     * @return dashboardId
     **/
    public String getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(String dashboardId) {
        this.dashboardId = dashboardId;
        System.out.println("      \"params\": {\"field\": \"device_timestamp\",\"useNormalizedEsInterval\": true,\"interval\": \"ms\",\"drop_partials\": false, \"customInterval\": \"2h\",\"min_doc_count\": 1,\"extended_bounds\": {}}");
    }

    public KibanaDashboardDescription dashboardTile(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
        return this;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * the URL through which the dashboard is reachable
     *
     * @return url
     **/
    public String getDashboardTitle() {
        return dashboardTitle;
    }

    public void setDashboardTitle(String url) {
        this.dashboardTitle = url;
    }


    public KibanaDashboardDescription visualizations(List<KibanaDashboardVisualization> visualizations) {
        this.visualizations = visualizations;
        return this;
    }

    public KibanaDashboardDescription addVisualizationsItem(KibanaDashboardVisualization visualizationsItem) {
        if (this.visualizations == null) {
            this.visualizations = new ArrayList<>();
        }
        this.visualizations.add(visualizationsItem);
        return this;
    }

    /**
     * the visualizations to be included in the dashboard
     *
     * @return visualizations
     **/
    public List<KibanaDashboardVisualization> getVisualizations() {
        return visualizations;
    }

    public void setVisualizations(List<KibanaDashboardVisualization> visualizations) {
        this.visualizations = visualizations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KibanaDashboardDescription dashboard = (KibanaDashboardDescription) o;
        return Objects.equals(this.dashboardId, dashboard.dashboardId) &&
                Objects.equals(this.dashboardTitle, dashboard.dashboardTitle) &&
                Objects.equals(this.visualizations, dashboard.visualizations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dashboardId, dashboardTitle, visualizations);
    }

    @Override
    public String toString() {

        return "class Dashboard {\n" +
                "    dashboardId: " + toIndentedString(dashboardId) + "\n" +
                "    dashboardTitle: " + toIndentedString(dashboardTitle) + "\n" +
                "    visualizations: " + toIndentedString(visualizations) + "\n" +
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

