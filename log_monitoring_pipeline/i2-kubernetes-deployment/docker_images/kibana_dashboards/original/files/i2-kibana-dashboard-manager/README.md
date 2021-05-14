# Kibana dashboards manager
To include kibana-dashboard-generator module use `--recursive` flag (do it only the first time, if the repository does not contain the kibana-dashboard-generator repository)

```git clone --recursive git@gitlab.com:telcaria-code/5growth/i2-kibana-dashboard-generator.git```

## Build

Before building the application, remember that, depending on the scenario where this component has to be deployed, the application.properties variables must change consequently


```shell script
# within this project
mv i2-kibana-dashboard-generator kibana-dashboards
cd kibana-dashboards
mvn clean install # install the module in local .m2 maven repository
cd ..
mvn clean install
```

## REST endpoint docs

Swagger UI
- http://localhost:8080/swagger-ui/index.html

OpenAPI descriptor
- http://localhost:8080/v3/api-docs.yaml
