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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class CalcRules {

    @JsonProperty("expr")
    private String expr;
    @JsonProperty("record")
    private String record;

    @JsonProperty("labels")
    private CalcLabels labels;


    public CalcRules() {

    }

    public CalcRules(String expr, String record) {
        this.expr = expr;
        this.record = record;
    }

    @JsonProperty("expr")
    public String getExpr() {
        return expr;
    }

    @JsonProperty("expr")
    public void setExpr(String expr) {
        this.expr = expr;
    }

    @JsonProperty("record")
    public String getRecord() {
        return record;
    }

    @JsonProperty("record")
    public void setRecord(String record) {
        this.record = record;
    }

    @JsonProperty("labels")
    public CalcLabels getLabels() {
        return labels;
    }

    @JsonProperty("labels")
    public void setLabels(CalcLabels labels) {
        this.labels = labels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalcRules calcRules = (CalcRules) o;
        return Objects.equals(expr, calcRules.expr) &&
                Objects.equals(record, calcRules.record) &&
                Objects.equals(labels, calcRules.labels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expr, record, labels);
    }
}
