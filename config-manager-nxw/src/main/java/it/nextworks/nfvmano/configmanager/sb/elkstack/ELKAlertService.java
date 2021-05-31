package it.nextworks.nfvmano.configmanager.sb.elkstack;

import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.elkstack.ELKAlertRepo;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKAlert;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKAlertDescription;

import java.util.HashSet;
import java.util.Set;


public class ELKAlertService implements ELKAlertRepo {

    private ELKStackConnector connector;

    private ELKAlertRepo repo;

    public ELKAlertService(ELKStackConnector connector, ELKAlertRepo elkDashboardRepo) {
        this.connector = connector;
        this.repo = elkDashboardRepo;
    }

    @Override
    public Future<ELKAlert> save(ELKAlertDescription description) {
        Future<ELKAlert> elkAlertFuture = connector.postAlert(description).compose(
                result -> {
                    return repo.save(result);
        });
        return elkAlertFuture;
    }

    @Override
    public Future<ELKAlert> save(ELKAlert description) {
        return null;
    }

    @Override
    public Future<Set<String>> deleteById(String dashboardId) {
        Future<Set<String>> future = connector.deleteAlert(dashboardId).compose(
                result -> {
                    Set<String> deleted = new HashSet<String>(result.body().getDeleted());
                    return Future.succeededFuture(deleted);
                }
        );
        return future;
    }

    @Override
    public Future<ELKAlert> findById(String dashboardId) {
        return null;
    }

}
