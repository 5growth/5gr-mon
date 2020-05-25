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

package it.nextworks.nfvmano.configmanager.rvmagent.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RVMAgentCreateResponse {


    @JsonProperty("agent_id")
    private String rvmAgentId = null;
    @JsonProperty("install_method")
    private String intallMethod = null;
    @JsonProperty("description")
    private String description = null;
    @JsonProperty("cloud_init_script")
    private String cloudInitScript;
    @JsonProperty("daemon_user")
    private String daemonUser;


    @Override
    public String toString() {
        return "RVMAgentResponse{" +
                "rvmAgentId='" + rvmAgentId + '\'' +
                ", intallMethod='" + intallMethod + '\'' +
                ", description='" + description + '\'' +
                ", cloudInitScript='" + cloudInitScript + '\'' +
                ", daemonUser='" + daemonUser + '\'' +
                '}';
    }

    public String getDaemonUser() {
        return daemonUser;
    }

    public void setDaemonUser(String daemonUser) {
        this.daemonUser = daemonUser;
    }



    public RVMAgentCreateResponse() {
    }

    public RVMAgentCreateResponse(String rvmAgentId, String intallMethod, String description, String cloudInitScript, String daemonUser) {
        this.rvmAgentId = rvmAgentId;
        this.intallMethod = intallMethod;
        this.description = description;
        this.cloudInitScript = cloudInitScript;
        this.daemonUser = daemonUser;
    }

    public String getRvmAgentId() {
        return rvmAgentId;
    }

    public void setRvmAgentId(String rvmAgentId) {
        this.rvmAgentId = rvmAgentId;
    }

    public String getIntallMethod() {
        return intallMethod;
    }

    public void setIntallMethod(String intallMethod) {
        this.intallMethod = intallMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCloudInitScript() {
        return cloudInitScript;
    }

    public void setCloudInitScript(String cloudInitScript) {
        this.cloudInitScript = cloudInitScript;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RVMAgentCreateResponse that = (RVMAgentCreateResponse) o;

        if (rvmAgentId != null ? !rvmAgentId.equals(that.rvmAgentId) : that.rvmAgentId != null) return false;
        if (intallMethod != null ? !intallMethod.equals(that.intallMethod) : that.intallMethod != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (cloudInitScript != null ? !cloudInitScript.equals(that.cloudInitScript) : that.cloudInitScript != null)
            return false;
        return daemonUser != null ? daemonUser.equals(that.daemonUser) : that.daemonUser == null;
    }

    @Override
    public int hashCode() {
        int result = rvmAgentId != null ? rvmAgentId.hashCode() : 0;
        result = 31 * result + (intallMethod != null ? intallMethod.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (cloudInitScript != null ? cloudInitScript.hashCode() : 0);
        result = 31 * result + (daemonUser != null ? daemonUser.hashCode() : 0);
        return result;
    }
}