package it.nextworks.nfvmano.configmanager.sb.kafkaRVMAgent.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KafkaRVMAgentCommandResponse extends KafkaGeneralResponse {
    @JsonProperty("command_id")
    private String commandId;
    @JsonProperty("agent_id")
    private String agentId;
    @JsonProperty("errs")
    private String errs;
    @JsonProperty("outs")
    private String outs;
    @JsonProperty("returncode")
    private String returncode;

    public KafkaRVMAgentCommandResponse() {
    }

    public KafkaRVMAgentCommandResponse(String commandId, String agentId, String errs, String outs, String returncode) {
        this.commandId = commandId;
        this.agentId = agentId;
        this.errs = errs;
        this.outs = outs;
        this.returncode = returncode;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getErrs() {
        return errs;
    }

    public void setErrs(String errs) {
        this.errs = errs;
    }

    public String getOuts() {
        return outs;
    }

    public void setOuts(String outs) {
        this.outs = outs;
    }

    public String getReturncode() {
        return returncode;
    }

    public void setReturncode(String returncode) {
        this.returncode = returncode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KafkaRVMAgentCommandResponse that = (KafkaRVMAgentCommandResponse) o;

        if (commandId != null ? !commandId.equals(that.commandId) : that.commandId != null) return false;
        if (agentId != null ? !agentId.equals(that.agentId) : that.agentId != null) return false;
        if (errs != null ? !errs.equals(that.errs) : that.errs != null) return false;
        if (outs != null ? !outs.equals(that.outs) : that.outs != null) return false;
        return returncode != null ? returncode.equals(that.returncode) : that.returncode == null;
    }

    @Override
    public int hashCode() {
        int result = commandId != null ? commandId.hashCode() : 0;
        result = 31 * result + (agentId != null ? agentId.hashCode() : 0);
        result = 31 * result + (errs != null ? errs.hashCode() : 0);
        result = 31 * result + (outs != null ? outs.hashCode() : 0);
        result = 31 * result + (returncode != null ? returncode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RVMAgentCommandResponse{" +
                "commandId='" + commandId + '\'' +
                ", agentId='" + agentId + '\'' +
                ", errs='" + errs + '\'' +
                ", outs='" + outs + '\'' +
                ", returncode='" + returncode + '\'' +
                '}';
    }
}
