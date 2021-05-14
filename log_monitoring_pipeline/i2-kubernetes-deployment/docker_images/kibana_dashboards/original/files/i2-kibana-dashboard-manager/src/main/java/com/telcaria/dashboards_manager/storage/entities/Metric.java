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

import com.telcaria.dashboards_manager.storage.enums.GraphType;
import com.telcaria.dashboards_manager.storage.enums.MetricType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "metric")
@Data
public class Metric {

  @Id
  private String topic;
  private String metricId;
  @Enumerated(EnumType.STRING)
  private MetricType type;
  private String name;
  private String metricCollectionType;
  private String unit;
  double interval;
  @Enumerated(EnumType.STRING)
  private GraphType graph;
  private String dashboardUrl="";
  private String dashboardId;
  private String kibanaUser;
  @ManyToOne
  private Experiment experiment;
}
