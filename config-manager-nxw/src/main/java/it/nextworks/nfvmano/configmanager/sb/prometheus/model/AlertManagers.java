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

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class AlertManagers {

    private List<StaticConfigs> staticConfigs;

    public AlertManagers() {

    }

    @JsonProperty("static_configs")
    public List<StaticConfigs> getStaticConfigs() {
        return staticConfigs;
    }

    @JsonProperty("static_configs")
    private void setStaticConfigs(List<StaticConfigs> staticConfigs) {
        this.staticConfigs = staticConfigs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlertManagers)) return false;
        AlertManagers that = (AlertManagers) o;
        return Objects.equals(getStaticConfigs(), that.getStaticConfigs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStaticConfigs());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AlertManagers.class.getSimpleName() + "[", "]")
                .add("staticConfigs=" + staticConfigs)
                .toString();
    }
}
