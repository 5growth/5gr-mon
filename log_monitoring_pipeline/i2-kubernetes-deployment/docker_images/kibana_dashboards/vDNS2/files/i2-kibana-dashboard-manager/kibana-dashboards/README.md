#Kibana Dashboard API Json Generator
##How to use
1. Clone project.
2. ``mvn clean install`` to install dependency.

##Example

```java
KibanaDashboardVisualization kibanaDashboardVisualization = new KibanaDashboardVisualization();
kibanaDashboardVisualization.setTitle("Line Visualization");
kibanaDashboardVisualization.setId("line-visualization");
kibanaDashboardVisualization.setVisualizationType("line");
kibanaDashboardVisualization.setAggregationType("agv");
kibanaDashboardVisualization.setMetricNameX("timestamp_file");
kibanaDashboardVisualization.setMetricNameY("metrics_data");
kibanaDashboardVisualization.setIndex("logstash-*")

Generator generator = new Generator();
String json = generator.createVisualizationObject(kibanaDashboardVisualization);
```
Result:
```json
"{\n
  \"objects\": [\n
    {\n
      \"id\": \"line-visualization\",\n
      \"type\": \"visualization\",\n
      \"version\": 1,\n
      \"attributes\": {\n
        \"title\": \"Line Visualization\",\n
        \"uiStateJSON\": \"{}\",\n
        \"description\": \"\",\n
        \"version\": 1,\n
        \"kibanaSavedObjectMeta\": {\n
          \"searchSourceJSON\": \"{\\\"index\\\":\\\"logstash-*\\\",\\\"query\\\":{\\\"query\\\":\\\"\\\",\\\"language\\\":\\\"lucene\\\"},\\\"filter\\\":[]}\"\n
        },\n
        \"visState\": \"{\\\"title\\\":\\\"Line Visualization\\\",\\\"type\\\":\\\"line\\\", \\\"params\\\":{\\\"type\\\":\\\"line\\\",\\\"grid\\\":{\\\"categoryLines\\\":false},\\\"categoryAxes\\\":[{\\\"id\\\":\\\"CategoryAxis-1\\\",\\\"type\\\":\\\"category\\\",\\\"position\\\":\\\"bottom\\\",\\\"show\\\":true,\\\"style\\\":{},\\\"scale\\\":{\\\"type\\\":\\\"linear\\\"},\\\"labels\\\":{\\\"show\\\":true,\\\"filter\\\":true,\\\"truncate\\\":100},\\\"title\\\":{}}],\\\"valueAxes\\\":[{\\\"id\\\":\\\"ValueAxis-1\\\",\\\"name\\\":\\\"LeftAxis-1\\\",\\\"type\\\":\\\"value\\\",\\\"position\\\":\\\"left\\\",\\\"show\\\":true,\\\"style\\\":{},\\\"scale\\\":{\\\"type\\\":\\\"linear\\\",\\\"mode\\\":\\\"normal\\\"},\\\"labels\\\":{\\\"show\\\":true,\\\"rotate\\\":0,\\\"filter\\\":false,\\\"truncate\\\":100},\\\"title\\\":{\\\"text\\\":\\\"Average metrics_data\\\"}}],\\\"seriesParams\\\":[{\\\"show\\\":\\\"true\\\",\\\"type\\\":\\\"line\\\",\\\"mode\\\":\\\"normal\\\",\\\"data\\\":{\\\"label\\\":\\\"Average metrics_data\\\",\\\"id\\\":\\\"1\\\"},\\\"valueAxis\\\":\\\"ValueAxis-1\\\",\\\"drawLinesBetweenPoints\\\":true,\\\"showCircles\\\":true}],\\\"addTooltip\\\":true,\\\"addLegend\\\":true,\\\"legendPosition\\\":\\\"right\\\",\\\"times\\\":[],\\\"addTimeMarker\\\":false,\\\"dimensions\\\":{\\\"x\\\":{\\\"accessor\\\":0,\\\"format\\\":{\\\"id\\\":\\\"number\\\"},\\\"params\\\":{\\\"interval\\\":4000},\\\"aggType\\\":\\\"histogram\\\"},\\\"y\\\":[{\\\"accessor\\\":1,\\\"format\\\":{\\\"id\\\":\\\"number\\\"},\\\"params\\\":{},\\\"aggType\\\":\\\"agv\\\"}]}},\\\"aggs\\\":[{\\\"id\\\":\\\"1\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"agv\\\",\\\"schema\\\":\\\"metric\\\",\\\"params\\\":{\\\"field\\\":\\\"metrics_data\\\"}},{\\\"id\\\":\\\"2\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"histogram\\\",\\\"schema\\\":\\\"segment\\\",\\\"params\\\":{\\\"field\\\":\\\"timestamp_file\\\",\\\"interval\\\":1,\\\"min_doc_count\\\":false,\\\"has_extended_bounds\\\":false,\\\"extended_bounds\\\":{\\\"min\\\":\\\"\\\",\\\"max\\\":\\\"\\\"}}}]}\"\n
      }\n
    }\n
  ]\n
}\n"
```