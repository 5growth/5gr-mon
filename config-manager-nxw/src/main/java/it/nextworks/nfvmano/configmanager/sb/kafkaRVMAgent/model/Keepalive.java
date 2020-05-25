package it.nextworks.nfvmano.configmanager.sb.kafkaRVMAgent.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.nextworks.nfvmano.configmanager.rvmagent.messages.ErrorMessage;

import java.util.ArrayList;


public class Keepalive extends KafkaGeneralResponse {
    @JsonProperty("status")
    private String status;
    @JsonProperty("errors_messages")
    private ArrayList<ErrorMessage> errorsMessages;

    public Keepalive() {
    }

    public Keepalive(String status, ArrayList<ErrorMessage> errorsMessages) {
        this.status = status;
        this.errorsMessages = errorsMessages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<ErrorMessage> getErrorsMessages() {
        return errorsMessages;
    }

    public void setErrorsMessages(ArrayList<ErrorMessage> errorsMessages) {
        this.errorsMessages = errorsMessages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Keepalive keepalive = (Keepalive) o;

        if (status != null ? !status.equals(keepalive.status) : keepalive.status != null) return false;
        return errorsMessages != null ? errorsMessages.equals(keepalive.errorsMessages) : keepalive.errorsMessages == null;
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (errorsMessages != null ? errorsMessages.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "keepalive{" +
                "status='" + status + '\'' +
                ", errors_messages=" + errorsMessages +
                '}';
    }
}
