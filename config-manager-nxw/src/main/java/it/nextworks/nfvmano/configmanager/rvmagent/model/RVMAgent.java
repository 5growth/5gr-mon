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

package it.nextworks.nfvmano.configmanager.rvmagent.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.ext.web.api.validation.ValidationException;
import it.nextworks.nfvmano.configmanager.render.CreateInitScript;
import it.nextworks.nfvmano.configmanager.rvmagent.messages.ErrorMessage;
import it.nextworks.nfvmano.configmanager.rvmagent.messages.RVMAgentCreateResponse;
import it.nextworks.nfvmano.configmanager.sb.kafkaRVMAgent.model.*;
import it.nextworks.nfvmano.configmanager.rvmagent.messages.RVMAgentCommand;
import it.nextworks.nfvmano.configmanager.utils.Validated;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class RVMAgent implements Validated, Runnable {
    @JsonProperty("agent_id")
    private String rvmAgentId = null;
    @JsonProperty("install_method")
    private String intallMethod = null;
    @JsonProperty("description")
    private String description = null;
    @JsonProperty("daemon_user")
    private String daemonUser = null;
    @JsonProperty("keep_alive_received")
    private String keepAliveReceived = "never";
    @JsonProperty("status")
    private String status = "Created";

    private static final Logger log = LoggerFactory.getLogger(RVMAgent.class);
    @JsonIgnore
    private String bootstrapServers;
    @JsonIgnore
    private Integer commandId = 0;
    @JsonIgnore
    private Map<String, RVMAgentCommand> commandMap = new HashMap<String, RVMAgentCommand>();
    @JsonIgnore
    private Map<String, KafkaRVMAgentCommandResponse> commandResultMap = new HashMap<String, KafkaRVMAgentCommandResponse>();
    @JsonIgnore
    private Map<String, PrometheusCollector> prometheusCollectorMap = new HashMap<String, PrometheusCollector>();;
    private long lastReceivedKeepaliveMessage = System.currentTimeMillis();
    @JsonIgnore
    private KafkaProducer kafkaProducer;
    @JsonIgnore
    private AtomicBoolean shutdown;
    @JsonIgnore
    private CountDownLatch shutdownLatch;
    @JsonIgnore
    private KafkaConsumer<String, String> kafkaConsumer;
    @JsonIgnore
    private List<String> topics = new ArrayList<>();
    @JsonIgnore
    private Integer agentTimeOut = 10000; //timeout 10 sec
    @JsonIgnore
    private ObjectMapper mapper;
    private ArrayList<ErrorMessage> errors;
    @JsonIgnore
    RVMAgentCreateResponse rvmAgentResponse = null;
    @JsonIgnore
    String rvmagentIdentifierMode = null;


    public void start_kafka_client(){

        Properties configConsumer = new Properties();
        Properties configProducer = new Properties();
        configConsumer.put("client.id", "server_" + getRvmAgentId());
        configConsumer.put("bootstrap.servers", bootstrapServers);
        configProducer.put("bootstrap.servers", bootstrapServers);
        configProducer.put("acks", "all");
        configConsumer.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        configConsumer.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        configProducer.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        configProducer.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        configConsumer.put("enable.auto.commit", "true");
        configProducer.put("client.id", "server_" + getRvmAgentId());
        configConsumer.put("group.id", "server_" + getRvmAgentId());
        topics.add(getBackwardTopic());
        this.kafkaProducer = new KafkaProducer<String, String>(configProducer);
        this.kafkaConsumer = new KafkaConsumer<>(configConsumer);
    }

    private String getBackwardTopic(){
        return rvmAgentId + "_backward";
    }

    private String getForwardTopic(){
        return rvmAgentId + "_forward";
    }

    public RVMAgent() {
        this.shutdown = new AtomicBoolean(false);
        this.shutdownLatch = new CountDownLatch(1);
        this.mapper = new ObjectMapper();
    }

    public Optional<ValidationException> validate() {
        if (intallMethod == null) {
            return Optional.of(new ValidationException("ALERT: alertname cannot be null"));
        }
        if (description == null) {
            return Optional.of(new ValidationException("ALERT: query cannot be null"));
        }
        return Optional.empty();
    }

    public RVMAgent(String rvmAgentId, String intallMethod, String description) {
        this.rvmAgentId = rvmAgentId;
        this.intallMethod = intallMethod;
        this.description = description;
        this.shutdown = new AtomicBoolean(false);
        this.shutdownLatch = new CountDownLatch(1);
        this.mapper = new ObjectMapper();
    }

    public String getRvmAgentId() {
        return rvmAgentId;
    }

    public void setRvmAgentId(String rvmAgentId) {
        this.rvmAgentId = rvmAgentId;
    }

    public String getIntallMethod() {
        return intallMethod;
    }

    public void setIntallMethod(String intallMethod) {
        this.intallMethod = intallMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void getCommandStatus(){

    }

    public PrometheusCollector addPrometheusCollector(PrometheusCollector prometheusCollector){
        prometheusCollectorMap.put(prometheusCollector.getCollectorId(), prometheusCollector);
        sendMessageToRVMAgent(prometheusCollector);
        return prometheusCollector;
    }

    public String delPrometheusCollector(String prometheusCollectorId){
        KafkaDeletePrometheusCollector kafkaDeletePrometheusCollector = new KafkaDeletePrometheusCollector();
        kafkaDeletePrometheusCollector.setRvmAgentId(rvmAgentId);
        kafkaDeletePrometheusCollector.setCollectorId(prometheusCollectorId);
        sendMessageToRVMAgent(kafkaDeletePrometheusCollector);
        prometheusCollectorMap.remove(prometheusCollectorId);
        return prometheusCollectorId;
    }

    public void send(){


    }

    public void analyzeMessage(ConsumerRecord<String, String> record) {
        log.debug("Received kafka message on topic {} with value: {}", record.topic(), record.value());
        if (!record.value().contains("keepalive")){
            log.info("Received kafka message on topic {} with value: {}", record.topic(), record.value());
        }

        try {
            KafkaGeneralResponse message =  mapper.readValue(record.value(), KafkaGeneralResponse.class);
            if (message instanceof Keepalive){
                this.lastReceivedKeepaliveMessage = System.currentTimeMillis();
                this.setKeepAliveReceived(true);
                this.setErrors((((Keepalive) message).getErrorsMessages()));
                if (((Keepalive) message).getStatus().equals("OK")){
                    if (!getStatus().equals("Ok")) {
                        setStatus("Ok");
                        log.debug(this.rvmAgentId + ": Received keepalive message with status OK");
                    }
                }else if (((Keepalive) message).getStatus().equals("Error")){
                    if (!getStatus().equals("Error")) {
                        setStatus("Error");
                        log.error(this.rvmAgentId + ": Received keepalive message with status Error");
                    }
                }
            }else if(message instanceof KafkaRVMAgentCommandResponse){
                log.debug("Received RVMAgentCommandResponse message from {}:\n", rvmAgentId, message.toString());
                KafkaRVMAgentCommandResponse kafkaRvmAgentCommandResponse = (KafkaRVMAgentCommandResponse)message;
                this.commandResultMap.put(kafkaRvmAgentCommandResponse.getCommandId(), kafkaRvmAgentCommandResponse);
            }else if(message instanceof KafkaAddPrometheusCollectorResponse){
                log.debug("Received AddPrometheusCollectorResponse message from {}:\n", rvmAgentId, message.toString());
            }else if(message instanceof KafkaDeletePrometheusCollectorResponse){
                log.debug("Received DeletePrometheusCollectorResponse message from {}:\n", rvmAgentId, message.toString());
            }
            else{
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();

        }


    }

    @Override
    public void run() {
        try {
            kafkaConsumer.subscribe(topics);
            while (!shutdown.get()) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(500);
                records.forEach(record -> analyzeMessage(record));
                if ((System.currentTimeMillis() - this.lastReceivedKeepaliveMessage) > agentTimeOut){
                    setStatus("Down");
                    log.debug("The server didn't receive a keep-alive message from the agent with id: {} during " +
                                    "timeout: {} ms. Last received message was {} ago.",
                            rvmAgentId, agentTimeOut, getKeepAliveReceived());
                }
            }
        } finally {
            kafkaConsumer.close();
            shutdownLatch.countDown();
        }
    }

    public void shutdown() throws InterruptedException {
        shutdown.set(true);
//        shutdownLatch.await();
    }

    public Integer getCommandId() {
        return commandId;
    }

    public Map<String, RVMAgentCommand> getCommandMap() {
        return commandMap;
    }

    public void setCommandMap(Map<String, RVMAgentCommand> commandMap) {
        this.commandMap = commandMap;
    }

    public Map<String, KafkaRVMAgentCommandResponse> getCommandResultMap() {
        return commandResultMap;
    }

    public void setCommandResultMap(Map<String, KafkaRVMAgentCommandResponse> commandResultMap) {
        this.commandResultMap = commandResultMap;
    }

    @JsonIgnore
    public String getBootstrapServers() {
        return bootstrapServers;
    }
    @JsonIgnore
    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    private void sendMessageToRVMAgent(MessageToRVMAgent messageToRVMAgent){
        String jsonString = null;
        try {
             jsonString = mapper.writeValueAsString(messageToRVMAgent);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.info("Message will be send to topic: {} : {}", getForwardTopic(), jsonString);
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(getForwardTopic(), null, jsonString);
        Future<RecordMetadata> future = kafkaProducer.send(record);

    }

    public RVMAgentCommand executeCommand(RVMAgentCommand rvmAgentCommand){
        commandId ++;
        rvmAgentCommand.setCommandId(commandId);
        commandMap.put(commandId.toString(), rvmAgentCommand);
        sendMessageToRVMAgent(rvmAgentCommand);
        return rvmAgentCommand;
    }

    public Map<String, PrometheusCollector> getPrometheusCollectorMap() {
        return prometheusCollectorMap;
    }

    public void setPrometheusCollectorMap(Map<String, PrometheusCollector> prometheusCollectorMap) {
        this.prometheusCollectorMap = prometheusCollectorMap;
    }


    @JsonIgnore
    public RVMAgentCreateResponse getRVMAgentCreateResponse() {
        if (this.rvmAgentResponse == null){
            this.rvmAgentResponse = new RVMAgentCreateResponse();
            CreateInitScript initScript = new CreateInitScript(rvmAgentId, daemonUser);
            initScript.setRvmagentIdentifierMode(this.rvmagentIdentifierMode);
            this.rvmAgentResponse.setRvmAgentId(rvmAgentId);
            this.rvmAgentResponse.setIntallMethod(intallMethod);
            this.rvmAgentResponse.setDescription(description);
            this.rvmAgentResponse.setDaemonUser(daemonUser);
            this.rvmAgentResponse.setCloudInitScript(initScript.generate_scripts());}
        return rvmAgentResponse;
    }

    public String getKeepAliveReceived() {
        if (keepAliveReceived.equals("never")){
            return keepAliveReceived;}
        else{
            Long millis = System.currentTimeMillis() - this.lastReceivedKeepaliveMessage;
            String stringPeriod =  DurationFormatUtils.formatDuration(millis, "dd 'days' HH:mm:ss", true);
            return stringPeriod;
        }
    }

    public void setKeepAliveReceived(String keepAliveReceived) {
        this.keepAliveReceived = keepAliveReceived;
    }

    public void setKeepAliveReceived(Boolean keepAliveReceived) {
        this.keepAliveReceived = "received";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!this.status.equals(status)){
            log.info("RVM agent with id {} changed status from {} to {}", rvmAgentId, this.status, status);
        }
        this.status = status;
    }

    public void setErrors(ArrayList<ErrorMessage> errors) {
        this.errors = errors;
    }

    public ArrayList<ErrorMessage> getErrors() {
        return errors;
    }

    public String getRvmagentIdentifierMode() {
        return rvmagentIdentifierMode;
    }

    public void setRvmagentIdentifierMode(String rvmagentIdentifierMode) {
        this.rvmagentIdentifierMode = rvmagentIdentifierMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RVMAgent rvmAgent = (RVMAgent) o;

        if (lastReceivedKeepaliveMessage != rvmAgent.lastReceivedKeepaliveMessage) return false;
        if (rvmAgentId != null ? !rvmAgentId.equals(rvmAgent.rvmAgentId) : rvmAgent.rvmAgentId != null) return false;
        if (intallMethod != null ? !intallMethod.equals(rvmAgent.intallMethod) : rvmAgent.intallMethod != null)
            return false;
        if (description != null ? !description.equals(rvmAgent.description) : rvmAgent.description != null)
            return false;
        if (daemonUser != null ? !daemonUser.equals(rvmAgent.daemonUser) : rvmAgent.daemonUser != null) return false;
        if (keepAliveReceived != null ? !keepAliveReceived.equals(rvmAgent.keepAliveReceived) : rvmAgent.keepAliveReceived != null)
            return false;
        if (status != null ? !status.equals(rvmAgent.status) : rvmAgent.status != null) return false;
        if (bootstrapServers != null ? !bootstrapServers.equals(rvmAgent.bootstrapServers) : rvmAgent.bootstrapServers != null)
            return false;
        if (commandId != null ? !commandId.equals(rvmAgent.commandId) : rvmAgent.commandId != null) return false;
        if (commandMap != null ? !commandMap.equals(rvmAgent.commandMap) : rvmAgent.commandMap != null) return false;
        if (commandResultMap != null ? !commandResultMap.equals(rvmAgent.commandResultMap) : rvmAgent.commandResultMap != null)
            return false;
        if (prometheusCollectorMap != null ? !prometheusCollectorMap.equals(rvmAgent.prometheusCollectorMap) : rvmAgent.prometheusCollectorMap != null)
            return false;
        if (kafkaProducer != null ? !kafkaProducer.equals(rvmAgent.kafkaProducer) : rvmAgent.kafkaProducer != null)
            return false;
        if (shutdown != null ? !shutdown.equals(rvmAgent.shutdown) : rvmAgent.shutdown != null) return false;
        if (shutdownLatch != null ? !shutdownLatch.equals(rvmAgent.shutdownLatch) : rvmAgent.shutdownLatch != null)
            return false;
        if (kafkaConsumer != null ? !kafkaConsumer.equals(rvmAgent.kafkaConsumer) : rvmAgent.kafkaConsumer != null)
            return false;
        if (topics != null ? !topics.equals(rvmAgent.topics) : rvmAgent.topics != null) return false;
        if (agentTimeOut != null ? !agentTimeOut.equals(rvmAgent.agentTimeOut) : rvmAgent.agentTimeOut != null)
            return false;
        if (mapper != null ? !mapper.equals(rvmAgent.mapper) : rvmAgent.mapper != null) return false;
        return errors != null ? errors.equals(rvmAgent.errors) : rvmAgent.errors == null;
    }

    @Override
    public int hashCode() {
        int result = rvmAgentId != null ? rvmAgentId.hashCode() : 0;
        result = 31 * result + (intallMethod != null ? intallMethod.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (daemonUser != null ? daemonUser.hashCode() : 0);
        result = 31 * result + (keepAliveReceived != null ? keepAliveReceived.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (bootstrapServers != null ? bootstrapServers.hashCode() : 0);
        result = 31 * result + (commandId != null ? commandId.hashCode() : 0);
        result = 31 * result + (commandMap != null ? commandMap.hashCode() : 0);
        result = 31 * result + (commandResultMap != null ? commandResultMap.hashCode() : 0);
        result = 31 * result + (prometheusCollectorMap != null ? prometheusCollectorMap.hashCode() : 0);
        result = 31 * result + (int) (lastReceivedKeepaliveMessage ^ (lastReceivedKeepaliveMessage >>> 32));
        result = 31 * result + (kafkaProducer != null ? kafkaProducer.hashCode() : 0);
        result = 31 * result + (shutdown != null ? shutdown.hashCode() : 0);
        result = 31 * result + (shutdownLatch != null ? shutdownLatch.hashCode() : 0);
        result = 31 * result + (kafkaConsumer != null ? kafkaConsumer.hashCode() : 0);
        result = 31 * result + (topics != null ? topics.hashCode() : 0);
        result = 31 * result + (agentTimeOut != null ? agentTimeOut.hashCode() : 0);
        result = 31 * result + (mapper != null ? mapper.hashCode() : 0);
        result = 31 * result + (errors != null ? errors.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RVMAgent{" +
                "rvmAgentId='" + rvmAgentId + '\'' +
                ", intallMethod='" + intallMethod + '\'' +
                ", description='" + description + '\'' +
                ", daemonUser='" + daemonUser + '\'' +
                ", keepAliveReceived='" + keepAliveReceived + '\'' +
                ", status='" + status + '\'' +
                ", bootstrapServers='" + bootstrapServers + '\'' +
                ", commandId=" + commandId +
                ", commandMap=" + commandMap +
                ", commandResultMap=" + commandResultMap +
                ", prometheusCollectorMap=" + prometheusCollectorMap +
                ", lastReceivedKeepaliveMessage=" + lastReceivedKeepaliveMessage +
                ", kafkaProducer=" + kafkaProducer +
                ", shutdown=" + shutdown +
                ", shutdownLatch=" + shutdownLatch +
                ", kafkaConsumer=" + kafkaConsumer +
                ", topics=" + topics +
                ", agentTimeOut=" + agentTimeOut +
                ", mapper=" + mapper +
                ", errors=" + errors +
                '}';
    }
}