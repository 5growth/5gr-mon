

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
import it.nextworks.nfvmano.configmanager.dashboards.DashboardRepo;
import it.nextworks.nfvmano.configmanager.dashboards.MemoryDashboardRepo;
import it.nextworks.nfvmano.configmanager.dashboards.model.Dashboard;
import it.nextworks.nfvmano.configmanager.exporters.ExporterRepo;
import it.nextworks.nfvmano.configmanager.exporters.MemoryExporterRepo;
import it.nextworks.nfvmano.configmanager.prometheusScraper.PrometheusScraperController;
import it.nextworks.nfvmano.configmanager.sb.grafana.GrafanaConnector;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.GrafanaDashboardWrapper;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.PostDashboardResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;

import static io.vertx.core.Future.succeededFuture;
import static java.lang.Thread.sleep;
import static org.apache.kafka.common.utils.Utils.readFileAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

@DisplayName("Graphana's test")
@ExtendWith(MockitoExtension.class)
@ExtendWith(VertxExtension.class)
class RestApiGrafanaTest extends MainVerticle {
    Integer port = 8989;
    String host = "127.0.0.1";
    String requestURL = String.format("http://%s:%s",host, port);
    Vertx vertx;

    private PrometheusScraperController prometheusScraperController= mock(PrometheusScraperController.class);
    private MemoryExporterRepo memoryExporterRepo = mock(MemoryExporterRepo.class);
    private GrafanaConnector grafanaConnector = mock(GrafanaConnector.class);
    private MemoryDashboardRepo memoryDashboardRepo = mock(MemoryDashboardRepo.class);
    private JSONParser parser = new JSONParser();


    @Override
    GrafanaConnector makeGrafanaConnector() {return grafanaConnector;}

    @Override
    DashboardRepo makeMemoryDashboardRepo() {return memoryDashboardRepo;}

    @Override
    void makePrometheusScraperController() {
        setPrometheusScraperController(prometheusScraperController);
    }

    @Override
    public ExporterRepo makeMemoryExporterRepo(){
        return memoryExporterRepo;
    }

    @Override
    void startPrometheusMQAgentAndPushGatewayController(Router router) {}


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
    @DisplayName("Get Dashboards")
    void getDashboards(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //creating mock object
        String mockMemoryDashboardRepoFindAllFileName = "src/test/resources/json/grafana/getMockMemoryDashboardRepoFindAll.json";
        HashSet mockMemoryDashboardRepoFindAll = null;
        try {
            mockMemoryDashboardRepoFindAll = new ObjectMapper().readValue(readFileAsString(mockMemoryDashboardRepoFindAllFileName),
                    HashSet.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        when(this.memoryDashboardRepo.findAll()).thenReturn(succeededFuture(mockMemoryDashboardRepoFindAll));
        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //referenceResponseBody
        String fileResponse = "src/test/resources/json/grafana/getAllReferenceResponseBody.json";
        String getReferenceResponseBodyJson = null;
        try {
            getReferenceResponseBodyJson = readFileAsString(fileResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String finalGetReferenceResponseBodyJson = getReferenceResponseBodyJson;
        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.get(requestURL + "/prom-manager/dashboard")
                        .header("Content-Type", "application/json")
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            String responseBody = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            JSONObject responseReferenceJsonObject = null;
            JSONObject responseJsonObject = null;
            try {
                responseReferenceJsonObject = (JSONObject) parser.parse(finalGetReferenceResponseBodyJson);
                responseJsonObject = (JSONObject) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assertEquals(responseJsonObject, responseReferenceJsonObject);
            requestCheckpoint.flag();
        });

    }

    @Test
    @DisplayName("Post dashboard")
    void createDashboard(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //creating verify object
        String fileReferenceGrafanaDashboardWrapper = "src/test/resources/json/grafana/postReferenceGrafanaDashboardWrapper.json";
        GrafanaDashboardWrapper referenceGrafanaDashboardWrapper = null;
        try {
            referenceGrafanaDashboardWrapper = new ObjectMapper().readValue(readFileAsString(fileReferenceGrafanaDashboardWrapper), GrafanaDashboardWrapper.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }
        GrafanaDashboardWrapper finalReferenceGrafanaDashboardWrapper = referenceGrafanaDashboardWrapper;

        //creating mock object
        String mockDashboardFileName = "src/test/resources/json/grafana/postMockDashboard.json";
        Dashboard mockDashboard = null;
        try {
            mockDashboard = new ObjectMapper().readValue(readFileAsString(mockDashboardFileName), Dashboard.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        String mockPostDashboardResponseFileName = "src/test/resources/json/grafana/postMockPostDashboardResponse.json";
        PostDashboardResponse mockPostDashboardResponse = null;
        try {
            mockPostDashboardResponse = new ObjectMapper().readValue(readFileAsString(mockPostDashboardResponseFileName), PostDashboardResponse.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        Future<PostDashboardResponse> responseFuture = Future.succeededFuture(mockPostDashboardResponse);
        when(this.grafanaConnector.postDashboard(any())).thenReturn(responseFuture);
        when(this.memoryDashboardRepo.save((Dashboard) Mockito.any())).thenReturn(succeededFuture(mockDashboard));
        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //request body
        String file_request = "src/test/resources/json/grafana/postRequestBodyWithoutAgentId.json";
        String postRequestBodyString = null;
        try {
            postRequestBodyString = readFileAsString(file_request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //referenceResponseBody
        String fileResponse = "src/test/resources/json/grafana/postReferenceResponseBodyWithoutAgentId.json";
        String postReferenceResponseBodyJson = null;
        try {
            postReferenceResponseBodyJson = readFileAsString(fileResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String finalPostReferenceResponseBodyJson = postReferenceResponseBodyJson;
        //request
        String finalPostRequestBodyString = postRequestBodyString;


        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.post(requestURL + "/prom-manager/dashboard")
                        .header("Content-Type", "application/json")
                        .body(finalPostRequestBodyString)
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            //check request parameters
            verify(this.grafanaConnector).postDashboard(eq(finalReferenceGrafanaDashboardWrapper));
            //check result
            String responseBody = response.getBody();
            assertThat(response.getStatus()).isEqualTo(201);
            JSONObject responseReferenceJsonObject = null;
            JSONObject responseJsonObject = null;
            try {
                responseReferenceJsonObject = (JSONObject) parser.parse(finalPostReferenceResponseBodyJson);
                responseJsonObject = (JSONObject) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            assertEquals(responseJsonObject, responseReferenceJsonObject);
            requestCheckpoint.flag();
        });
    }

    @Test
    @DisplayName("Update dashboard")
    void UpdateDashboard(Vertx vertx, VertxTestContext testContext) {

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //creating verify object
        String fileReferenceGrafanaDashboardWrapper = "src/test/resources/json/grafana/updateReferenceGrafanaDashboardWrapper.json";
        GrafanaDashboardWrapper referenceGrafanaDashboardWrapper = null;
        try {
            referenceGrafanaDashboardWrapper = new ObjectMapper().readValue(readFileAsString(fileReferenceGrafanaDashboardWrapper), GrafanaDashboardWrapper.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }
        GrafanaDashboardWrapper finalReferenceGrafanaDashboardWrapper = referenceGrafanaDashboardWrapper;

        //creating mock object
        String mockMemoryDashboardRepoFindByIdFileName = "src/test/resources/json/grafana/updateMockMemoryDashboardRepoFindById.json";
        Dashboard mockMemoryDashboardRepoFindById = null;
        try {
            mockMemoryDashboardRepoFindById = new ObjectMapper().readValue(readFileAsString(mockMemoryDashboardRepoFindByIdFileName),
                    Dashboard.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }
        when(this.memoryDashboardRepo.findById(any())).thenReturn(succeededFuture(mockMemoryDashboardRepoFindById));

        String mockPostDashboardResponseFileName = "src/test/resources/json/grafana/updateMockPostDashboardResponseForUpdate.json";
        PostDashboardResponse mockPostDashboardResponse = null;
        try {
            mockPostDashboardResponse = new ObjectMapper().readValue(readFileAsString(mockPostDashboardResponseFileName), PostDashboardResponse.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        Future<PostDashboardResponse> responseFuture = Future.succeededFuture(mockPostDashboardResponse);
        when(this.grafanaConnector.postDashboard(any())).thenReturn(responseFuture);

        String mockDashboardFileName = "src/test/resources/json/grafana/updateMockDashboard.json";
        Dashboard mockDashboard = null;
        try {
            mockDashboard = new ObjectMapper().readValue(readFileAsString(mockDashboardFileName), Dashboard.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        when(this.memoryDashboardRepo.save((Dashboard) Mockito.any())).thenReturn(succeededFuture(mockDashboard));

        //request body
        String dashboardId = "e692OPYGz";
        String file_request = "src/test/resources/json/grafana/updateRequestBody.json";
        String postRequestBodyString = null;
        try {
            postRequestBodyString = readFileAsString(file_request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //referenceResponseBody
        String fileResponse = "src/test/resources/json/grafana/updateReferenceResponseBody.json";
        String postReferenceResponseBodyJson = null;
        try {
            postReferenceResponseBodyJson = readFileAsString(fileResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Checkpoint requestCheckpoint = testContext.checkpoint(1);
        String finalPostReferenceResponseBodyJson = postReferenceResponseBodyJson;
        //request
        String finalPostRequestBodyString = postRequestBodyString;


        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {

                response = Unirest.put(requestURL + "/prom-manager/dashboard/" + dashboardId)
                        .header("Content-Type", "application/json")
                        .body(finalPostRequestBodyString)
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            //check request parameters
            verify(this.memoryDashboardRepo).findById(eq(dashboardId));
            verify(this.grafanaConnector).postDashboard(eq(finalReferenceGrafanaDashboardWrapper));
            //check result
            String responseBody = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            JSONObject responseReferenceJsonObject = null;
            JSONObject responseJsonObject = null;
            try {
                responseReferenceJsonObject = (JSONObject) parser.parse(finalPostReferenceResponseBodyJson);
                responseJsonObject = (JSONObject) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assertEquals(responseJsonObject, responseReferenceJsonObject);
            requestCheckpoint.flag();
        });
    }





    @Test
    @DisplayName("Get Dashboard")
    void getDashboard(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String dashboardId = "mFCecfYMz";

        //creating mock object
        String mockMemoryDashboardRepoFindByIdFileName = "src/test/resources/json/grafana/getMockMemoryDashboardRepoFindById.json";
        Dashboard mockMemoryDashboardRepoFindById = null;
        try {
            mockMemoryDashboardRepoFindById = new ObjectMapper().readValue(readFileAsString(mockMemoryDashboardRepoFindByIdFileName),
                    Dashboard.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        when(this.memoryDashboardRepo.findById(any())).thenReturn(succeededFuture(mockMemoryDashboardRepoFindById));
        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //referenceResponseBody
        String fileResponse = "src/test/resources/json/grafana/getByIdReferenceResponseBody.json";
        String getReferenceResponseBodyJson = null;
        try {
            getReferenceResponseBodyJson = readFileAsString(fileResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String finalGetReferenceResponseBodyJson = getReferenceResponseBodyJson;
        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.get(requestURL + "/prom-manager/dashboard/" + dashboardId)
                        .header("Content-Type", "application/json")
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            verify(this.memoryDashboardRepo).findById(eq(dashboardId));
            String responseBody = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            JSONObject responseReferenceJsonObject = null;
            JSONObject responseJsonObject = null;
            try {
                responseReferenceJsonObject = (JSONObject) parser.parse(finalGetReferenceResponseBodyJson);
                responseJsonObject = (JSONObject) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assertEquals(responseJsonObject, responseReferenceJsonObject);
            requestCheckpoint.flag();
        });
    }


    @Test
    @DisplayName("Delete Dashboard")
    void deleteDashboard(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String dashboardId = "mFCecfYMz";

        //creating mock object
        String mockMemoryDashboardRepoFindByIdFileName = "src/test/resources/json/grafana/getMockMemoryDashboardRepoFindById.json";
        Dashboard mockMemoryDashboardRepoFindById = null;
        try {
            mockMemoryDashboardRepoFindById = new ObjectMapper().readValue(readFileAsString(mockMemoryDashboardRepoFindByIdFileName),
                    Dashboard.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        when(this.memoryDashboardRepo.findById(any())).thenReturn(succeededFuture(mockMemoryDashboardRepoFindById));
        when(this.grafanaConnector.deleteDashboard(any())).thenReturn(succeededFuture());
        when(this.memoryDashboardRepo.deleteById(anyString(), anyBoolean())).thenReturn(Future.succeededFuture(Collections.singleton(dashboardId)));
        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.delete(requestURL + "/prom-manager/dashboard/" + dashboardId)
                        .header("Content-Type", "application/json")
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

            verify(this.memoryDashboardRepo).findById(eq(dashboardId));
            String responseBody = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            JSONObject responseReferenceJsonObject = null;
            JSONObject responseJsonObject = null;
            try {
                responseReferenceJsonObject = (JSONObject) parser.parse("{\"deleted\":[\"" + dashboardId + "\"]}");
                responseJsonObject = (JSONObject) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assertEquals(responseJsonObject, responseReferenceJsonObject);
            requestCheckpoint.flag();
        });


    }

}
