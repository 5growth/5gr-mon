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

package com.telcaria.dashboards_manager.storage.entities;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "experiment")
@Data
public class Experiment {

  @Id
  private String expId;
  private String useCase;
  private String siteFacility;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn
  private List<Metric> metrics;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn
  private List<Kpi> kpis;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn
  private List<Log> logs;

  public void addLog(Log logEntity) {
    logs.add(logEntity);
    logEntity.setExperiment(this);
  }

  public void removeLog(Log logEntity) {
    logs.remove(logEntity);
    logEntity.setExperiment(null);
  }

  public void addKpi(Kpi kpi) {
    kpis.add(kpi);
    kpi.setExperiment(this);
  }

  public void removeKpi(Kpi kpi) {
    kpis.remove(kpi);
    kpi.setExperiment(null);
  }

  public void addMetric(Metric metric) {
    metrics.add(metric);
    metric.setExperiment(this);
  }

  public void removeMetric(Metric metric) {
    metrics.remove(metric);
    metric.setExperiment(null);
  }

}
