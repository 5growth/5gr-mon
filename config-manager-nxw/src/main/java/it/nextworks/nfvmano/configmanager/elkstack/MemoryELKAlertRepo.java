
package it.nextworks.nfvmano.configmanager.elkstack;

import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKAlert;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKAlertDescription;

import java.util.*;


public class MemoryELKAlertRepo implements ELKAlertRepo {

    private Map<String, ELKAlert> map = new HashMap<>();

    @Override
    public Future<ELKAlert> save(ELKAlertDescription description) {
        String uuid = UUID.randomUUID().toString();
        ELKAlert alert = new ELKAlert(description).alertId(uuid);

        return save(alert);
    }

    @Override
    public Future<ELKAlert> save(ELKAlert elkAlert) {
        map.put(elkAlert.getAlertId(), elkAlert);
        return Future.succeededFuture(elkAlert);
    }

    @Override
    public Future<Set<String>> deleteById(String alertId) {
        ELKAlert removed = map.remove(alertId);
        if (removed == null) {
            return Future.succeededFuture(Collections.emptySet());
        } else {
            return Future.succeededFuture(Collections.singleton(alertId));
        }
    }

    @Override
    public Future<ELKAlert> findById(String alertId) {
        return Future.succeededFuture(map.get(alertId));
    }

}
