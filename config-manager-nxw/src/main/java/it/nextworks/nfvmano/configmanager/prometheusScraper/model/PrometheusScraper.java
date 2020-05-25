/*
* Copyright 2018 Nextworks s.r.l.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package it.nextworks.nfvmano.configmanager.prometheusScraper.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import groovy.transform.Synchronized;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.validation.ValidationException;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import it.nextworks.nfvmano.configmanager.prometheusMQAgent.PrometheusMQAgent;
import it.nextworks.nfvmano.configmanager.rvmagent.model.RVMAgent;
import it.nextworks.nfvmano.configmanager.utils.Validated;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.Future;

import static io.vertx.core.json.Json.mapper;

public class PrometheusScraper extends TimerTask implements Validated {
    @JsonProperty("scraperId")
    private String scraperId = null;
    @JsonProperty("performanceMetric")
    private String performanceMetric = null;
    @JsonProperty("nsid")
    private String nsid = null;
    @JsonProperty("vnfid")
    private String vnfid = null;
    @JsonProperty("kafkaTopic")
    private String kafkaTopic = null;
    @JsonProperty("interval")
    private Integer interval = null;
    @JsonProperty("expression")
    private String expression = null;
    @JsonIgnore
    private WebClient client;
    @JsonIgnore
    private static final Logger log = LoggerFactory.getLogger(PrometheusMQAgent.class);
    @JsonIgnore
    private KafkaProducer kafkaProducer;
    @JsonIgnore
    private String promHost;
    @JsonIgnore
    private Integer promPort;


    public Optional<ValidationException> validate() {
        if (performanceMetric == null) {
            return Optional.of(new ValidationException("Scraper: performanceMetric cannot be null"));
        }
        if (nsid == null) {
            return Optional.of(new ValidationException("Scraper: nsid cannot be null"));
        }
        if (vnfid == null) {
            return Optional.of(new ValidationException("Scraper: vnfid cannot be null"));
        }
        return Optional.empty();
    }

    public PrometheusScraper() {
    }


    public PrometheusScraper(String scraperId, String performanceMetric, String nsid, String vnfid) {
        Vertx vertx = Vertx.vertx();
        this.client = WebClient.create(vertx);
        this.scraperId = scraperId;
        this.performanceMetric = performanceMetric;
        this.nsid = nsid;
        this.vnfid = vnfid;
    }

    public String getScraperId() {
        return scraperId;
    }

    public void setScraperId(String scraperId) {
        this.scraperId = scraperId;
    }

    public String getPerformanceMetric() {
        return performanceMetric;
    }

    public void setPerformanceMetric(String performanceMetric) {
        this.performanceMetric = performanceMetric;
    }

    public String getNsid() {
        return nsid;
    }

    public void setNsid(String nsid) {
        this.nsid = nsid;
    }

    public String getVnfid() {
        return vnfid;
    }

    public void setVnfid(String vnfid) {
        this.vnfid = vnfid;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrometheusScraper that = (PrometheusScraper) o;
        return Objects.equals(scraperId, that.scraperId) &&
                Objects.equals(performanceMetric, that.performanceMetric) &&
                Objects.equals(nsid, that.nsid) &&
                Objects.equals(vnfid, that.vnfid) &&
                Objects.equals(kafkaTopic, that.kafkaTopic) &&
                Objects.equals(interval, that.interval) &&
                Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scraperId, performanceMetric, nsid, vnfid, kafkaTopic, interval, expression);
    }

    @Override
    public String toString() {
        return "PrometheusScraper{" +
                "scraperId='" + scraperId + '\'' +
                ", performanceMetric='" + performanceMetric + '\'' +
                ", nsid='" + nsid + '\'' +
                ", vnfid='" + vnfid + '\'' +
                ", kafkaTopic='" + kafkaTopic + '\'' +
                ", interval=" + interval +
                ", expression='" + expression + '\'' +
                '}';
    }

    @JsonIgnore
    @Synchronized
    void publishToKafka( String kafkaTopic, JsonArray json) {
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(kafkaTopic,
                null, jsonString);
        Future<RecordMetadata> future = this.kafkaProducer.send(record);
    }

    @JsonIgnore
    @Override
    public void run() {
        String url = "/api/v1/query?query=" + this.performanceMetric + "{nsId=\"" +
                this.nsid + "\",vnfdId=\"" + this.vnfid + "\"}";
        this.client.get(this.promPort, this.promHost, url)
                .send(ar -> {
                    if (ar.failed()) {
                        log.error("Error during transmitting data to Prometheus: " + ar.cause().getMessage());
                        HttpResponse<Buffer> result = ar.result();
                    }
                    if (ar.succeeded()) {
                        HttpResponse<Buffer> response = ar.result();
                        String body = response.bodyAsString();
                        JsonObject json = new JsonObject(body);
                        json = json.getJsonObject("data");
                        JsonArray message = null;
                        if (json != null) {
                            message = json.getJsonArray("result");
                            if (message.isEmpty() == true) {
                                log.error("Error. No data in Prometheus for: NSid: %s, VNFid %s, PerformanceMetric %s");
                            } else {
                                JsonObject message1 = message.getJsonObject(0);
                                message1.put("type_message", "metric");
                                publishToKafka(this.kafkaTopic, message);
                            }
                        } else {
                            log.error("Error. No data in Prometheus for: NSid: %s, VNFid %s, PerformanceMetric %s");
                        }

                    } else {
                        log.debug("PrometheusScraper can't get data from Prometheus");
                    }
                });
    }

    public void setKafkaProducer(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void setPrometheus(String promHost, Integer promPort) {
        this.promHost = promHost;
        this.promPort = promPort;
    }

    public void setWebClient(WebClient webClient) {
        this.client = webClient;
    }
}

