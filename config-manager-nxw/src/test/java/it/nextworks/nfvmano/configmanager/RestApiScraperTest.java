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
import it.nextworks.nfvmano.configmanager.prometheusScraper.MemoryPrometheusScraperRepo;
import it.nextworks.nfvmano.configmanager.prometheusScraper.PrometheusScraperRepo;
import it.nextworks.nfvmano.configmanager.prometheusScraper.model.PrometheusScraper;
import it.nextworks.nfvmano.configmanager.sb.PrometheusScraper.PrometheusScraperConnector;
import it.nextworks.nfvmano.configmanager.sb.prometheus.PrometheusConnector;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.CalculateRules;
import org.apache.kafka.clients.producer.KafkaProducer;
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
import java.util.HashMap;
import java.util.Optional;

import static java.lang.Thread.sleep;
import static org.apache.kafka.common.utils.Utils.readFileAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@DisplayName("Scraper's test")
@ExtendWith(MockitoExtension.class)
@ExtendWith(VertxExtension.class)
class RestApiScraperTest extends MainVerticle {
    Integer port = 8989;
    String host = "127.0.0.1";
    String requestURL = String.format("http://%s:%s",host, port);
    Vertx vertx;

    private MemoryPrometheusScraperRepo prometheusScraperRepo  = mock(MemoryPrometheusScraperRepo.class);
    private PrometheusScraperConnector prometheusScraperConnector = mock(PrometheusScraperConnector.class);
    private PrometheusConnector prometheusConnector = mock(PrometheusConnector.class);
    private KafkaProducer kafkaProducerForScraper = mock(KafkaProducer.class);

    private JSONParser parser = new JSONParser();

    @Override
    PrometheusConnector makePrometheusConnector() {
        return prometheusConnector;
    }

    @Override
    public PrometheusScraperRepo makePrometheusScraperRepo(){
        return this.prometheusScraperRepo;
    }

    @Override
    public PrometheusScraperConnector makePrometheusScraperConnector(){
        return this.prometheusScraperConnector;
    }

    @Override
    KafkaProducer makekafkaProducerForScraper(){
        return kafkaProducerForScraper;
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
    @DisplayName("Post scraper")
    void createAlert(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //creating mock object
        CalculateRules rules = getObjectFromFile("src/test/resources/json/scraper/postMockConnectorGetCalculateRules.json", CalculateRules.class);
        when(this.prometheusConnector.getCalculateRules()).thenReturn(rules);
        when(this.prometheusConnector.setCalculateRules(any())).thenReturn(Future.succeededFuture());



        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //request body
        String postRequestBodyString = getObjectFromFile("src/test/resources/json/scraper/postRequestBodyWithoutAgentId.json", String.class);

        //referenceResponseBody
        String postReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/scraper/postReferenceResponseBodyWithoutAgentId.json", String.class);

        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.post(requestURL + "/prom-manager/prometheus_scraper")
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
            assertNotNull(responseJsonObject.get("scraperId"));
            responseJsonObject.remove("scraperId");
            responseReferenceJsonObject.remove("scraperId");
            assertEquals(responseReferenceJsonObject, responseJsonObject);
            requestCheckpoint.flag();
        });
    }



    @Test
    @DisplayName("Delete Scraper")
    void deleteScraper(Vertx vertx, VertxTestContext testContext) {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //creating mock object
        String scraperId = "26a7cf17-8c9f-4005-abf0-7c8631de0319";
        //creating mock object
        CalculateRules rules = getObjectFromFile("src/test/resources/json/scraper/postMockConnectorGetCalculateRules.json", CalculateRules.class);
        when(this.prometheusConnector.getCalculateRules()).thenReturn(rules);
        when(this.prometheusConnector.setCalculateRules(any())).thenReturn(Future.succeededFuture());

        PrometheusScraper prometheusScraper = getObjectFromFile("src/test/resources/json/scraper/deleteMockPrometheusScraperRepoFindbyId.json", PrometheusScraper.class);
        Future<Optional<PrometheusScraper>> optionalFuture  = Future.succeededFuture(Optional.ofNullable(prometheusScraper));
        when(this.prometheusScraperRepo.findById(any())).thenReturn(optionalFuture);
        String key = "fgt-f655e4d-1422-411f-bde5-df7a79277878_webserver";
        HashMap<String, String> metricTopicMap = new HashMap<String, String>();
        this.getScraperHashMap().put(key, metricTopicMap);
        this.getScraperHashMap().get(key).put("latency_ms", "fgt-f655e4d-1422-411f-bde5-df7a79277878_scaling");

        Checkpoint requestCheckpoint = testContext.checkpoint(1);

        //referenceResponseBody
        String getReferenceResponseBodyJson = getObjectFromFile("src/test/resources/json/scraper/deleteReferenceResponseBodyById.json", String.class);

        testContext.verify(() -> {
            HttpResponse<String> response = null;
            try {
                response = Unirest.delete(requestURL + "/prom-manager/prometheus_scraper/" + scraperId)
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
