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

import com.telcaria.dashboards_manager.nbi.wrapper.LogScrapperRequestWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.LogScrapperResponseWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.UrlWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.ValueWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.elasticsearch.ElasticsearchDashboardsManagerResponseWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.elasticsearch.ElasticsearchParametersWrapper;
import net.minidev.json.JSONObject;

public interface DashboardsManagerService {

  JSONObject createDashboardRequest(ValueWrapper valueWrapper);

  UrlWrapper getDashboardRequest(String experimentId);

  String deleteDashboardRequest(String dashboardId);

  LogScrapperResponseWrapper createScrapper(LogScrapperRequestWrapper ScrapperRequestWrapper);

  JSONObject deleteScrapper(String scraperId);

  void disableKibana();
}
