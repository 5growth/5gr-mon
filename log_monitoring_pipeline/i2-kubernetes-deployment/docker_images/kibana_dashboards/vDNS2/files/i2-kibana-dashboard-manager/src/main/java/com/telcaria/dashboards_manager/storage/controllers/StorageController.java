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

package com.telcaria.dashboards_manager.storage.controllers;


import com.telcaria.dashboards_manager.storage.service.StorageService;
import com.telcaria.dashboards_manager.storage.wrappers.ExperimentWrapper;
import com.telcaria.dashboards_manager.storage.wrappers.KpiWrapper;
import com.telcaria.dashboards_manager.storage.wrappers.MetricWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/storage")
public class StorageController {

  private StorageService storageService;

  @Autowired
  public StorageController(StorageService storageService) {
    this.storageService = storageService;
  }

  @GetMapping(value = "/experiment/{experimentId}")
  public @ResponseBody ExperimentWrapper getExperiment(@PathVariable String experimentId) {
    return storageService.experimentToWrapper(experimentId);
  }

  @GetMapping(value = "/kpi/{kpiId}")
  public @ResponseBody KpiWrapper getKpi(@PathVariable String kpiId) {
    return storageService.kpiToWrapper(kpiId);
  }

  @GetMapping(value = "/metric/{metricId}")
  public @ResponseBody MetricWrapper getMetric(@PathVariable String metricId) {
    return storageService.metricToWrapper(metricId);
  }

  @PostMapping(value = "/experiment")
  public @ResponseBody String createExperiment(@RequestBody ExperimentWrapper experimentWrapper) {
    storageService.createExperiment(experimentWrapper);
    return "OK";
  }

  @PostMapping(value = "/kpi")
  public @ResponseBody String createKpi(@RequestBody KpiWrapper kpiWrapper) {
    storageService.createKpi(kpiWrapper);
    return "OK";
  }

  @PostMapping(value = "/metric")
  public @ResponseBody String createMetric(@RequestBody MetricWrapper metricWrapper) {
    storageService.createMetric(metricWrapper);
    return "OK";
  }

  @DeleteMapping(value = "/experiment/{experimentId}")
  public @ResponseBody String deleteExperiment(@PathVariable String experimentId) {
    storageService.removeExperiment(experimentId);
    return "OK";
  }

  @DeleteMapping(value = "/kpi/{kpiId}")
  public @ResponseBody String deleteKpi(@PathVariable String kpiId) {
    storageService.removeKpi(kpiId);
    return "OK";
  }

  @DeleteMapping(value = "/metric/{metricId}")
  public @ResponseBody String deleteMetric(@PathVariable String metricId) {
    storageService.removeMetric(metricId);
    return "OK";
  }

}
