
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
import it.nextworks.nfvmano.configmanager.exporters.ExporterRepo;
import it.nextworks.nfvmano.configmanager.exporters.MemoryExporterRepo;
import it.nextworks.nfvmano.configmanager.exporters.model.Exporter;
import it.nextworks.nfvmano.configmanager.exporters.model.ExporterDescription;
import it.nextworks.nfvmano.configmanager.prometheusScraper.PrometheusScraperController;
import it.nextworks.nfvmano.configmanager.sb.prometheus.PrometheusConnector;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.PrometheusConfig;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static io.vertx.core.Future.succeededFuture;
import static java.lang.Thread.sleep;
import static org.apache.kafka.common.utils.Utils.readFileAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

@DisplayName("Prometheus's test")
@ExtendWith(MockitoExtension.class)
@ExtendWith(VertxExtension.class)
class RestApiExporterTest extends MainVerticle {
    Integer port = 8989;
    String host = "127.0.0.1";
    String requestURL = String.format("http://%s:%s",host, port);
    Vertx vertx;
    private PrometheusConnector prometheusConnector = mock(PrometheusConnector.class);
    private PrometheusScraperController prometheusScraperController= mock(PrometheusScraperController.class);
    private MemoryExporterRepo memoryExporterRepo = mock(MemoryExporterRepo.class);
    private JSONParser parser = new JSONParser();

    @Override
    void makePrometheusScraperController() {
        setPrometheusScraperController(prometheusScraperController);
    }

    @Override
    PrometheusConnector makePrometheusConnector() {
        return prometheusConnector;
    }

    @Override
    public ExporterRepo makeMemoryExporterRepo(){
        return memoryExporterRepo;
    }

    @Override
    void startPrometheusMQAgentAndPushGatewayController(Router router) {}


    void wait_port_availability(String host, int port) {
        boolean checking = true;
        SocketChannel socketChannel = null;
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
    @DisplayName("Get exporters")
    void getExporters(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //creating mock object
        String memoryExporterRepoFindAllFileName = "src/test/resources/json/exporter/getMemoryExporterRepoFindAll.json";
        Map memoryExporterRepoFindAll = null;
        try {
            memoryExporterRepoFindAll = new ObjectMapper().readValue(readFileAsString(memoryExporterRepoFindAllFileName), Map.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }
        HashSet setMemoryExporterRepoFindAll = new HashSet<>(memoryExporterRepoFindAll.values());
        when(this.memoryExporterRepo.findAll()).thenReturn(succeededFuture(setMemoryExporterRepoFindAll));

        Checkpoint requestCheckpoint = testContext.checkpoint(1);
        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.get(requestURL + "/prom-manager/exporter").asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            //check result
            String responseBody = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            String fileResponse = "src/test/resources/json/exporter/getAllReferenceResponseBody.json";
            JSONObject responseReferenceJsonObject = null;
            JSONObject responseJsonObject = null;
            try {
                responseReferenceJsonObject = (JSONObject) parser.parse(new FileReader(fileResponse));
                responseJsonObject = (JSONObject) parser.parse(responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assertEquals(responseJsonObject, responseReferenceJsonObject);
            requestCheckpoint.flag();
        });
    }

    @Test
    @DisplayName("Post exporter")
    void createExporter(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //creating mock object
        String mockPrometheusConfigFileName = "src/test/resources/json/exporter/mockPrometheusConfig.json";
        PrometheusConfig prometheusConfig = null;
        try {
            prometheusConfig = new ObjectMapper().readValue(readFileAsString(mockPrometheusConfigFileName), PrometheusConfig.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        when(this.prometheusConnector.getConfig()).thenReturn(prometheusConfig);
        when(this.prometheusConnector.setConfig(any())).thenReturn(succeededFuture());

        String mockMemoryExporterRepoSaveFileName = "src/test/resources/json/exporter/putMemoryExporterRepoSave.json";
        Exporter mockMemoryExporterRepoSave = null;
        try {
            mockMemoryExporterRepoSave = new ObjectMapper().readValue(readFileAsString(mockMemoryExporterRepoSaveFileName), Exporter.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        when(this.memoryExporterRepo.save((Exporter) any())).thenReturn(succeededFuture(mockMemoryExporterRepoSave));
        when(this.memoryExporterRepo.save((ExporterDescription) any())).thenCallRealMethod();
        Future<Void> aux = Future.succeededFuture();

        when(this.prometheusConnector.setConfig(any())).thenReturn(aux);
        Checkpoint requestCheckpoint = testContext.checkpoint(1);
        //request body
        String file_request = "src/test/resources/json/exporter/postRequestBodyWithoutAgentId.json";
        String postRequestBodyString = null;
        try {
            postRequestBodyString = readFileAsString(file_request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //referenceResponseBody
        String fileResponse = "src/test/resources/json/exporter/postReferenceResponseBodyWithoutAgentId.json";
        String postReferenceResponseBodyJson = null;
        try {
            postReferenceResponseBodyJson = readFileAsString(fileResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //request
        String finalPostRequestBodyString = postRequestBodyString;
        String finalPostReferenceResponseBodyJson = postReferenceResponseBodyJson;
        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.post(requestURL + "/prom-manager/exporter")
                        .header("Content-Type", "application/json")
                        .body(finalPostRequestBodyString)
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

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
            //delete unchecked parameters
            responseReferenceJsonObject.remove("exporterId");
            responseJsonObject.remove("exporterId");
            assertEquals(responseJsonObject, responseReferenceJsonObject);
            requestCheckpoint.flag();
        });
    }

    @Test
    @DisplayName("Update exporter")
    void UpdateExporter(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //creating mock object
        String mockPrometheusConfigFileName = "src/test/resources/json/exporter/mockPrometheusConfig.json";
        PrometheusConfig prometheusConfig = null;
        try {
            prometheusConfig = new ObjectMapper().readValue(readFileAsString(mockPrometheusConfigFileName), PrometheusConfig.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        when(this.prometheusConnector.getConfig()).thenReturn(prometheusConfig);
        when(this.prometheusConnector.setConfig(any())).thenReturn(succeededFuture());

        String mockMemoryExporterRepoSaveFileName = "src/test/resources/json/exporter/putMemoryExporterRepoSave.json";
        Exporter mockMemoryExporterRepoSave = null;
        try {
            mockMemoryExporterRepoSave = new ObjectMapper().readValue(readFileAsString(mockMemoryExporterRepoSaveFileName), Exporter.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        when(this.memoryExporterRepo.update((Exporter) any())).thenReturn(succeededFuture(mockMemoryExporterRepoSave));
        Future<Void> aux = Future.succeededFuture();
        when(this.prometheusConnector.setConfig(any())).thenReturn(aux);
        Checkpoint requestCheckpoint = testContext.checkpoint(1);
        //request body
        String file_request = "src/test/resources/json/exporter/putRequestBody.json";
        String postRequestBodyString = null;
        try {
            postRequestBodyString = readFileAsString(file_request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //referenceResponseBody
        String fileResponse = "src/test/resources/json/exporter/putReferenceResponseBody.json";
        String postReferenceResponseBodyJson = null;
        try {
            postReferenceResponseBodyJson = readFileAsString(fileResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //request
        String finalPostRequestBodyString = postRequestBodyString;
        String finalPostReferenceResponseBodyJson = postReferenceResponseBodyJson;
        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.put(requestURL + "/prom-manager/exporter/299e0f5b-e505-498d-8c63-4fc49b0ef430")
                        .header("Content-Type", "application/json")
                        .body(finalPostRequestBodyString)
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
                responseReferenceJsonObject = (JSONObject) parser.parse(finalPostReferenceResponseBodyJson);
                responseJsonObject = (JSONObject) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //delete unchecked parameters
            responseReferenceJsonObject.remove("exporterId");
            responseJsonObject.remove("exporterId");
            assertEquals(responseJsonObject, responseReferenceJsonObject);
            requestCheckpoint.flag();
        });
    }





    @Test
    @DisplayName("Get exporter")
    void getExporter(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String exporterId = "c598554f-e301-44db-8787-33eec511cf05";
        //creating mock object
        String memoryExporterRepoFindByIdFileName = "src/test/resources/json/exporter/getMemoryExporterRepoFindById.json";
        Exporter memoryExporterRepoFindById = null;
        try {
            memoryExporterRepoFindById = new ObjectMapper().readValue(readFileAsString(memoryExporterRepoFindByIdFileName), Exporter.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        when(this.memoryExporterRepo.findById(any())).thenReturn(Future.succeededFuture(Optional.ofNullable(memoryExporterRepoFindById)));

        //referenceResponseBody
        String fileResponse = "src/test/resources/json/exporter/getByIdReferenceResponseBody.json";
        String getReferenceResponseBodyJson = null;
        try {
            getReferenceResponseBodyJson = readFileAsString(fileResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Checkpoint requestCheckpoint = testContext.checkpoint(1);
        String finalGetReferenceResponseBodyJson = getReferenceResponseBodyJson;
        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.get(requestURL + "/prom-manager/exporter/" + exporterId).asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            //check result
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
    @DisplayName("Delete exporter")
    void deleteExporter(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String exporterId = "299e0f5b-e505-498d-8c63-4fc49b0ef430";
        //creating mock object

        String mockPrometheusConfigFileName = "src/test/resources/json/exporter/mockPrometheusConfig.json";
        PrometheusConfig prometheusConfig = null;
        try {
            prometheusConfig = new ObjectMapper().readValue(readFileAsString(mockPrometheusConfigFileName), PrometheusConfig.class);

        } catch (
                IOException e) {
            e.printStackTrace();
        }

        when(this.prometheusConnector.getConfig()).thenReturn(prometheusConfig);
        when(this.prometheusConnector.setConfig(any())).thenReturn(Future.succeededFuture());
        when(this.memoryExporterRepo.deleteById(any())).thenReturn(Future.succeededFuture(Collections.singleton(exporterId)));
        Checkpoint requestCheckpoint = testContext.checkpoint(1);
        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.delete(requestURL + "/prom-manager/exporter/" + exporterId).asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            //check result
            String responseBody = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            String referenceResponseBody = "{\"deleted\":[\"299e0f5b-e505-498d-8c63-4fc49b0ef430\"]}";
            JSONObject responseReferenceJsonObject = null;
            JSONObject responseJsonObject = null;
            try {
                responseReferenceJsonObject = (JSONObject) parser.parse(referenceResponseBody);
                responseJsonObject = (JSONObject) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assertEquals(responseJsonObject, responseReferenceJsonObject);
            requestCheckpoint.flag();
        });
    }



}
