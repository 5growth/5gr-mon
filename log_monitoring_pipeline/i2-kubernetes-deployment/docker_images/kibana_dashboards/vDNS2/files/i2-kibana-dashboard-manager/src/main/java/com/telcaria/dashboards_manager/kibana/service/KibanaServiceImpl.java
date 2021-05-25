/*
 * Copyright 2021-present Open Networking Foundation
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

package com.telcaria.dashboards_manager.kibana.service;

import com.telcaria.dashboards_manager.storage.enums.GraphType;
import com.telcaria.dashboards_manager.kibana.client.KibanaConnectorService;
import com.telcaria.dashboards_manager.kibana.client.KibanaProperties;
import com.telcaria.dashboards_manager.storage.entities.Kpi;
import com.telcaria.dashboards_manager.storage.entities.Log;
import com.telcaria.dashboards_manager.storage.entities.Metric;
import com.telcaria.kibana.dashboards.Generator;
import com.telcaria.kibana.dashboards.model.KibanaDashboardDescription;
import com.telcaria.kibana.dashboards.model.KibanaDashboardVisualization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class KibanaServiceImpl implements KibanaService{

    @Autowired
    private KibanaProperties kibanaProperties;
    @Autowired
    private KibanaConnectorService kibanaConnectorService;
    private Generator generator = new Generator();

    @Override
    public String createKibanaDashboard(Log logEntity) {

        // We need one dashboard
        KibanaDashboardDescription kibanaDashboardDescription = new KibanaDashboardDescription();
        kibanaDashboardDescription.setDashboardId(logEntity.getDashboardId());
        kibanaDashboardDescription.setIndex(logEntity.getTopic().toLowerCase());
        kibanaDashboardDescription.setDashboardTitle(logEntity.getDashboardTitle());

        // We need three visualizations
        // First of all, we create the PIE graph
        KibanaDashboardVisualization kibanaVisualization_1 = new KibanaDashboardVisualization();
        kibanaVisualization_1.setTitle("Pie chart - " + logEntity.getTopic().toLowerCase());
        kibanaVisualization_1.setId(logEntity.getTopic().toLowerCase() + "_1");
        kibanaVisualization_1.setVisualizationType(GraphType.PIE.toString());
        kibanaVisualization_1.setIndex(logEntity.getTopic().toLowerCase());
        kibanaVisualization_1.setYlabel_1("VNF Name");
        kibanaVisualization_1.setYlabel_2("Log File");
        kibanaVisualization_1.setMetricNameX_1("host.name.keyword");
        kibanaVisualization_1.setMetricNameX_2("log.file.path.keyword");

        // Secondly, we create the BAR graph
        KibanaDashboardVisualization kibanaVisualization_2 = new KibanaDashboardVisualization();
        kibanaVisualization_2.setTitle("Histogram chart - " + logEntity.getTopic().toLowerCase());
        kibanaVisualization_2.setId(logEntity.getTopic().toLowerCase() + "_2");
        kibanaVisualization_2.setVisualizationType(GraphType.BAR.toString());
        kibanaVisualization_2.setIndex(logEntity.getTopic().toLowerCase());
        kibanaVisualization_2.setYlabel_1("VNF Name");
        kibanaVisualization_2.setYlabel_2("Log File");
        kibanaVisualization_2.setMetricNameX_1("host.name.keyword");
        kibanaVisualization_2.setMetricNameX_2("log.file.path.keyword");

        // Finally, we create the SEARCH graph
        KibanaDashboardVisualization kibanaVisualization_search = new KibanaDashboardVisualization();
        kibanaVisualization_search.setTitle("Search visualization - " + logEntity.getTopic().toLowerCase());
        kibanaVisualization_search.setId(logEntity.getTopic().toLowerCase() + "_search");
        kibanaVisualization_search.setVisualizationType(GraphType.SEARCH.toString());
        kibanaVisualization_search.setIndex(logEntity.getTopic().toLowerCase());

        // Embed the visualizations to the dashboard.
        kibanaDashboardDescription.addVisualizationsItem(kibanaVisualization_1);
        kibanaDashboardDescription.addVisualizationsItem(kibanaVisualization_2);
        kibanaDashboardDescription.addVisualizationsItem(kibanaVisualization_search);

        // Translate them
        List<String> kibanaObjects = generator.translate(kibanaDashboardDescription);

        // And create the corresponding Kibana objects
        for (String jsonKibanaObject : kibanaObjects) {
            if(!kibanaConnectorService.postKibanaObject(jsonKibanaObject)){
                return null;
            }
        }

        // Set owner to the dashboard (if enabled, no by default)
        if (kibanaProperties.isDashboardOwnerEnabled()){
            if (!kibanaConnectorService.setOwner(logEntity.getDashboardId(), "elastic")) {
                return null;
            }
        }

        // Finally, create the index pattern
        if (!kibanaConnectorService.putKibanaIndexPattern(logEntity.getTopic())) {
            return null;
        }

        // Finally, generate the URL
        return getUrl(kibanaDashboardDescription.getDashboardId());
    }

    @Override
    public String createKibanaDashboard(Kpi kpi) {

        KibanaDashboardDescription kibanaDashboardDescription = new KibanaDashboardDescription();
        kibanaDashboardDescription.setDashboardId(kpi.getDashboardId());
        kibanaDashboardDescription.setIndex(kpi.getTopic().toLowerCase());
        kibanaDashboardDescription.setDashboardTitle(kpi.getKpiId());

        KibanaDashboardVisualization kibanaVisualization = new KibanaDashboardVisualization();
        kibanaVisualization.setTitle(kpi.getKpiId());
        kibanaVisualization.setId(kpi.getTopic().toLowerCase());
        kibanaVisualization.setVisualizationType(kpi.getGraph().toString());
        kibanaVisualization.setIndex(kpi.getTopic().toLowerCase());
        kibanaVisualization.setYlabel(kpi.getName() + " (" + kpi.getUnit() + ")");


        if(kpi.getGraph().equals(GraphType.LINE)) {
            //line
            kibanaVisualization.setMetricNameX("device_timestamp");
            kibanaVisualization.setMetricNameY("kpi_value");
            kibanaVisualization.setXlabel("device_timestamp");
            kibanaVisualization.setAggregationType("avg");

        } else if(kpi.getGraph().equals(GraphType.PIE)){
            //pie
            //TODO: plot several graphs differentiating by "device_id" attribute?
            kibanaVisualization.setMetricNameX("kpi_value");
            kibanaVisualization.setAggregationType("count");

        }else if(kpi.getGraph().equals(GraphType.COUNTER)){ // visualizationType==metric
            //type count, do nothing
        }


        kibanaDashboardDescription.addVisualizationsItem(kibanaVisualization);
        List<String> kibanaObjects = generator.translate(kibanaDashboardDescription);
        //String jsonKibanaObject = kibanaObjects.get(0);

        for (String jsonKibanaObject : kibanaObjects) {
            if(!kibanaConnectorService.postKibanaObject(jsonKibanaObject)){
                return null;
            }
        }

        if (kibanaProperties.isDashboardOwnerEnabled()){
            if(!kibanaConnectorService.setOwner(kpi.getDashboardId(), kpi.getKibanaUser())){
                return null;
            }
        }
        if (!kibanaConnectorService.putKibanaIndexPattern(kpi.getTopic())) {
            return null;
        }
        return getUrl(kibanaDashboardDescription.getDashboardId());
    }

    @Override
    public String createKibanaDashboard(Metric metric) {

        KibanaDashboardDescription kibanaDashboardDescription = new KibanaDashboardDescription();
        kibanaDashboardDescription.setDashboardId(metric.getDashboardId());
        kibanaDashboardDescription.setIndex(metric.getTopic().toLowerCase());
        kibanaDashboardDescription.setDashboardTitle(metric.getMetricId());

        KibanaDashboardVisualization kibanaVisualization = new KibanaDashboardVisualization();
        kibanaVisualization.setTitle(metric.getMetricId());
        kibanaVisualization.setId(metric.getTopic().toLowerCase());
        kibanaVisualization.setVisualizationType(metric.getGraph().toString());
        kibanaVisualization.setIndex(metric.getTopic().toLowerCase());
        kibanaVisualization.setYlabel(metric.getName() + " (" + metric.getUnit()+ ")");


        if(metric.getGraph().equals(GraphType.LINE)) {
            //line
            kibanaVisualization.setMetricNameX("device_timestamp");
            kibanaVisualization.setMetricNameY("metric_value");
            kibanaVisualization.setXlabel("device_timestamp");
            kibanaVisualization.setAggregationType("avg");
        } else if(metric.getGraph().equals(GraphType.PIE)){
            //pie
            //TODO: plot several graphs differentiating by "device_id" attribute?
            kibanaVisualization.setMetricNameX("metric_value");
            kibanaVisualization.setAggregationType("count");

        }else if(metric.getGraph().equals(GraphType.COUNTER)){ // visualizationType==metric
            //type count, do nothing
        }

        kibanaDashboardDescription.addVisualizationsItem(kibanaVisualization);
        List<String> kibanaObjects = generator.translate(kibanaDashboardDescription);
        //String jsonKibanaObject = kibanaObjects.get(0);

        for (String jsonKibanaObject : kibanaObjects) {
            if(!kibanaConnectorService.postKibanaObject(jsonKibanaObject)){
                return null;
            }
        }
        if (kibanaProperties.isDashboardOwnerEnabled()) {
            if (!kibanaConnectorService.setOwner(metric.getDashboardId(), metric.getKibanaUser())) {
                return null;
            }
        }
        if (!kibanaConnectorService.putKibanaIndexPattern(metric.getTopic().toLowerCase())) {
            return null;
        }
        return getUrl(kibanaDashboardDescription.getDashboardId());
    }

    @Override
    public boolean deleteKibanaDashboard(Log logEntity) {
        return kibanaConnectorService.removeKibanaObject("visualization_" + logEntity.getTopic().toLowerCase() + "_1", "visualization") &&
                kibanaConnectorService.removeKibanaObject("visualization_" + logEntity.getTopic().toLowerCase() + "_2", "visualization") &&
                kibanaConnectorService.removeKibanaObject("search_" + logEntity.getTopic().toLowerCase() + "_search", "search") &&
                kibanaConnectorService.removeKibanaObject(logEntity.getDashboardId(), "dashboard") &&
                kibanaConnectorService.removeKibanaObject("index_" + logEntity.getTopic().toLowerCase(), "index-pattern");
    }

    @Override
    public boolean deleteKibanaDashboard(Kpi kpi) {
        return kibanaConnectorService.removeKibanaObject("visualization_" + kpi.getTopic().toLowerCase(), "visualization") &&
                kibanaConnectorService.removeKibanaObject(kpi.getDashboardId(), "dashboard") &&
                kibanaConnectorService.removeKibanaObject("index_" + kpi.getTopic().toLowerCase(), "index-pattern");
    }

    @Override
    public boolean deleteKibanaDashboard(Metric metric) {
        return kibanaConnectorService.removeKibanaObject("visualization_" + metric.getTopic().toLowerCase(), "visualization") &&
                kibanaConnectorService.removeKibanaObject(metric.getDashboardId(), "dashboard") &&
                kibanaConnectorService.removeKibanaObject("index_" + metric.getTopic().toLowerCase(), "index-pattern");
    }
    

    private String getUrl(String id) {
        return kibanaProperties.getDashboardUrl() + "/app/kibana#/dashboard/" + id + "?embed=true&_g=(refreshInterval:(pause:!f,value:10000),time:(from:now-1y,to:now))";
    }

}
