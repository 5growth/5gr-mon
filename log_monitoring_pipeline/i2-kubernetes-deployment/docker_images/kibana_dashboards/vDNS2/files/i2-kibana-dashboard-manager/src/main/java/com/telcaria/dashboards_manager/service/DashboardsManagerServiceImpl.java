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

package com.telcaria.dashboards_manager.service;

import com.telcaria.dashboards_manager.kibana.service.KibanaService;
import com.telcaria.dashboards_manager.storage.enums.GraphType;
import com.telcaria.dashboards_manager.storage.service.StorageService;
import com.telcaria.dashboards_manager.elasticsearch.service.ElasticsearchService;
import com.telcaria.dashboards_manager.nbi.wrapper.UrlWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.ValueWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.elasticsearch.ElasticsearchDashboardsManagerResponseWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.elasticsearch.ElasticsearchParametersWrapper;
import com.telcaria.dashboards_manager.storage.entities.Experiment;
import com.telcaria.dashboards_manager.storage.entities.Log;
import com.telcaria.dashboards_manager.storage.wrappers.LogWrapper;
import com.telcaria.dashboards_manager.storage.wrappers.ExperimentWrapper;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;


import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class DashboardsManagerServiceImpl implements DashboardsManagerService {

  private StorageService storageService;
  private KibanaService kibanaService;
  private ElasticsearchService elasticsearchService;

  @Value("${kibana.enabled}")
  private boolean isKibanaEnabled = true;

  @Autowired
  public DashboardsManagerServiceImpl(StorageService storageService,
                                      KibanaService kibanaService,
                                      ElasticsearchService elasticsearchService) {
    this.storageService = storageService;
    this.kibanaService = kibanaService;
    this.elasticsearchService = elasticsearchService;
  }

  @Override
  public JSONObject createDashboardRequest(ValueWrapper valueWrapper) {
    // Create experiment in a synchronous way.
    try {
      waitFunction();
    } catch (Exception e) {
      log.error("Exception due to waitFunction for {}", valueWrapper);
    }
    return createDashboard(valueWrapper);
  }

  @Override
  public UrlWrapper getDashboardRequest(String experimentId) {
    Optional<Experiment> experimentOp = storageService.getExperiment(experimentId);
    UrlWrapper urlWrapper;
    if (experimentOp.isPresent()) {
      //Always just 1 logs per experiment
      Log logEntity = experimentOp.get().getLogs().get(0);
      urlWrapper = new UrlWrapper(logEntity.getDashboardUrl());
      return  urlWrapper;
    } else {
      urlWrapper = new UrlWrapper("Experiment Id does not exist");
      return  urlWrapper;
    }
  }

  @Override
  public String deleteDashboardRequest(String dashboardId) {
    Optional<Log> logOp = storageService.findLogFromDashboardID(dashboardId);
    if (logOp.isPresent()) {
      Log logEntity = logOp.get();
      String experimentId = logEntity.getTopic();
      if (storageService.getExperiment(experimentId).isPresent()) {
        // Remove Dashboard from Kibana
        if (isKibanaEnabled) {
          kibanaService.deleteKibanaDashboard(logEntity);
        }
        // Remove from database
        storageService.removeLogFromExperiment(logEntity.getTopic(), experimentId);
        storageService.removeLog(logEntity.getTopic());
      } else {
        log.error("Experiment Id does not exist");
        return "Experiment Id does not exist";
      }
    } else {
      log.error("{} does not exist", dashboardId);
      return dashboardId + " does not exist";
    }
    return dashboardId;
  }

  @Override
  public ElasticsearchDashboardsManagerResponseWrapper getDataFromTopic(String experimentId,
                                                                        ElasticsearchParametersWrapper elasticsearchParametersWrapper) {
    //TODO update the whole endpoint
    ElasticsearchDashboardsManagerResponseWrapper response = new ElasticsearchDashboardsManagerResponseWrapper();

    Optional<Log> logOP = storageService.getLog(experimentId);
    if (logOP.isPresent()){
      response = elasticsearchService.searchData(experimentId.toLowerCase(Locale.ROOT), elasticsearchParametersWrapper);
    } else {
      log.warn("Topic not present in DB {}", experimentId);
    }

    return response;
  }

  @Override
  public void disableKibana() {
    this.isKibanaEnabled = false;
  }

  private void waitFunction () throws InterruptedException {
    int upper = 1000;
    int lower = 1;
    int r = (int) (Math.random() * (upper - lower)) + lower;
    Thread.sleep(r);
  }

  private synchronized JSONObject createDashboard(ValueWrapper valueWrapper) {

    if (!storageService.getExperiment(valueWrapper.getNs_id()).isPresent()) {
      // Create Experiment Wrapper
      log.info("Creating experiment {} in DB", valueWrapper.getNs_id());
      ExperimentWrapper experimentWrapper = new ExperimentWrapper();
      experimentWrapper.setExpId(valueWrapper.getNs_id());

      // Persist Experiment
      try {
        storageService.createExperiment(experimentWrapper);
      }
      catch (Exception e) {
        log.warn("Trying to save experiment {} that is already in DB - concurrency problems", valueWrapper.getNs_id());
      }
    } else {
      log.info("Experiment {} is present in DB", valueWrapper.getNs_id());
    }

    String experimentId = valueWrapper.getNs_id();
    LogWrapper logWrapper = LogWrapperFromValueWrapper(valueWrapper);

    log.info("Creating LogWrapper {} in DB", logWrapper.getTopic());
    //Persist LogWrapper
    String logID = storageService.createLog(logWrapper);
    JSONObject node = new JSONObject();
    if (storageService.getLog(logID).isPresent()){
      Log logEntity = storageService.getLog(logID).get();
      storageService.addLogToExperiment(logEntity, experimentId);
      // Create Dashboard in Kibana
      if (isKibanaEnabled){
        String dashboardUrl = kibanaService.createKibanaDashboard(logEntity);
        if (dashboardUrl != null){
          log.info("Dashboard Visualization for log {} Created", logWrapper.getTopic());
          // Extract dashboard URL
          logWrapper.setDashboardUrl(dashboardUrl);
          // Update DB entities with Dashboard data
          storageService.updateLogWrapper(logWrapper);
          node.put("dashboardId", logWrapper.getDashboardId());
          node.put("url", logWrapper.getDashboardUrl());
          node.put("dashboardTitle", logWrapper.getDashboardTitle());
          node.put("ns_id", logWrapper.getTopic());
          node.put("dashboard_type", logWrapper.getDashboardType());
          return node;
        } else {
          log.error("Dashboard Visualization Error for {}", logWrapper.getTopic());
          node.put("error", "Dashboard Visualization Error for " + logWrapper.getTopic());
          return node;
        }
      } else {
        node.put("info", "Kibana is not enabled");
        return node;
      }
    } else {
      node.put("error", "Dashboard Visualization Error for " + logWrapper.getTopic());
      log.error("{} was not retrieved correctly", logWrapper.getTopic());
      return node;
    }
  }

  private LogWrapper LogWrapperFromValueWrapper(ValueWrapper valueWrapper) {
    LogWrapper LogWrapper = new LogWrapper();
    LogWrapper.setTopic(valueWrapper.getTopic());
    LogWrapper.setDashboardId(generateUUID());
    LogWrapper.setExpId(valueWrapper.getNs_id());
    // Although we set PIE type, it will be PIE + BAR.
    LogWrapper.setGraph(GraphType.PIE);
    LogWrapper.setDashboardTitle(valueWrapper.getDashboardTitle());
    LogWrapper.setDashboardType(valueWrapper.getDashboard_type());

    return LogWrapper;
  }

  private String generateUUID(){
    return UUID.randomUUID().toString();
  }
}
