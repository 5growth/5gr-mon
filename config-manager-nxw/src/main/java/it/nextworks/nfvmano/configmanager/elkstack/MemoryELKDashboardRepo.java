
package it.nextworks.nfvmano.configmanager.elkstack;

import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKDashboard;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKDashboardDescription;

import java.util.*;


public class MemoryELKDashboardRepo implements ELKDashboardRepo {

    private Map<String, ELKDashboard> map = new HashMap<>();

    @Override
    public Future<ELKDashboard> save(ELKDashboardDescription description) {
        String uuid = UUID.randomUUID().toString();
        ELKDashboard dashboard = new ELKDashboard(description)
                .dashboardId(uuid)
                .url("/this/is/an/url/" + uuid);
        return save(dashboard);
    }

    @Override
    public Future<ELKDashboard> save(ELKDashboard elkDashboard) {
        map.put(elkDashboard.getDashboardId(), elkDashboard);
        return Future.succeededFuture(elkDashboard);
    }

    @Override
    public Future<Set<String>> deleteById(String dashboardId) {
        ELKDashboard removed = map.remove(dashboardId);
        if (removed == null) {
            return Future.succeededFuture(Collections.emptySet());
        } else {
            return Future.succeededFuture(Collections.singleton(dashboardId));
        }
    }

    @Override
    public Future<ELKDashboard> findById(String dashboardId) {
        return Future.succeededFuture(map.get(dashboardId));
    }

}
