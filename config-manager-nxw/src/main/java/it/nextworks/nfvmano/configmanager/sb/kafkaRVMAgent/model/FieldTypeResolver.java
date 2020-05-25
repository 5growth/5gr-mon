package it.nextworks.nfvmano.configmanager.sb.kafkaRVMAgent.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

import java.io.IOException;

public class FieldTypeResolver extends TypeIdResolverBase {
    @Override
    public String idFromValue(Object o) {
        throw new RuntimeException("required for serialization only");
    }

    @Override
    public String idFromValueAndType(Object o, Class<?> aClass) {
        throw new RuntimeException("required for serialization only");
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        switch (id) {
            case "keepalive": {
                return context.getTypeFactory().constructType(new TypeReference<Keepalive>() {
                });
            }
            case "command_response": {
                return context.getTypeFactory().constructType(new TypeReference<KafkaRVMAgentCommandResponse>() {
                });
            }
            case "added_prometheus_collector": {
                return context.getTypeFactory().constructType(new TypeReference<KafkaAddPrometheusCollectorResponse>() {
                });
            }
            case "deleted_prometheus_collector": {
                return context.getTypeFactory().constructType(new TypeReference<KafkaDeletePrometheusCollectorResponse>() {
                });
            }
            default: {
                throw new IllegalArgumentException(id + " not known");
            }
        }
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
