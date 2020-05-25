package it.nextworks.nfvmano.configmanager.sb.kafkaRVMAgent.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = PROPERTY, property = "object_type")
@JsonTypeIdResolver(FieldTypeResolver.class)
public abstract class KafkaGeneralResponse {
    public KafkaGeneralResponse() {
    }
}
