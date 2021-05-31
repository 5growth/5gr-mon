
package it.nextworks.nfvmano.configmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import it.nextworks.nfvmano.configmanager.alerts.AlertRepo;
import it.nextworks.nfvmano.configmanager.alerts.model.Alert;
import it.nextworks.nfvmano.configmanager.prometheusScraper.PrometheusScraperController;
import it.nextworks.nfvmano.configmanager.sb.prometheus.PrometheusConnector;
import it.nextworks.nfvmano.configmanager.sb.prometheus.TargetRepo;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.AlertManagerConfig;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.AlertRules;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static org.apache.kafka.common.utils.Utils.readFileAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

@DisplayName("Alert's test")
@ExtendWith(MockitoExtension.class)
@ExtendWith(VertxExtension.class)
class RestApiAlertTest extends MainVerticle {
    Integer port = 8989;
    String host = "127.0.0.1";
    String requestURL = String.format("http://%s:%s",host, port);
    Vertx vertx;

    private PrometheusScraperController prometheusScraperController= mock(PrometheusScraperController.class);
    private AlertRepo alertRepo = mock(AlertRepo.class);
    private TargetRepo targetRepo = mock(TargetRepo.class);
    private PrometheusConnector prometheusConnector = mock(PrometheusConnector.class);

    private JSONParser parser = new JSONParser();

    @Override
    PrometheusConnector makePrometheusConnector() {
        return prometheusConnector;
    }

    @Override
    AlertRepo makeAlertRepo(){
        return this.alertRepo;
    }

    @Override
    TargetRepo makeTargetRepo(){
        return this.targetRepo;
    }

    @Override
    void makePrometheusScraperController() {
        setPrometheusScraperController(prometheusScraperController);
    }

    @Override
    void startPrometheusMQAgentAndPushGatewayController(Router router) {}

    private <T extends Object> T getObjectFromFile(String fileName, Class<T> type) {
        T object = null;
        if (type.getTypeName() != "java.lang.String"){
            try {
                object = (T) new ObjectMapper().readValue(readFileAsString(fileName), type);
            } catch (IOException e) {
                System.out.println(e.getMessage());}
            }
        else{
            try {
                object = (T) readFileAsString(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        return (T) object;
    }

    void wait_port_availability(String host, int port) {
        boolean checking = true;
        while (checking) {
            try {
                Socket socket = new Socket(host, port);
                checking = false;
                socket.close();
            } catch (IOException e) {
                try {
                    sleep(100);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
    }

    @BeforeEach
    void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(this, testContext.succeeding(id -> {
            wait_port_availability(host, port);
            testContext.completeNow();

        }));
    }


    @Test
    @DisplayName("Get Alerts")
    void getAlerts(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //creating mock object
        Set alerts = getObjectFromFile("src/test/resources/json/alert/getAllMockAlertRepoFindAll.json", Set.class);
        when(this.alertRepo.findAll()).thenReturn(Future.succeededFuture(alerts));

        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //referenceResponseBody
        String getReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/alert/getAllReferenceResponseBody.json", String.class);

        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.get(requestURL + "/prom-manager/alert")
                        .header("Content-Type", "application/json")
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            //check result
            String responseBody = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            JSONArray responseReferenceJsonObject = null;
            JSONArray responseJsonObject = null;
            try {
                responseReferenceJsonObject = (JSONArray) parser.parse(getReferenceResponseBodyJson);
                responseJsonObject = (JSONArray) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assertEquals(responseJsonObject, responseReferenceJsonObject);
            requestCheckpoint.flag();
        });

    }




    @Test
    @DisplayName("Post alert")
    void createAlert(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //creating mock object
        AlertRules rules = getObjectFromFile("src/test/resources/json/alert/postMockConnectorGetRules.json", AlertRules.class);
        when(this.prometheusConnector.getRules()).thenReturn(rules);
        when(this.prometheusConnector.setRules(any())).thenReturn(Future.succeededFuture());
        when(this.prometheusConnector.setAMConfig(any())).thenReturn(Future.succeededFuture());
        AlertManagerConfig alertManagerConfig = getObjectFromFile("src/test/resources/json/alert/postMockConnectorGetAMConfig.json", AlertManagerConfig.class);
        when(this.prometheusConnector.getAMConfig()).thenReturn(alertManagerConfig);
        when(this.targetRepo.saveOrGet(any())).thenReturn(UUID.fromString("d36b3293-2455-4e39-a09f-312fb7f514f0"));
        Alert alertSave = getObjectFromFile("src/test/resources/json/alert/postMockAlertRepoSave.json", Alert.class);
        when(this.alertRepo.save(any())).thenReturn(Future.succeededFuture(alertSave));

        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //request body
        String postRequestBodyString = getObjectFromFile("src/test/resources/json/alert/postRequestBodyWithoutAgentId.json", String.class);

        //referenceResponseBody
        String postReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/alert/postReferenceResponseBodyWithoutAgentId.json", String.class);

        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.post(requestURL + "/prom-manager/alert")
                        .header("Content-Type", "application/json")
                        .body(postRequestBodyString)
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            //check result
            String responseBody = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            JSONObject responseReferenceJsonObject = null;
            JSONObject responseJsonObject = null;
            try {
                responseReferenceJsonObject = (JSONObject) parser.parse(postReferenceResponseBodyJson);
                responseJsonObject = (JSONObject) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            assertEquals(responseJsonObject, responseReferenceJsonObject);
            requestCheckpoint.flag();
        });
    }

    @Test
    @DisplayName("Update Alert")
    void updateAlert(Vertx vertx, VertxTestContext testContext) {

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //creating mock object
        String alertId = "f05c4c69-cedf-4fe5-810a-18acc9f20ca6";
        AlertRules rules = getObjectFromFile("src/test/resources/json/alert/putMockConnectorGetRules.json", AlertRules.class);
        when(this.prometheusConnector.getRules()).thenReturn(rules);
        when(this.targetRepo.saveOrGet(any())).thenReturn(UUID.fromString(alertId));
        AlertManagerConfig alertManagerConfig = getObjectFromFile("src/test/resources/json/alert/putMockConnectorGetAMConfig.json", AlertManagerConfig.class);
        when(this.prometheusConnector.getAMConfig()).thenReturn(alertManagerConfig);
        when(this.prometheusConnector.setAMConfig(any())).thenReturn(Future.succeededFuture());
        when(this.prometheusConnector.setRules(any())).thenReturn(Future.succeededFuture());
        Alert alertSave = getObjectFromFile("src/test/resources/json/alert/putMockAlertRepoUpdate.json", Alert.class);
        when(this.alertRepo.update(any())).thenReturn(Future.succeededFuture(alertSave));

        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //request body
        String putRequestBodyString = getObjectFromFile("src/test/resources/json/alert/putRequestBody.json", String.class);

        //referenceResponseBody
        String postReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/alert/putReferenceResponseBody.json", String.class);

        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.put(requestURL + "/prom-manager/alert/"+ alertId)
                        .header("Content-Type", "application/json")
                        .body(putRequestBodyString)
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            //check result
            String responseBody = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            JSONObject responseReferenceJsonObject = null;
            JSONObject responseJsonObject = null;
            try {
                responseReferenceJsonObject = (JSONObject) parser.parse(postReferenceResponseBodyJson);
                responseJsonObject = (JSONObject) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            assertEquals(responseJsonObject, responseReferenceJsonObject);
            requestCheckpoint.flag();
        });

    }

    @Test
    @DisplayName("Get Alert")
    void getAlert(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //creating mock object
        Alert alert = getObjectFromFile("src/test/resources/json/alert/getMockAlertRepoFindById.json", Alert.class);
        when(this.alertRepo.findById(any())).thenReturn(Future.succeededFuture(Optional.ofNullable(alert)));
        String alertId = "5ede96f0-adf0-4417-acdb-7a536f4f4378";

        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //referenceResponseBody
        String getReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/alert/getReferenceResponseBodyById.json", String.class);

        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.get(requestURL + "/prom-manager/alert/" + alertId)
                        .header("Content-Type", "application/json")
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            //check result
            String responseBody = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            JSONObject responseReferenceJsonObject = null;
            JSONObject responseJsonObject = null;
            try {
                responseReferenceJsonObject = (JSONObject) parser.parse(getReferenceResponseBodyJson);
                responseJsonObject = (JSONObject) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            assertEquals(responseJsonObject, responseReferenceJsonObject);
            requestCheckpoint.flag();
        });

    }


    @Test
    @DisplayName("Delete Alert")
    void deleteAlert(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //creating mock object
        String alertId = "19061b80-88c3-4654-b00f-6a4840912f46";
        AlertRules rules = getObjectFromFile("src/test/resources/json/alert/deleteMockConnectorGetRules.json", AlertRules.class);
        AlertManagerConfig alertManagerConfig = getObjectFromFile("src/test/resources/json/alert/deleteMockConnectorGetAMConfig.json", AlertManagerConfig.class);
        when(this.prometheusConnector.getAMConfig()).thenReturn(alertManagerConfig);
        when(this.prometheusConnector.setAMConfig(any())).thenReturn(Future.succeededFuture());
        when(this.prometheusConnector.getRules()).thenReturn(rules);
        when(this.prometheusConnector.setRules(any())).thenReturn(Future.succeededFuture());
        when(this.targetRepo.saveOrGet(any())).thenReturn(UUID.fromString(alertId));
//        Alert alertSave = getObjectFromFile("src/test/resources/json/alert/postMockAlertRepoSave.json", Alert.class);
        when(this.alertRepo.deleteById(any(), anyBoolean())).thenReturn(Future.succeededFuture(Collections.singleton(alertId)));

        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //referenceResponseBody
        String getReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/alert/deleteReferenceResponseBodyById.json", String.class);

        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.delete(requestURL + "/prom-manager/alert/" + alertId)
                        .header("Content-Type", "application/json")
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            //check result
            String responseBody = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            JSONObject responseReferenceJsonObject = null;
            JSONObject responseJsonObject = null;
            try {
                responseReferenceJsonObject = (JSONObject) parser.parse(getReferenceResponseBodyJson);
                responseJsonObject = (JSONObject) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            assertEquals(responseReferenceJsonObject, responseJsonObject);
            requestCheckpoint.flag();
        });
    }

}
