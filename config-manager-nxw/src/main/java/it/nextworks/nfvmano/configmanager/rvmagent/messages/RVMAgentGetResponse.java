package it.nextworks.nfvmano.configmanager.rvmagent.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class RVMAgentGetResponse {


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
    @JsonProperty("errors")
    private ArrayList<ErrorMessage> errors;
    @JsonProperty("cloud_init_script")
    private String keepAliveReceived;
    @JsonProperty("status")
    private String status;

    public RVMAgentGetResponse() {
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

    public String getDaemonUser() {
        return daemonUser;
    }

    public void setDaemonUser(String daemonUser) {
        this.daemonUser = daemonUser;
    }

    public ArrayList<ErrorMessage> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<ErrorMessage> errors) {
        this.errors = errors;
    }

    public String getKeepAliveReceived() {
        return keepAliveReceived;
    }

    public void setKeepAliveReceived(String keepAliveReceived) {
        this.keepAliveReceived = keepAliveReceived;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RVMAgentGetResponse that = (RVMAgentGetResponse) o;

        if (rvmAgentId != null ? !rvmAgentId.equals(that.rvmAgentId) : that.rvmAgentId != null) return false;
        if (intallMethod != null ? !intallMethod.equals(that.intallMethod) : that.intallMethod != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (cloudInitScript != null ? !cloudInitScript.equals(that.cloudInitScript) : that.cloudInitScript != null)
            return false;
        if (daemonUser != null ? !daemonUser.equals(that.daemonUser) : that.daemonUser != null) return false;
        if (errors != null ? !errors.equals(that.errors) : that.errors != null) return false;
        if (keepAliveReceived != null ? !keepAliveReceived.equals(that.keepAliveReceived) : that.keepAliveReceived != null)
            return false;
        return status != null ? status.equals(that.status) : that.status == null;
    }

    @Override
    public int hashCode() {
        int result = rvmAgentId != null ? rvmAgentId.hashCode() : 0;
        result = 31 * result + (intallMethod != null ? intallMethod.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (cloudInitScript != null ? cloudInitScript.hashCode() : 0);
        result = 31 * result + (daemonUser != null ? daemonUser.hashCode() : 0);
        result = 31 * result + (errors != null ? errors.hashCode() : 0);
        result = 31 * result + (keepAliveReceived != null ? keepAliveReceived.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RVMAgentGetResponse{" +
                "rvmAgentId='" + rvmAgentId + '\'' +
                ", intallMethod='" + intallMethod + '\'' +
                ", description='" + description + '\'' +
                ", cloudInitScript='" + cloudInitScript + '\'' +
                ", daemonUser='" + daemonUser + '\'' +
                ", errors=" + errors +
                ", keepAliveReceived='" + keepAliveReceived + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}