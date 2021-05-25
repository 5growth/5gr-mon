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
public class SourceWrapper {

    private String message;
    @JsonProperty(value = "@timestamp")
    private String timestamp;
    private EcsWrapper ecs;
    @JsonProperty(value = "@version")
    private String version;
    private AgentWrapper agent;
    private LogsWrapper log;
    private InputWrapper input;
    private HostWrapper host;
}
