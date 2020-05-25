package com.telcaria.kibana.dashboards;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import com.telcaria.kibana.dashboards.model.KibanaDashboardVisualization;
import com.telcaria.kibana.dashboards.model.KibanaDashboardDescription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Generator {

    //private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private String templateDirectory;

    public Generator(String templateDirectory) {
        this.templateDirectory = templateDirectory;
    }

    public Generator() {
        this.templateDirectory = "dashboard-templates/";
    }

    public List<String> translate(KibanaDashboardDescription description){
        List<String> objects = new ArrayList<>();
        //Translate visualizations
        description.getVisualizations().forEach(visualization ->
                objects.add(createVisualizationObject(visualization))
        );
        //Translate dashboard
        objects.add(createDashboard(description));
        return objects;
    }

    public String createVisualizationObject(KibanaDashboardVisualization visualization) {
        Jinjava jinjava = new Jinjava();
        Map<String, Object> context = Maps.newHashMap();
        //Retrieve data from Json and create context to fill in the templates
        context.put("id", visualization.getId());
        context.put("title", visualization.getTitle());
        context.put("index", visualization.getIndex());
        if(visualization.getAggregationType() != null)
            context.put("aggregationType", visualization.getAggregationType());
        if(visualization.getMetricNameX() != null)
            context.put("metricNameX", visualization.getMetricNameX());
        if(visualization.getMetricNameY() != null)
            context.put("metricNameY", visualization.getMetricNameY());
        //Retrieve Jinjava template
        //TODO: enum with visualization type.
        //TODO: Handle templates in a separate class
        String template = null;
        switch(visualization.getVisualizationType()) {
            case "metric":
                template = templateDirectory + "count-visualization-template.json";
                break;
            case "line":
                template = templateDirectory + "line-visualization-template.json";
                break;
            case "pie":
                template = templateDirectory + "pie-visualization-template.json";
                break;
            default:
                //TODO: Handle unknown visualization type
                break;
        }
        try {
            template = Resources.toString(Resources.getResource(template), Charsets.UTF_8);
        }
        catch (IOException e) {
            //log.warn(e.toString());
        }
        return jinjava.render(template, context);
    }

    public String createDashboard(KibanaDashboardDescription dashboard) {
        Jinjava jinjava = new Jinjava();
        Map<String, Object> context = Maps.newHashMap();
        context.put("id", dashboard.getDashboardId());
        context.put("title", dashboard.getDashboardTitle());
        context.put("index", dashboard.getIndex());
        //Add visualizations
        //transform to string array
        List<String> dashboardIdList = dashboard.getVisualizations().stream().map(v -> v.getId()).collect(Collectors.toList());
        context.put("dashboard_id_list", dashboardIdList);
        context.put("panelsJson", getPanelsJson(dashboardIdList));
        //TODO: Handle template paths in a separate class
        String template = templateDirectory + "dashboard-template.json";
        try {
            template = Resources.toString(Resources.getResource(template), Charsets.UTF_8);
        }
        catch (IOException e) {
            //log.warn(e.toString());
        }
        return jinjava.render(template, context);
    }

    private String getPanelsJson(List<String> dashboardsList){
        int x = 0,y = -15;
        String panelsJson = "[";
        for (int i = 0; i < dashboardsList.size(); i++) {
            if( i % 2 == 0) {
                x = 0;
                y += 15;
            }
            else {
                x = 24;
            }
            panelsJson = panelsJson.concat("{\\\"gridData\\\":{\\\"w\\\":24,\\\"h\\\":15,\\\"x\\\":" + x + ",\\\"y\\\":" + y + ",\\\"i\\\":\\\"" + (i+1) + "\\\"},\\\"version\\\":\\\"7.3.1\\\",\\\"panelIndex\\\"" +
                    ":\\\"" + (i+1) + "\\\",\\\"embeddableConfig\\\":{},\\\"panelRefName\\\":\\\"panel_" + (i + 1) + "\\\"}");
            if (i < dashboardsList.size() - 1) {
                panelsJson = panelsJson.concat(",");
            }
        }
        return panelsJson.concat("]");
    }
}
