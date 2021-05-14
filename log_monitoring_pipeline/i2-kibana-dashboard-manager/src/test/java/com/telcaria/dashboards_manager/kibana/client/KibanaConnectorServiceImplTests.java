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

package com.telcaria.dashboards_manager.kibana.client;

import com.telcaria.dashboards_manager.kibana.client.KibanaConnectorService;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class KibanaConnectorServiceImplTests {

    @Autowired
    KibanaConnectorService kibanaConnectorService;

    @Test
    void postKibanaIndexPattern(){
        kibanaConnectorService.putKibanaIndexPattern("uc.4.france_nice.infrastructure_metric.expb_metricid");
    }

    @Test
    @Order(1)
    void postKibanaObject(){
        String kibanaObject = "{\n" +
                "  \"objects\": [\n" +
                "    {\n" +
                "      \"id\": \"count-visualization\",\n" +
                "      \"type\": \"visualization\",\n" +
                "      \"version\": 1,\n" +
                "      \"attributes\": {\n" +
                "        \"title\": \"Count Visualization\",\n" +
                "        \"uiStateJSON\": \"{}\",\n" +
                "        \"description\": \"\",\n" +
                "        \"version\": 1,\n" +
                "        \"kibanaSavedObjectMeta\": {\n" +
                "          \"searchSourceJSON\": \"{\\\"index\\\":\\\"logstash-*\\\",\\\"query\\\":{\\\"query\\\":\\\"\\\",\\\"language\\\":\\\"lucene\\\"},\\\"filter\\\":[]}\"\n" +
                "        },\n" +
                "        \"visState\": \"{\\\"title\\\":\\\"Count Visualization\\\",\\\"type\\\":\\\"metric\\\",\\\"params\\\":{\\\"addTooltip\\\":true,\\\"addLegend\\\":false,\\\"type\\\":\\\"metric\\\",\\\"metric\\\":{\\\"percentageMode\\\":false,\\\"useRanges\\\":false,\\\"colorSchema\\\":\\\"Green to Red\\\",\\\"metricColorMode\\\":\\\"None\\\",\\\"colorsRange\\\":[{\\\"from\\\":0,\\\"to\\\":10000}],\\\"labels\\\":{\\\"show\\\":true},\\\"invertColors\\\":false,\\\"style\\\":{\\\"bgFill\\\":\\\"#000\\\",\\\"bgColor\\\":false,\\\"labelColor\\\":false,\\\"subText\\\":\\\"\\\",\\\"fontSize\\\":60}}},\\\"aggs\\\":[{\\\"id\\\":\\\"1\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"count\\\",\\\"schema\\\":\\\"metric\\\",\\\"params\\\":{}}]}\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";
        boolean ok = kibanaConnectorService.postKibanaObject(kibanaObject);
        assertTrue(ok);
    }

    @Test
    @Order(2)

    void  giveDashboardPermissionToUser(){
        kibanaConnectorService.setOwner("74c757a4-785f-482e-b920-cba0edaa7584", "user1");
    }

    @Test
    @Order(3)
    void deleteKibanaObject1(){
        String kibanaObjectid = "count-visualization";
        String kibanaObjectType = "visualization";
        boolean ok = kibanaConnectorService.removeKibanaObject(kibanaObjectid, kibanaObjectType);
        assertTrue(ok);
    }

    @Test
    @Order(4)
    void deleteKibanaObject2(){
        String kibanaObjectid = "e958415e-851c-4407-8b5c-8e4560380adb";
        String kibanaObjectType = "dashboard";
        boolean ok = kibanaConnectorService.removeKibanaObject(kibanaObjectid, kibanaObjectType);
        assertTrue(ok);
    }




}
