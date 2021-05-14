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

package com.telcaria.dashboards_manager.nbi.controller;


import com.telcaria.dashboards_manager.nbi.wrapper.DashboardsManagerRequestWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.UrlWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.ValueWrapper;
import com.telcaria.dashboards_manager.service.DashboardsManagerService;
import com.telcaria.dashboards_manager.nbi.wrapper.elasticsearch.ElasticsearchDashboardsManagerResponseWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.elasticsearch.ElasticsearchParametersWrapper;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@Slf4j
public class DashboardsManagerController {

  private DashboardsManagerService dashboardsManagerService;

  @Autowired
  public DashboardsManagerController(DashboardsManagerService dashboardsManagerService) {
    this.dashboardsManagerService = dashboardsManagerService;
  }

  @PostMapping(value = "/dashboard")
  public @ResponseBody ResponseEntity createDashboard(@RequestBody DashboardsManagerRequestWrapper dashboardsManagerRequestWrapper) {
    //TODO: manage exceptions?
    List<JSONObject> response = new ArrayList<JSONObject>();
    JSONObject node;
    for (ValueWrapper valueWrapper : dashboardsManagerRequestWrapper.getValue()) {
      log.info("Create dashboard for topic {}", valueWrapper.getTopic());
      node = dashboardsManagerService.createDashboardRequest(valueWrapper);
      response.add(node);
    }
    return new ResponseEntity(response.get(0), HttpStatus.ACCEPTED);
  }

  @GetMapping(value = "/dashboard/{nsdId}")
  public @ResponseBody
  UrlWrapper getDashboard(@PathVariable String nsdId) {
    //TODO: manage exceptions?
    log.info("Get dashboards for NS {}", nsdId);
    return dashboardsManagerService.getDashboardRequest(nsdId);
  }

  @DeleteMapping(value = "/dashboard")
  public @ResponseBody ResponseEntity deleteDashboard(@RequestBody JSONObject Id) {
    String dashboardId = Id.getAsString("dashboardId");
    dashboardsManagerService.deleteDashboardRequest(dashboardId);
    //TODO: manage exceptions?
    return new ResponseEntity(HttpStatus.ACCEPTED);
  }

  @GetMapping(value = "/dashboard/data/{nsdId}")
  public @ResponseBody
  ElasticsearchDashboardsManagerResponseWrapper getDataFromTopic(@PathVariable("nsdId") String nsdId,
                                                                 @RequestBody ElasticsearchParametersWrapper elasticsearchParametersWrapper) {
    //TODO: manage exceptions?
    log.info("Get data from for NS {}", nsdId);

    return dashboardsManagerService.getDataFromTopic(nsdId, elasticsearchParametersWrapper);
  }
}
