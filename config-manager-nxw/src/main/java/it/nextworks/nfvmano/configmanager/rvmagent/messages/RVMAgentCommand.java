package it.nextworks.nfvmano.configmanager.rvmagent.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.ext.web.api.validation.ValidationException;
import it.nextworks.nfvmano.configmanager.sb.kafkaRVMAgent.model.MessageToRVMAgent;
import it.nextworks.nfvmano.configmanager.utils.Validated;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class RVMAgentCommand implements Validated, MessageToRVMAgent {
    @JsonProperty("agent_id")
    private String agentId;
    @JsonProperty("command_id")
    private Integer commandId;
    private ArrayList<String> args;
    private Map<String, String> env;
    @JsonProperty("type_message")
    private String typeMessage;
    private String cwd;
    private ArrayList<String> body;
    @JsonProperty("object_type")
    private String objectType = "command";

    public RVMAgentCommand() {
    }

    public RVMAgentCommand(String agentId, Integer commandId, ArrayList<String> args, Map<String, String> env, String typeMessage, String cwd, ArrayList<String> body, String objectType) {
        this.agentId = agentId;
        this.commandId = commandId;
        this.args = args;
        this.env = env;
        this.typeMessage = typeMessage;
        this.cwd = cwd;
        this.body = body;
        this.objectType = objectType;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public void setArgs(ArrayList<String> args) {
        this.args = args;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public String getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(String typeMessage) {
        this.typeMessage = typeMessage;
    }

    public String getCwd() {
        return cwd;
    }

    public void setCwd(String cwd) {
        this.cwd = cwd;
    }

    public ArrayList<String> getBody() {
        return body;
    }

    public void setBody(ArrayList<String> body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RVMAgentCommand RVMAgentCommand = (RVMAgentCommand) o;

        if (agentId != null ? !agentId.equals(RVMAgentCommand.agentId) : RVMAgentCommand.agentId != null) return false;
        if (args != null ? !args.equals(RVMAgentCommand.args) : RVMAgentCommand.args != null) return false;
        if (env != null ? !env.equals(RVMAgentCommand.env) : RVMAgentCommand.env != null) return false;
        if (typeMessage != null ? !typeMessage.equals(RVMAgentCommand.typeMessage) : RVMAgentCommand.typeMessage != null) return false;
        if (cwd != null ? !cwd.equals(RVMAgentCommand.cwd) : RVMAgentCommand.cwd != null) return false;
        return body != null ? body.equals(RVMAgentCommand.body) : RVMAgentCommand.body == null;
    }

    @Override
    public int hashCode() {
        int result = agentId != null ? agentId.hashCode() : 0;
        result = 31 * result + (args != null ? args.hashCode() : 0);
        result = 31 * result + (env != null ? env.hashCode() : 0);
        result = 31 * result + (typeMessage != null ? typeMessage.hashCode() : 0);
        result = 31 * result + (cwd != null ? cwd.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Comamnd{" +
                "agentId='" + agentId + '\'' +
                ", args=" + args +
                ", env=" + env +
                ", typeMessage='" + typeMessage + '\'' +
                ", cwd='" + cwd + '\'' +
                ", body=" + body +
                '}';
    }

    public Optional<ValidationException> validate() {
        if (agentId == null) {
            return Optional.of(new ValidationException("ALERT: alertname cannot be null"));
        }
        if (typeMessage == null) {
            return Optional.of(new ValidationException("ALERT: query cannot be null"));
        }
        return Optional.empty();
    }

    public Integer getCommandId() {
        return commandId;
    }

    public void setCommandId(Integer commandId) {
        this.commandId = commandId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
}
