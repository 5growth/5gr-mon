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

package com.telcaria.dashboards_manager.elasticsearch.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HitWrapper {

    @JsonProperty(value = "_index")
    private String index;
    @JsonProperty(value = "_type")
    private String type;
    @JsonProperty(value = "_id")
    private String id;
    @JsonProperty(value = "_score")
    private float score;
    @JsonProperty(value = "_source")
    private SourceWrapper source;
}
