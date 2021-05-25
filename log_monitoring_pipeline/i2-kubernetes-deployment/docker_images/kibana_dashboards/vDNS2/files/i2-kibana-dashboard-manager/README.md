# Kibana dashboards manager

To include kibana-dashboard-generator (if not included):

```sh
cp -r ../i2-kibana-dashboard-generator kibana-dashboards
```

## Build

Before building the application, remember that, depending on the scenario where this component has to be deployed, the application.properties variables must change consequently

```shell script
# within this project
cd kibana-dashboards
mvn clean install -DskipTests # install the module in local .m2 maven repository
cd ..
mvn clean install -DskipTests
```

## REST endpoint docs

Swagger UI
- http://localhost:8080/swagger-ui/index.html

OpenAPI descriptor
- http://localhost:8080/v3/api-docs.yaml
