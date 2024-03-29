/*
* Copyright 2018 Nextworks s.r.l.
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

package it.nextworks.nfvmano.configmanager.sb.prometheus.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class CalculateRules {

    @JsonProperty("groups")
    private List<CalcGroups> groups = new ArrayList<>();

    public CalculateRules() {

    }

    @JsonProperty("groups")
    public List<CalcGroups> getGroups() {
        return groups;
    }

    @JsonProperty("groups")
    public void setGroups(List<CalcGroups> groups) {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        this.groups = groups;
    }

}
