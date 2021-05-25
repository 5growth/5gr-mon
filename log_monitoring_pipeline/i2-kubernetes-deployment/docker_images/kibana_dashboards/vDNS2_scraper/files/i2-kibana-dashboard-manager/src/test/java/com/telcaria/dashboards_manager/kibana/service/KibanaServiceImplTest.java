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

package com.telcaria.dashboards_manager.kibana.service;

import com.telcaria.dashboards_manager.storage.entities.Log;
import com.telcaria.dashboards_manager.storage.enums.GraphType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class KibanaServiceImplTest {

    @Autowired
    KibanaService kibanaService;

    @Test
    void createKibanaDashboard() {

        Log log1 = new Log();
        log1.setTopic("15-abr-1");
        log1.setGraph(GraphType.PIE);
        log1.setDashboardId("84c757a4-785f-482e-b920-cba0edaa7584");
        log1.setDashboardTitle("titulo");
        log1.setDashboardType("tipo");

        String url1 = kibanaService.createKibanaDashboard(log1);
        log.info(url1);
    }

    @Test
    void deleteKibanaDashboard() {

        Log log1 = new Log();
        log1.setTopic("15-abr-1");
        log1.setGraph(GraphType.PIE);
        log1.setDashboardId("84c757a4-785f-482e-b920-cba0edaa7584");
        log1.setDashboardTitle("titulo");
        log1.setDashboardType("tipo");

        boolean deleted = kibanaService.deleteKibanaDashboard(log1);
        log.info("{}", deleted);
    }
}