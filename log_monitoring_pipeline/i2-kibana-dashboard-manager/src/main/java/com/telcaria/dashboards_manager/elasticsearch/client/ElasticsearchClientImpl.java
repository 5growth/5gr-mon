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

package com.telcaria.dashboards_manager.elasticsearch.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telcaria.dashboards_manager.elasticsearch.wrapper.ElasticsearchResponseWrapper;
import com.telcaria.dashboards_manager.nbi.wrapper.elasticsearch.ElasticsearchParametersWrapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Slf4j
@Transactional
public class ElasticsearchClientImpl implements ElasticsearchClient {

  private Environment env;

  @Autowired
  public ElasticsearchClientImpl(Environment env) {
    this.env = env;
  }

  @Override
  public ElasticsearchResponseWrapper searchData(String index, ElasticsearchParametersWrapper parameters) {

    ElasticsearchResponseWrapper data = new ElasticsearchResponseWrapper();

    OkHttpClient client = new OkHttpClient().newBuilder().build();
    Request request = new Request.Builder()
            .url(env.getProperty("elasticsearch.ip_port") + index + "/_search?" +
                         "source_content_type=application/json&source={\"from\":" +
                         parameters.getFrom() + ",\"size\":" +
                         parameters.getSize() + "}")
            .method("GET", null)
            .build();
    try {
      Response response = client.newCall(request).execute();

      if (response.isSuccessful()) {
        ResponseBody responseBody = response.body();
        String responseString = responseBody.string();
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        data = objectMapper.readValue(responseString, ElasticsearchResponseWrapper.class);
      } else {
        log.warn("Search data operation without successful result");
      }

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return data;
  }
}
