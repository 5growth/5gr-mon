package it.nextworks.nfvmano.configmanager.sb.prometheusPushGateway;

import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.prometheusPushGateway.PrometheusPushGatewayRepo;
import it.nextworks.nfvmano.configmanager.sb.prometheusPushGateway.model.MetricsObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static it.nextworks.nfvmano.configmanager.prometheusMQAgent.PrometheusMQAgent.isInclude;

public class PrometheusPushGatewayService implements PrometheusPushGatewayRepo {

    private ConcurrentHashMap<Map<String, String>, MetricsObject> pushGatewayMemory;

    public PrometheusPushGatewayService(ConcurrentHashMap<Map<String, String>, MetricsObject> pushGatewayMemory) {
        this.pushGatewayMemory = pushGatewayMemory;
    }

    @Override
    public Future<Optional<String>> findByUrlSuffix(String urlSuffix) {
        String urlSuffixDecoded = null;
        try {
            urlSuffixDecoded = java.net.URLDecoder.decode(urlSuffix, String.valueOf(StandardCharsets.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, String>requestMapLabelValue = new HashMap<>();
        String[] params = urlSuffixDecoded.split("(?<!:)/");
        for(int i = 1; i < params.length; i = i + 2){
            String key = params[i];
            String value = params [i+1];
            if (key.equals("metrics")){
                key = "job";
            }
            requestMapLabelValue.put(key, value);
        }

        MetricsObject metricsObject = null;
        Map<String, String> foundMapLabelValue = null;
        for (Map<String, String> mapLabelValue: pushGatewayMemory.keySet()){
            if (isInclude(requestMapLabelValue, mapLabelValue) == true){
                foundMapLabelValue = mapLabelValue;
                metricsObject = pushGatewayMemory.get(mapLabelValue);
                break;
            }
        }
        if (foundMapLabelValue == null){
            return Future.succeededFuture(Optional.ofNullable(null));
        }else{
            String metricsString = metricsObject.getMetrics();
            if (pushGatewayMemory.get(foundMapLabelValue).getGetCount() <= 0){
                pushGatewayMemory.remove(foundMapLabelValue);
            }
            return Future.succeededFuture(Optional.ofNullable(metricsString));
        }
    }
}
