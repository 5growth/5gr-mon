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

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class CalcLabels {

    @JsonProperty("nsId")
    private String nsId;
    @JsonProperty("vnfdId")
    private String vnfdId;

    public CalcLabels() {

    }

    @JsonProperty("nsId")
    public String getNsId() {
        return nsId;
    }

    @JsonProperty("nsId")
    public void setNsId(String nsId) {
        this.nsId = nsId;
    }

    @JsonProperty("vnfdId")
    public String getVnfdId() {
        return vnfdId;
    }

    @JsonProperty("vnfdId")
    public void setVnfdId(String vnfdId) {
        this.vnfdId = vnfdId;
    }
}
