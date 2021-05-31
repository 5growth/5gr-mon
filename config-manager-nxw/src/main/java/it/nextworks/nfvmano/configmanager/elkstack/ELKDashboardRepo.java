package it.nextworks.nfvmano.configmanager.elkstack;

import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKDashboard;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKDashboardDescription;

import java.util.Set;

public interface ELKDashboardRepo {

    Future<ELKDashboard> save(ELKDashboardDescription description);
    Future<ELKDashboard> save(ELKDashboard description);

    Future<Set<String>> deleteById(String dashboardId);
    Future<ELKDashboard> findById(String dashboardId);

}
