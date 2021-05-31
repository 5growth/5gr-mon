
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
import it.nextworks.nfvmano.configmanager.prometheusScraper.PrometheusScraperController;
import it.nextworks.nfvmano.configmanager.rvmagent.RVMAgentRepo;
import it.nextworks.nfvmano.configmanager.rvmagent.messages.RVMAgentCommand;
import it.nextworks.nfvmano.configmanager.rvmagent.model.PrometheusCollector;
import it.nextworks.nfvmano.configmanager.rvmagent.model.RVMAgent;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;
import static org.apache.kafka.common.utils.Utils.readFileAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

@DisplayName("Agent's test")
@ExtendWith(MockitoExtension.class)
@ExtendWith(VertxExtension.class)
class RestApiAgentExporterTest extends MainVerticle {
    Integer port = 8989;
    String host = "127.0.0.1";
    String requestURL = String.format("http://%s:%s",host, port);
    Vertx vertx;

    private PrometheusScraperController prometheusScraperController= mock(PrometheusScraperController.class);
    private RVMAgentRepo rvmAgentRepo = mock(RVMAgentRepo.class);

    private JSONParser parser = new JSONParser();

    @Override
    RVMAgentRepo makeRVMAgentRepo(){
        return rvmAgentRepo;
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



//    @Test
    @DisplayName("RVM Agent add prometheus exporter")
    void rvmAgentAddPrometheusExporter(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //creating mock object
        RVMAgent mockRVMAgent = mock(RVMAgent.class);
        List<RVMAgent> rvmAgentList = Arrays.asList(mockRVMAgent);
        when(this.rvmAgentRepo.findById(anyString())).thenReturn(Future.succeededFuture(Optional.ofNullable(rvmAgentList)));
        PrometheusCollector prometheusCollector = getObjectFromFile("src/test/resources/json/agent/exporter/postMockAddPrometheusCollector.json", PrometheusCollector.class);
        when(mockRVMAgent.addPrometheusCollector(any())).thenReturn(prometheusCollector);
        RVMAgentCommand rvmAgentCommand = getObjectFromFile("src/test/resources/json/agent/exporter/postMockExecuteCommand.json", RVMAgentCommand.class);
        when(mockRVMAgent.executeCommand(any())).thenReturn(rvmAgentCommand);
        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //request body
        String postRequestBodyString = getObjectFromFile("src/test/resources/json/agent/exporter/postRequestBody.json", String.class);

        //referenceResponseBody
        String postReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/agent/exporter/postReferenceResponseBody.json", String.class);

        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.post(requestURL + "/prom-manager/install_exporter")
                        .header("Content-Type", "application/json")
                        .body(postRequestBodyString)
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
                responseReferenceJsonObject = (JSONObject) parser.parse(postReferenceResponseBodyJson);
                responseJsonObject = (JSONObject) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assertEquals(responseReferenceJsonObject, responseJsonObject);
            requestCheckpoint.flag();
        });
    }

    @Test
    @DisplayName("RVM Agent delete prometheus exporter")
    void rvmAgentDeletePrometheusExporter(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //creating mock object
        RVMAgent mockRVMAgent = mock(RVMAgent.class);
        List<RVMAgent> rvmAgentList = Arrays.asList(mockRVMAgent);
        when(this.rvmAgentRepo.findById(anyString())).thenReturn(Future.succeededFuture(Optional.ofNullable(rvmAgentList)));
        RVMAgentCommand rvmAgentCommand = getObjectFromFile("src/test/resources/json/agent/exporter/deleteMockExecuteCommand.json", RVMAgentCommand.class);
        when(mockRVMAgent.executeCommand(any())).thenReturn(rvmAgentCommand);
        Checkpoint requestCheckpoint = testContext.checkpoint(1);

//        //request body
//        String postRequestBodyString = getObjectFromFile("src/test/resources/json/agent/exporter/postRequestBody.json", String.class);

        //referenceResponseBody
        String postReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/agent/exporter/deleteReferenceResponseBody.json", String.class);

        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.delete(requestURL + "/prom-manager/uninstall_exporter/vm_agent_1/node_exporter")
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
                responseReferenceJsonObject = (JSONObject) parser.parse(postReferenceResponseBodyJson);
                responseJsonObject = (JSONObject) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assertEquals(responseReferenceJsonObject, responseJsonObject);
            requestCheckpoint.flag();
        });
    }


}
