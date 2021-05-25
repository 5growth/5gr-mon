/*
 * Copyright 2021-present Open Networking Foundation
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

package com.telcaria.dashboards_manager.elasticsearch.service;

import com.telcaria.dashboards_manager.elasticsearch.wrapper.HitWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.LogScrapperRequestWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.elasticsearch.ElasticsearchDataWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.elasticsearch.ElasticsearchDashboardsManagerResponseWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.elasticsearch.ElasticsearchParametersWrapper;
import com.telcaria.dashboards_manager.elasticsearch.client.ElasticsearchClient;
import com.telcaria.dashboards_manager.elasticsearch.wrapper.SourceWrapper;
import com.telcaria.dashboards_manager.elasticsearch.wrapper.ElasticsearchResponseWrapper;
import com.telcaria.dashboards_manager.storage.entities.Scrapper;
import com.telcaria.dashboards_manager.storage.service.StorageService;
import com.telcaria.dashboards_manager.storage.wrappers.ScrapperWrapper;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.connect.json.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
@Slf4j
@Transactional
public class ElasticsearchServiceImpl implements ElasticsearchService {

  private ElasticsearchClient elasticsearchClient;
  @Autowired
  private ThreadPoolTaskScheduler taskScheduler;
  private HashMap<String, ScheduledFuture> scheduledRunnableTasks;
  private StorageService storageService;
  private Environment env;
  Properties props;
  Producer<String, String> producer;

  // Locks for the scheduler
  private static Map<String, Lock> locksPerScraper = new ConcurrentHashMap<>();

  @Autowired
  public ElasticsearchServiceImpl(
          ElasticsearchClient elasticsearchClient,ThreadPoolTaskScheduler taskScheduler, StorageService storageService, Environment env) {
    this.elasticsearchClient = elasticsearchClient;
    this.taskScheduler = taskScheduler;
    this.storageService = storageService;
    this.env = env;

    scheduledRunnableTasks = new HashMap<String, ScheduledFuture>();
    props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("kafka.ip_port"));//IP of kafka Broker
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producer = new KafkaProducer<>(props);
  }

  @Override
  public String startLogScrapper(LogScrapperRequestWrapper scrapperRequestWrapper) {

    String scrapperId = generateUUID();

    if(!scheduledRunnableTasks.containsKey(scrapperId)) {
      storageService.createScrapper(scrapperRequestWrapper, scrapperId);
      try {
        //Get interval as integer
        int interval = Integer.parseInt(scrapperRequestWrapper.getInterval());
        interval = interval*1000; // In ms

        log.info("Creating LogScraper " + scrapperId + "for NS " + scrapperRequestWrapper.getNsid());
        // Schedule the scrapper every interval in ms.
        ScheduledFuture task = taskScheduler.scheduleWithFixedDelay(new LogScrapperTask(scrapperId), interval);

        // Include that runnable task in the HashMap.
        scheduledRunnableTasks.put(scrapperId, task);
      } catch (Exception e){
        log.error(e.toString());
        throw new RuntimeException("Interval must be a number");
      }
      return scrapperId;
    }
    else{
      log.error("LogScrapper with ID " + scrapperId + " already exists");
      throw new RuntimeException("LogScrapper with ID " + scrapperId + " already exists");
    }
  }

  private static Lock getLock(String scraperId) {
    if (!locksPerScraper.containsKey(scraperId)) {
      locksPerScraper.put(scraperId, new java.util.concurrent.locks.ReentrantLock());
    }
    return locksPerScraper.get(scraperId);
  }

  @Override
  public boolean deleteLogScrapper(String scrapperId){
    Optional<Scrapper> scrapperOp = storageService.getScrapper(scrapperId);
    if (scrapperOp.isPresent()) {
      Scrapper scrapper = scrapperOp.get();
      // Remove scheduled task.
      ScheduledFuture task = scheduledRunnableTasks.get(scrapperId);
      task.cancel(true);
      if (task.isDone()) {
        log.info("logScrapper " + scrapperId + " removed correctly");
        // And remove it in the HashMaps.
        scheduledRunnableTasks.remove(scrapperId);
        locksPerScraper.remove(scrapperId);
        storageService.removeScrapper(scrapperId);
        return true;
      } else {
        log.error("logScrapper " + scrapperId + " not removed correctly");
        return false;
      }
    }
    throw new RuntimeException("ScraperId does not exist");
  }

  public void publishKafka(String kafkaTopic, ElasticsearchResponseWrapper responseWrapper, String regex) {

    List<JSONObject> jsonList = new ArrayList<>();
    Pattern pattern = Pattern.compile(regex);

    for (HitWrapper hitWrapper : responseWrapper.getHits().getHits()) {

      SourceWrapper sourceWrapper = hitWrapper.getSource();
      Matcher matcher = pattern.matcher(sourceWrapper.getMessage());
      //log.info("Checking this hit: " + sourceWrapper.toString());

      if (matcher.matches()) {
        //log.info("This matches with the regex " + regex);
        JSONObject node = new JSONObject();
        node.put("message", sourceWrapper.getMessage());
        node.put("timestamp", sourceWrapper.getTimestamp());
        node.put("host", sourceWrapper.getAgent().getHostname());
        node.put("agent", sourceWrapper.getAgent().getId());
        node.put("log_path", sourceWrapper.getLog().getFile().getPath());//ToDo check escape chars at ".toString"
        jsonList.add(node);
      }
    }

    // Only publish if there is data that matches the regex.
    if (jsonList.size() > 0) {
      // Send data in a list.
      JSONObject rootNode = new JSONObject();
      rootNode.put("record", jsonList);

      ProducerRecord<String, String> record = new ProducerRecord<>(kafkaTopic, rootNode.toString());
      //Sending message to kafka Broker
      log.info("LogScraper sending data to topic: " + kafkaTopic + ". Data is the following: " + rootNode.toString());
      producer.send(record);
    }
  }

  private String generateUUID(){
    return UUID.randomUUID().toString();
  }

  @Transactional
  public class LogScrapperTask implements Runnable {

    private final String scrapperId;


    @Autowired
    public LogScrapperTask(String scrapperId) {
      this.scrapperId = scrapperId;
    }

    @Override
    //public synchronized void run() {
    public void run(){
      if(storageService.getScrapper(scrapperId).isPresent()) {
        //log.info("Running LogScraper " + scrapperId);

        // First of all, try to obtain the lock. If not, it will try it again in the next execution.
        Lock scraperLock = getLock(scrapperId);
        if (scraperLock != null) {
          if (!scraperLock.tryLock()) {
            //log.warn("Lock not adquired for scraperId: " + scrapperId + ". Trying in the next round");
          }
          else {
            //log.info("Lock adquired for scraperId " + scrapperId);

            Scrapper scrapper = storageService.getScrapper(scrapperId).get();
            int index = scrapper.getIndex();
            ElasticsearchParametersWrapper parameters = new ElasticsearchParametersWrapper();
            parameters.setFrom(index);
            parameters.setSize(5);

            //Get Info from elasticSearch
            ElasticsearchResponseWrapper response = elasticsearchClient.searchData(scrapper.getNsid().toLowerCase(), parameters);
            //Update DataBase with new index (index + numhits)
            int numhits = response.getHits().getHits().size();
            if (numhits > 0) {
              //log.info("Scraper " + scrapperId + " collecting data from index called " + scrapper.getNsid().toLowerCase()
              //                 + ". Numhits:" + numhits + "," + " current index:" + index);

              ScrapperWrapper scrapperWrapper = new ScrapperWrapper();
              scrapperWrapper.setScrapperId(scrapperId);
              scrapperWrapper.setIndex(index + numhits);
              storageService.updateScrapper(scrapperWrapper);

              //Transform hits to Json and publish it on Kafka
              publishKafka(scrapper.getKafkaTopic(), response, scrapper.getExpression());
            }

            // Releasing lock
            scraperLock.unlock();
            //log.info("Lock released for scraperId " + scrapperId);
          }
        }
      }
    }
  }
}
