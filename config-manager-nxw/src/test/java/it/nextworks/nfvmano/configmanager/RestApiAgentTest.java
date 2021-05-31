
package it.nextworks.nfvmano.configmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import it.nextworks.nfvmano.configmanager.prometheusScraper.PrometheusScraperController;
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

import static java.lang.Thread.sleep;
import static org.apache.kafka.common.utils.Utils.readFileAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

@DisplayName("Agent's test")
@ExtendWith(MockitoExtension.class)
@ExtendWith(VertxExtension.class)
class RestApiAgentTest extends MainVerticle {
    Integer port = 8989;
    String host = "127.0.0.1";
    String requestURL = String.format("http://%s:%s",host, port);
    Vertx vertx;

    private PrometheusScraperController prometheusScraperController= mock(PrometheusScraperController.class);

    private JSONParser parser = new JSONParser();

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
    @DisplayName("Post Agent without agent id")
    void createAgentWithoutAgentId(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //request body
        String postRequestBodyString = getObjectFromFile("src/test/resources/json/agent/postRequestBodyWithoutAgentId.json", String.class);

        //referenceResponseBody
        String postReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/agent/postReferenceResponseBodyWithoutAgentId.json", String.class);

        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.post(requestURL + "/prom-manager/agent")
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
            assertNotNull(responseJsonObject.get("agent_id"));
            assertTrue(responseJsonObject.get("agent_id").toString().startsWith("vm_agent_"));
            assertNotNull(responseJsonObject.get("cloud_init_script"));
            assertTrue(responseJsonObject.get("cloud_init_script").toString().contains("Download and execute the script that"));
            responseJsonObject.remove("agent_id");
            responseJsonObject.remove("cloud_init_script");
            responseReferenceJsonObject.remove("agent_id");
            responseReferenceJsonObject.remove("cloud_init_script");
            assertEquals(responseReferenceJsonObject, responseJsonObject);
            requestCheckpoint.flag();
        });
    }

    @Test
    @DisplayName("Post Agent with agent id")
    void createAgentWithAgentId(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //request body
        String postRequestBodyString = getObjectFromFile("src/test/resources/json/agent/postRequestBodyWithAgentId.json", String.class);

        //referenceResponseBody
        String postReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/agent/postReferenceResponseBodyWithAgentId.json", String.class);

        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.post(requestURL + "/prom-manager/agent")
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
            assertNotNull(responseJsonObject.get("cloud_init_script"));
            assertTrue(responseJsonObject.get("cloud_init_script").toString().contains("Download and execute the script that"));
            responseJsonObject.remove("cloud_init_script");
            responseReferenceJsonObject.remove("cloud_init_script");
            assertEquals(responseReferenceJsonObject, responseJsonObject);
            requestCheckpoint.flag();
        });
    }

    @Test
    @DisplayName("Get Agent")
    void getAgent(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //request body
        String postRequestBodyString = getObjectFromFile("src/test/resources/json/agent/postRequestBodyWithAgentId.json", String.class);

        //referenceResponseBody
        String postReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/agent/postReferenceResponseBodyWithAgentId.json", String.class);

        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.post(requestURL + "/prom-manager/agent")
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
            assertNotNull(responseJsonObject.get("cloud_init_script"));
            assertTrue(responseJsonObject.get("cloud_init_script").toString().contains("Download and execute the script that"));
            responseJsonObject.remove("cloud_init_script");
            responseReferenceJsonObject.remove("cloud_init_script");
            assertEquals(responseReferenceJsonObject, responseJsonObject);
            String agent_id = (String) responseJsonObject.get("agent_id");

            //referenceResponseBody
            String getReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/agent/getReferenceResponseBody.json", String.class);

            try {
                response = Unirest.get(requestURL + "/prom-manager/agent/" + agent_id)
                        .header("Content-Type", "application/json")
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            //check result
            responseBody = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            JSONArray responseReferenceJsonArray = null;
            JSONArray responseJsonArray = null;
            try {
                responseReferenceJsonArray = (JSONArray) parser.parse(getReferenceResponseBodyJson);
                responseJsonArray = (JSONArray) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assertEquals(responseReferenceJsonArray, responseJsonArray);
            requestCheckpoint.flag();
        });
    }

    @Test
    @DisplayName("Get Agents")
    void getAgents(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //request body
        String postRequestBodyString = getObjectFromFile("src/test/resources/json/agent/postRequestBodyWithAgentId.json", String.class);

        //referenceResponseBody
        String postReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/agent/postReferenceResponseBodyWithAgentId.json", String.class);

        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.post(requestURL + "/prom-manager/agent")
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
            assertNotNull(responseJsonObject.get("cloud_init_script"));
            assertTrue(responseJsonObject.get("cloud_init_script").toString().contains("Download and execute the script that"));
            responseJsonObject.remove("cloud_init_script");
            responseReferenceJsonObject.remove("cloud_init_script");
            assertEquals(responseReferenceJsonObject, responseJsonObject);
            String agent_id = (String) responseJsonObject.get("agent_id");

            //referenceResponseBody
            String getReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/agent/getAgentsReferenceResponseBody.json", String.class);

            try {
                response = Unirest.get(requestURL + "/prom-manager/agent")
                        .header("Content-Type", "application/json")
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            //check result
            responseBody = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            JSONArray responseReferenceJsonArray = null;
            JSONArray responseJsonArray = null;
            try {
                responseReferenceJsonArray = (JSONArray) parser.parse(getReferenceResponseBodyJson);
                responseJsonArray = (JSONArray) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assertEquals(responseReferenceJsonArray, responseJsonArray);
            requestCheckpoint.flag();
        });
    }





    @Test
    @DisplayName("Delete Agent")
    void deleteAgent(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //request body
        String postRequestBodyString = getObjectFromFile("src/test/resources/json/agent/postRequestBodyWithAgentId.json", String.class);

        //referenceResponseBody
        String postReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/agent/postReferenceResponseBodyWithAgentId.json", String.class);

        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.post(requestURL + "/prom-manager/agent")
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
            assertNotNull(responseJsonObject.get("cloud_init_script"));
            assertTrue(responseJsonObject.get("cloud_init_script").toString().contains("Download and execute the script that"));
            responseJsonObject.remove("cloud_init_script");
            responseReferenceJsonObject.remove("cloud_init_script");
            assertEquals(responseReferenceJsonObject, responseJsonObject);
            String agent_id = (String) responseJsonObject.get("agent_id");

            //referenceResponseBody
            String deleteReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/agent/deleteReferenceResponseBody.json", String.class);

            try {
                response = Unirest.delete(requestURL + "/prom-manager/agent/" + agent_id)
                        .header("Content-Type", "application/json")
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            //check result
            responseBody = response.getBody();
            assertThat(response.getStatus()).isEqualTo(200);
            responseReferenceJsonObject = null;
            responseJsonObject = null;
            try {
                responseReferenceJsonObject = (JSONObject) parser.parse(deleteReferenceResponseBodyJson);
                responseJsonObject = (JSONObject) parser.parse(responseBody);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assertEquals(responseReferenceJsonObject, responseJsonObject);
            requestCheckpoint.flag();
        });
    }

}
