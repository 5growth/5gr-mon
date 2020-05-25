package it.nextworks.nfvmano.configmanager.prometheusMQAgent;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import it.nextworks.nfvmano.configmanager.prometheusScraper.model.PrometheusScraper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.vertx.core.json.Json.mapper;

public class PrometheusMQAgent extends Thread {
    private KafkaConsumer<String, String> consumer;
    private KafkaProducer<String, String> producer;
    private List<String> topics = new ArrayList<>();
    private AtomicBoolean shutdown;
    private CountDownLatch shutdownLatch;
    private Properties config;
    private Vertx vertx;
    private WebClient client;
    private static final Logger log = LoggerFactory.getLogger(PrometheusMQAgent.class);
    private int pushGatewayPort;
    private String pushGatewayAddress;
    private String pushGatewayTopic;
    private ConcurrentHashMap<String, HashMap<String, String>> concurrentHashMap;


    public PrometheusMQAgent(Properties config, Vertx vertx, ConcurrentHashMap<String, HashMap<String, String>> concurrentHashMap) {
        this.config = config;
        this.concurrentHashMap = concurrentHashMap;
        this.shutdown = new AtomicBoolean(false);
        this.shutdownLatch = new CountDownLatch(1);
        this.vertx = vertx;
        this.client = WebClient.create(vertx);
        this.pushGatewayPort = Integer.parseInt(this.config.getProperty("prometheus.PushGatewayPort"));
        this.pushGatewayAddress = this.config.getProperty("prometheus.PushGatewayAddress");
        this.pushGatewayTopic = this.config.getProperty("prometheus.PushGateway.topic");
        this.topics.add(this.pushGatewayTopic);
        Properties configForKafkaConsumer = (Properties) config.clone();
        configForKafkaConsumer.remove("prometheus.PushGatewayPort");
        configForKafkaConsumer.remove("prometheus.PushGatewayAddress");
        configForKafkaConsumer.remove("prometheus.PushGateway.topic");
        Properties configForKafkaProducer = (Properties) configForKafkaConsumer.clone();
        configForKafkaProducer.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        configForKafkaProducer.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        configForKafkaProducer.put("acks", "all");
        configForKafkaProducer.put("client.id", "MPScrapper");
        this.consumer = new KafkaConsumer<>(configForKafkaConsumer);
        this.producer = new KafkaProducer<String, String>(configForKafkaProducer);
    }



    public void sendToPrometheusGateway(ConsumerRecord<String, String> record) {
        log.debug("PrometheusMQAgent received message from kafka: " + record.value());
        JsonObject obj = new JsonObject(record.value());
        String key = obj.getString("key");
        Buffer buffer = Buffer.buffer(obj.getString("value"));
        client.post(this.pushGatewayPort, this.pushGatewayAddress, key)
                .sendBuffer(buffer, ar -> {
                    if (ar.failed()) {
                        log.error("Error during transmitting data to Prometheus GateWay: " + ar.cause().getMessage());
                        HttpResponse<Buffer> result = ar.result();
                    }
                });
        String[] keyArray  = key.split("/");
        String nsId = "";
        String vnfId = "";
        for(int i = 0; i < keyArray.length; i+=2){
            if (keyArray[i].equals("nsId")){
                nsId = keyArray[i+1];
            }
            if (keyArray[i].equals("vnfdId")){
                vnfId = keyArray[i+1];
            }
        }
        String mapKey = nsId + "_" + vnfId;
        HashMap<String, String> metricTopicMap =  concurrentHashMap.get(mapKey);
        if (metricTopicMap != null){
            scrapeAndPublishToKafka(nsId, vnfId, metricTopicMap);
        }
    }

    public void scrapeAndPublishToKafka(String nsId, String vnfId, HashMap<String, String> metricTopicMap) {
        for (Map.Entry<String, String> metricTopic : metricTopicMap.entrySet()) {
            String url = "/api/v1/query?query=" + metricTopic.getKey() + "{nsId=\"" +
                    nsId + "\",vnfdId=\"" + vnfId + "\"}";
            this.client.get(9090, "127.0.0.1", url)
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
                                    publishToKafka(metricTopic.getValue(), message);
                                }
                            } else {
                                log.error("Error. No data in Prometheus for: NSid: %s, VNFid %s, PerformanceMetric %s");
                            }

                        } else {
                            log.debug("PrometheusScraper can't get data from Prometheus");
                        }
                    });

        }
    }

    void publishToKafka( String kafkaTopic, JsonArray json) {
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(kafkaTopic,
                null, jsonString);
        Future<RecordMetadata> future = this.producer.send(record);
    }


    public void run() {
        try {
            consumer.subscribe(topics);
            while (!shutdown.get()) {
                ConsumerRecords<String, String> records = consumer.poll(500);
                records.forEach(record -> sendToPrometheusGateway(record));
            }
        } finally {
            consumer.close();
            shutdownLatch.countDown();
        }
    }

    public void shutdown() throws InterruptedException {
        shutdown.set(true);
        shutdownLatch.await();
    }
}