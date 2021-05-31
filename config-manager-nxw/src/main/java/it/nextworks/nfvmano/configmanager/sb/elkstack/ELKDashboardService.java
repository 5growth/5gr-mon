package it.nextworks.nfvmano.configmanager.sb.elkstack;

import io.vertx.core.Future;
import io.vertx.ext.web.client.HttpResponse;
import it.nextworks.nfvmano.configmanager.common.DeleteResponse;
import it.nextworks.nfvmano.configmanager.elkstack.ELKDashboardRepo;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKDashboard;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKDashboardDescription;

import java.util.Set;


public class ELKDashboardService implements ELKDashboardRepo {

    private ELKStackConnector connector;

    private ELKDashboardRepo repo;

    public ELKDashboardService(ELKStackConnector connector, ELKDashboardRepo elkDashboardRepo) {
        this.connector = connector;
        this.repo = elkDashboardRepo;
    }

    @Override
    public Future<ELKDashboard> save(ELKDashboardDescription description) {
        Future<ELKDashboard> dashboardResponseFuture = connector.postJob(description).compose(
                result -> {return connector.postDashboard(description);}
        ).compose(
                result -> {
                    return repo.save(result);
        });
        return dashboardResponseFuture;
    }

    @Override
    public Future<ELKDashboard> save(ELKDashboard description) {
        return null;
    }

    @Override
    public Future<Set<String>> deleteById(String dashboardId) {
        Future<HttpResponse<DeleteResponse>> dashboardFuture = connector.deleteDashboard(dashboardId);
        Future<Set<String>> map;
        map = dashboardFuture.compose(deleting -> {
            Future<HttpResponse<DeleteResponse>> compose;
            if (deleting.body() != null) {
                compose = repo.findById(dashboardId).compose(result -> {
                    return connector.deleteJob(result.getNsId());
                });
                return compose;
            }
            return null;
        }).compose(result -> {
                    return repo.deleteById(dashboardId);
                }
            );
        return map;
    }

    @Override
    public Future<ELKDashboard> findById(String dashboardId) {
        return null;
    }

}
