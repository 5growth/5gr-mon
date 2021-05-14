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

package com.telcaria.dashboards_manager.storage.service;

import com.telcaria.dashboards_manager.storage.entities.Experiment;
import com.telcaria.dashboards_manager.storage.entities.Kpi;
import com.telcaria.dashboards_manager.storage.entities.Log;
import com.telcaria.dashboards_manager.storage.entities.Metric;
import com.telcaria.dashboards_manager.storage.wrappers.ExperimentWrapper;
import com.telcaria.dashboards_manager.storage.wrappers.KpiWrapper;
import com.telcaria.dashboards_manager.storage.wrappers.LogWrapper;
import com.telcaria.dashboards_manager.storage.wrappers.MetricWrapper;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface StorageService {

  /* - - - - - - - - - - - - - - - - - Experiment - - - - - - - - - - - - - - - - - */
  String createExperiment(ExperimentWrapper experimentWrapper);

  void removeExperiment(String experimentId);

  Optional<Experiment> getExperiment(String experimentId);

  ExperimentWrapper experimentToWrapper(String experimentId);

  Experiment findExperimentFromKpi(String topic);

  Experiment findExperimentFromMetric(String topic);

  Boolean isExperimentEmpty(String experimentId);

  /* - - - - - - - - - - - - - - - - - Kpi - - - - - - - - - - - - - - - - - */

  String createKpi(KpiWrapper kpiWrapper);

  void removeKpi(String kpiId);

  Optional<Kpi> getKpi(String kpiId);

  void addKpiToExperiment(Kpi kpi, String experimentId);

  void removeKpiFromExperiment(String kpiId, String experimentId);

  KpiWrapper kpiToWrapper(String kpiId);

  List<Kpi> findAllKpisFromExperiment(String experimentId);

  Optional<Kpi> getKpiFromExperimentAndTopic(String experimentId, String topic);

  void updateKpi(KpiWrapper kpiWrapper);

  /* - - - - - - - - - - - - - - - - - Log - - - - - - - - - - - - - - - - - */
  String createLog(LogWrapper logWrapper);

  Optional<Log> getLog(String logId);

  LogWrapper logToWrapper(String logId);

  void addLogToExperiment(Log logEntity, String experimentId);

  void updateLogWrapper(LogWrapper logWrapper);

  void removeLogFromExperiment(String metricId, String experimentId);

  void removeLog(String logId);

  Optional<Log> findLogFromDashboardID(String dashboardID);

  /* - - - - - - - - - - - - - - - - - Metric - - - - - - - - - - - - - - - - - */

  String createMetric(MetricWrapper metricWrapper);

  void removeMetric(String metricId);

  Optional<Metric> getMetric(String metricId);

  void addMetricToExperiment(Metric metric, String experimentId);

  void removeMetricFromExperiment(String metricId, String experimentId);

  MetricWrapper metricToWrapper(String metricId);

  List<Metric> findAllMetricsFromExperiment(String experimentId);

  Optional<Metric> getMetricFromExperimentAndTopic(String experimentId, String topic);

  void updateMetric(MetricWrapper metricWrapper);

}
