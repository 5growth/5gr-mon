package com.telcaria.kibana.dashboards;

import com.telcaria.kibana.dashboards.model.KibanaDashboardDescription;
import com.telcaria.kibana.dashboards.model.KibanaDashboardVisualization;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeneratorTest {

    @Test
    void translate() throws IOException {
        Generator generator = new Generator();
        KibanaDashboardDescription description = new KibanaDashboardDescription();
        description.setDashboardId("test-dashboard");
        description.setDashboardTitle("Test Dashboard");

        KibanaDashboardVisualization kibanaDashboardVisualization1 = new KibanaDashboardVisualization();
        kibanaDashboardVisualization1.setTitle("Count Visualization");
        kibanaDashboardVisualization1.setId("count-visualization");
        kibanaDashboardVisualization1.setVisualizationType("metric");
        kibanaDashboardVisualization1.setVisualizationType("metric");

        KibanaDashboardVisualization kibanaDashboardVisualization2 = new KibanaDashboardVisualization();
        kibanaDashboardVisualization2.setTitle("Line Visualization");
        kibanaDashboardVisualization2.setId("line-visualization");
        kibanaDashboardVisualization2.setVisualizationType("line");
        kibanaDashboardVisualization2.setAggregationType("agv");
        kibanaDashboardVisualization2.setMetricNameX("timestamp_file");
        kibanaDashboardVisualization2.setMetricNameY("metrics_data");

        List<KibanaDashboardVisualization> kibanaDashboardVisualizations = new ArrayList<>();
        kibanaDashboardVisualizations.add(kibanaDashboardVisualization1);
        kibanaDashboardVisualizations.add(kibanaDashboardVisualization2);

        description.setVisualizations(kibanaDashboardVisualizations);
        List<String> descriptions = generator.translate(description);

        assertEquals(3, descriptions.size());
    }

    @Test
    void createVisualizationObject(){
        String expectedJson1 = "{\n" +
                "  \"objects\": [\n" +
                "    {\n" +
                "      \"id\": \"count-visualization\",\n" +
                "      \"type\": \"visualization\",\n" +
                "      \"updated_at\": \"2018-09-07T18:40:33.247Z\",\n" +
                "      \"version\": 1,\n" +
                "      \"attributes\": {\n" +
                "        \"title\": \"Count Visualization\",\n" +
                "        \"uiStateJSON\": \"{}\",\n" +
                "        \"description\": \"\",\n" +
                "        \"version\": 1,\n" +
                "        \"kibanaSavedObjectMeta\": {\n" +
                "          \"searchSourceJSON\": \"{\\\"index\\\":\\\"logstash-*\\\",\\\"query\\\":{\\\"query\\\":\\\"\\\",\\\"language\\\":\\\"lucene\\\"},\\\"filter\\\":[]}\"\n" +
                "        },\n" +
                "        \"visState\": \"{\\\"title\\\":\\\"Count Visualization\\\",\\\"type\\\":\\\"metric\\\",\\\"params\\\":{\\\"addTooltip\\\":true,\\\"addLegend\\\":false,\\\"type\\\":\\\"metric\\\",\\\"metric\\\":{\\\"percentageMode\\\":false,\\\"useRanges\\\":false,\\\"colorSchema\\\":\\\"Green to Red\\\",\\\"metricColorMode\\\":\\\"None\\\",\\\"colorsRange\\\":[{\\\"from\\\":0,\\\"to\\\":10000}],\\\"labels\\\":{\\\"show\\\":true},\\\"invertColors\\\":false,\\\"style\\\":{\\\"bgFill\\\":\\\"#000\\\",\\\"bgColor\\\":false,\\\"labelColor\\\":false,\\\"subText\\\":\\\"\\\",\\\"fontSize\\\":60}}},\\\"aggs\\\":[{\\\"id\\\":\\\"1\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"count\\\",\\\"schema\\\":\\\"metric\\\",\\\"params\\\":{}}]}\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";
        String expectedJson2 = "{\n" +
                "  \"objects\": [\n" +
                "    {\n" +
                "      \"id\": \"line-visualization\",\n" +
                "      \"type\": \"visualization\",\n" +
                "      \"updated_at\": \"2018-09-07T18:40:33.247Z\",\n" +
                "      \"version\": 1,\n" +
                "      \"attributes\": {\n" +
                "        \"title\": \"Line Visualization\",\n" +
                "        \"uiStateJSON\": \"{}\",\n" +
                "        \"description\": \"\",\n" +
                "        \"version\": 1,\n" +
                "        \"kibanaSavedObjectMeta\": {\n" +
                "          \"searchSourceJSON\": \"{\\\"index\\\":\\\"logstash-*\\\",\\\"query\\\":{\\\"query\\\":\\\"\\\",\\\"language\\\":\\\"lucene\\\"},\\\"filter\\\":[]}\"\n" +
                "        },\n" +
                "        \"visState\": \"{\\\"title\\\":\\\"Line Visualization\\\",\\\"type\\\":\\\"line\\\", \\\"params\\\":{\\\"type\\\":\\\"line\\\",\\\"grid\\\":{\\\"categoryLines\\\":false},\\\"categoryAxes\\\":[{\\\"id\\\":\\\"CategoryAxis-1\\\",\\\"type\\\":\\\"category\\\",\\\"position\\\":\\\"bottom\\\",\\\"show\\\":true,\\\"style\\\":{},\\\"scale\\\":{\\\"type\\\":\\\"linear\\\"},\\\"labels\\\":{\\\"show\\\":true,\\\"filter\\\":true,\\\"truncate\\\":100},\\\"title\\\":{}}],\\\"valueAxes\\\":[{\\\"id\\\":\\\"ValueAxis-1\\\",\\\"name\\\":\\\"LeftAxis-1\\\",\\\"type\\\":\\\"value\\\",\\\"position\\\":\\\"left\\\",\\\"show\\\":true,\\\"style\\\":{},\\\"scale\\\":{\\\"type\\\":\\\"linear\\\",\\\"mode\\\":\\\"normal\\\"},\\\"labels\\\":{\\\"show\\\":true,\\\"rotate\\\":0,\\\"filter\\\":false,\\\"truncate\\\":100},\\\"title\\\":{\\\"text\\\":\\\"Average metrics_data\\\"}}],\\\"seriesParams\\\":[{\\\"show\\\":\\\"true\\\",\\\"type\\\":\\\"line\\\",\\\"mode\\\":\\\"normal\\\",\\\"data\\\":{\\\"label\\\":\\\"Average metrics_data\\\",\\\"id\\\":\\\"1\\\"},\\\"valueAxis\\\":\\\"ValueAxis-1\\\",\\\"drawLinesBetweenPoints\\\":true,\\\"showCircles\\\":true}],\\\"addTooltip\\\":true,\\\"addLegend\\\":true,\\\"legendPosition\\\":\\\"right\\\",\\\"times\\\":[],\\\"addTimeMarker\\\":false,\\\"dimensions\\\":{\\\"x\\\":{\\\"accessor\\\":0,\\\"format\\\":{\\\"id\\\":\\\"number\\\"},\\\"params\\\":{\\\"interval\\\":4000},\\\"aggType\\\":\\\"histogram\\\"},\\\"y\\\":[{\\\"accessor\\\":1,\\\"format\\\":{\\\"id\\\":\\\"number\\\"},\\\"params\\\":{},\\\"aggType\\\":\\\"agv\\\"}]}},\\\"aggs\\\":[{\\\"id\\\":\\\"1\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"agv\\\",\\\"schema\\\":\\\"metric\\\",\\\"params\\\":{\\\"field\\\":\\\"metrics_data\\\"}},{\\\"id\\\":\\\"2\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"histogram\\\",\\\"schema\\\":\\\"segment\\\",\\\"params\\\":{\\\"field\\\":\\\"timestamp_file\\\",\\\"interval\\\":1,\\\"min_doc_count\\\":false,\\\"has_extended_bounds\\\":false,\\\"extended_bounds\\\":{\\\"min\\\":\\\"\\\",\\\"max\\\":\\\"\\\"}}}]}\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";
        Generator generator = new Generator();
        KibanaDashboardVisualization kibanaDashboardVisualization1 = new KibanaDashboardVisualization();
        kibanaDashboardVisualization1.setTitle("Count Visualization");
        kibanaDashboardVisualization1.setId("count-visualization");
        kibanaDashboardVisualization1.setVisualizationType("metric");
        kibanaDashboardVisualization1.setIndex("logstash-*");

        KibanaDashboardVisualization kibanaDashboardVisualization2 = new KibanaDashboardVisualization();
        kibanaDashboardVisualization2.setTitle("Line Visualization");
        kibanaDashboardVisualization2.setId("line-visualization");
        kibanaDashboardVisualization2.setVisualizationType("line");
        kibanaDashboardVisualization2.setAggregationType("agv");
        kibanaDashboardVisualization2.setMetricNameX("timestamp_file");
        kibanaDashboardVisualization2.setMetricNameY("metrics_data");
        kibanaDashboardVisualization2.setIndex("logstash-*");


        String json1 = generator.createVisualizationObject(kibanaDashboardVisualization1);
        String json2 = generator.createVisualizationObject(kibanaDashboardVisualization2);

        assertEquals(expectedJson1, json1);
        assertEquals(expectedJson2, json2);
    }

}