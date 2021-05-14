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


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@Slf4j
@Transactional
public class KibanaConnectorServiceImpl implements KibanaConnectorService {

    @Autowired
    private KibanaProperties kibanaProperties;

    public boolean postKibanaObject(String kibanaJsonObject) {

        WebClient client = WebClient.create(kibanaProperties.getBaseUrl());

        log.debug("Kibana object:\n{}", kibanaJsonObject);
        log.debug("Requesting new object to Kibana");
        try {
            String response = client.post()
                    .uri("/api/kibana/dashboards/import")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("kbn-xsrf", "true")
                    .bodyValue(kibanaJsonObject)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("Object successfully created. Response = " + response);
            return true;
        } catch (WebClientResponseException e) {
            log.info("error and no send request");
                log.error("Error while trying to create in Kibana. " + e.getResponseBodyAsString());
                return false;
        }
    }

    public boolean putKibanaIndexPattern(String indexPattern) {

        WebClient client = WebClient.create(kibanaProperties.getBaseUrl());

        log.debug("Requesting new object to Kibana");
        try {
            int tries = 0;
            String indexPatternFields;
            do {
                log.debug("Requesting new object to Kibana. Try n {}", tries);
                indexPatternFields = getIndexPatternFields(indexPattern);
                tries ++;
                if (indexPatternFields == null) {
                    Thread.sleep(5000);
                }
            } while (indexPatternFields == null && tries < 3);

            assert indexPatternFields != null;
            indexPatternFields = indexPatternFields.substring(10, indexPatternFields.length()-1).replaceAll("\"", "\\\\\"");

            String response = client.put()
                    .uri("/api/saved_objects/index-pattern/index_" + indexPattern)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("kbn-xsrf", "true")
                    .bodyValue("{\"attributes\":{\"title\":\"" + indexPattern + "\",\"fields\":\"" + indexPatternFields + "\"}}")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("Object successfully created. Response = " + response);
            return true;
        } catch (WebClientResponseException | InterruptedException e) {
            log.error("Error while trying to create in Kibana. " + e);
            return false;
        }
    }

    public String getIndexPatternFields(String indexPattern){

        WebClient client = WebClient.create(kibanaProperties.getBaseUrl());

        //http://10.9.8.202:5601/api/index_patterns/_fields_for_wildcard?pattern=uc.4.france_nice.infrastructure_metric.expb_metricid&meta_fields=_source&meta_fields=_id&meta_fields=_type&meta_fields=_index&meta_fields=_score
        log.debug("getIndexPatternFields {}", indexPattern);
        try {
            String response = client.get()
                    .uri("/api/index_patterns/_fields_for_wildcard?pattern=" + indexPattern + "&meta_fields=_source&meta_fields=_id&meta_fields=_type&meta_fields=_index&meta_fields=_score")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("kbn-xsrf", "true")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            assert response.contains("fields");
            log.debug("getIndexPatternFields. Response = " + response);
            return response;
        } catch (WebClientResponseException e) {
            log.error("Error while getIndexPatternFields " + e.getResponseBodyAsString());
            return null;
        }

    }

    public boolean setOwner(String dashboardId, String user) {
        //The user can be the userId, the username or the email depending on the configuration given to the kibana keycloak plugin
        WebClient client = WebClient.create(kibanaProperties.getBaseUrl());

        log.debug("setOwner {} {}", dashboardId, user);
        try {
            String response = client.put()
                    .uri("/api/saved_objects/dashboard/" + dashboardId + "/permissions/view")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("kbn-xsrf", "true")
                    .bodyValue("{\"users\": " + getStringUsers(user) + "}")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("setOwner successfully. Response = " + response);
            return true;
        } catch (WebClientResponseException e) {
            log.error("Error while trying to setOwner in Kibana. " + e.getResponseBodyAsString());
            return false;
        }
    }

    private String getStringUsers(String users){
        String[] usersArray = users.split(",");
        String generatedString = "[";

        for(int i = 0; i < usersArray.length; i++){
            generatedString = generatedString.concat("\"" + usersArray[i] + "\"");
            if (i < usersArray.length - 1){
                generatedString = generatedString.concat(",");
            }
        }
        generatedString = generatedString.concat("]");

        return  generatedString;
    }

    public boolean removeKibanaObject(String kibanaObjectId, String kibanaObjectType) {

        WebClient client = WebClient.create(kibanaProperties.getBaseUrl());

        log.debug("Kibana object:\n{}", kibanaObjectId);
        log.debug("Removing new object to Kibana");
        try {
            String response = client.delete()
                    .uri("/api/saved_objects/" + kibanaObjectType + "/" + kibanaObjectId)
                    .header("kbn-xsrf", "true")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.debug("Object successfully Kibana. " + response);
            return true;
        } catch (WebClientResponseException e) {
            log.error("Error while trying to remove in Kibana. " + e.getResponseBodyAsString());
            return false;
        }

    }
}
