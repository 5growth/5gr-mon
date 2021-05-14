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

package com.telcaria.dashboards_manager.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telcaria.dashboards_manager.elasticsearch.wrapper.ElasticsearchResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
class ElasticsearchTests {

  @Test
  void transformResponseInObject() {
    String response = "{\"took\":3,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":{\"value\":6,\"relation\":\"eq\"},\"max_score\":1.0,\"hits\":[{\"_index\":\"uc1.7.spain_5tonic.application_metric.tracking_response_tim\",\"_type\":\"_doc\",\"_id\":\"31CzYncBlNsJNWHaZbj4\",\"_score\":1.0,\"_source\":{\"device_id\":\"vnf-1\",\"@timestamp\":\"2021-02-02T12:24:20.097Z\",\"records\":{\"value\":{}},\"@version\":\"1\",\"unit\":\"random\",\"device_timestamp\":\"2021-02-02T12:24:20.087Z\",\"context\":\"value1=1\",\"metric_value\":1.2177218586010583,\"message_size\":158}},{\"_index\":\"uc1.7.spain_5tonic.application_metric.tracking_response_tim\",\"_type\":\"_doc\",\"_id\":\"41CzYncBlNsJNWHacbit\",\"_score\":1.0,\"_source\":{\"device_id\":\"vnf-1\",\"@timestamp\":\"2021-02-02T12:24:23.106Z\",\"records\":{\"value\":{}},\"@version\":\"1\",\"unit\":\"random\",\"device_timestamp\":\"2021-02-02T12:24:23.101Z\",\"context\":\"value1=1\",\"metric_value\":-0.4981616028485316,\"message_size\":159}}]}}";
    log.info("RESPONSE: {}", response);
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      ElasticsearchResponseWrapper data = objectMapper.readValue(response, ElasticsearchResponseWrapper.class);
      log.info("PARSED RESPONSE: {}", data.toString());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  @Test
  void transformResponseInObject2() {
    String response = "{\"took\":0,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":{\"value\":4000,\"relation\":\"eq\"},\"max_score\":1.0,\"hits\":[{\"_index\":\"multisite_usecase.5f651af4-704f-4b9b-a6ac-db217f600ca3.spain_5tonic.application_metric.requests_time_taken\",\"_type\":\"_doc\",\"_id\":\"xUDgYncBTeSL1Bvc6j8q\",\"_score\":1.0,\"_source\":{\"ecs\":{\"version\":\"1.5.0\"},\"context\":\"\",\"records\":{\"value\":{}},\"agent\":{\"ephemeral_id\":\"9b26dea6-0991-48f5-a79f-b266eaaa782f\",\"hostname\":\"nested-729fe2bd-d9e4-4655-909f-fffb07b51bdb-2-apache-client-vnf\",\"id\":\"2427a5fb-917e-4ef1-bb2b-c396ec1ba913\",\"version\":\"7.7.1\",\"type\":\"filebeat\"},\"device_id\":\"112233445566\",\"message_size\":797,\"@version\":\"1\",\"device_timestamp\":\"2021-02-02T13:13:35.000Z\",\"host\":{\"name\":\"nested-729fe2bd-d9e4-4655-909f-fffb07b51bdb-2-apache-client-vnf\"},\"unit\":\"ms\",\"metric_value\":18.0,\"log\":{\"offset\":1302,\"file\":{\"path\":\"/var/log/expb_metricsId.csv\"}},\"fields\":{\"topic_id\":\"multisite_usecase.5f651af4-704f-4b9b-a6ac-db217f600ca3.spain_5tonic.application_metric.requests_time_taken\"},\"@timestamp\":\"2021-02-02T13:14:00.860Z\"}},{\"_index\":\"multisite_usecase.5f651af4-704f-4b9b-a6ac-db217f600ca3.spain_5tonic.application_metric.requests_time_taken\",\"_type\":\"_doc\",\"_id\":\"xkDgYncBTeSL1Bvc6j8q\",\"_score\":1.0,\"_source\":{\"ecs\":{\"version\":\"1.5.0\"},\"context\":\"\",\"records\":{\"value\":{}},\"agent\":{\"ephemeral_id\":\"9b26dea6-0991-48f5-a79f-b266eaaa782f\",\"hostname\":\"nested-729fe2bd-d9e4-4655-909f-fffb07b51bdb-2-apache-client-vnf\",\"id\":\"2427a5fb-917e-4ef1-bb2b-c396ec1ba913\",\"version\":\"7.7.1\",\"type\":\"filebeat\"},\"device_id\":\"112233445566\",\"message_size\":797,\"@version\":\"1\",\"device_timestamp\":\"2021-02-02T13:13:36.000Z\",\"host\":{\"name\":\"nested-729fe2bd-d9e4-4655-909f-fffb07b51bdb-2-apache-client-vnf\"},\"unit\":\"ms\",\"metric_value\":18.0,\"log\":{\"offset\":1333,\"file\":{\"path\":\"/var/log/expb_metricsId.csv\"}},\"fields\":{\"topic_id\":\"multisite_usecase.5f651af4-704f-4b9b-a6ac-db217f600ca3.spain_5tonic.application_metric.requests_time_taken\"},\"@timestamp\":\"2021-02-02T13:14:00.861Z\"}}]}}";
    log.info("RESPONSE: {}", response);
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    try {
      ElasticsearchResponseWrapper data = objectMapper.readValue(response, ElasticsearchResponseWrapper.class);
      log.info("PARSED RESPONSE: {}", data.toString());
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
