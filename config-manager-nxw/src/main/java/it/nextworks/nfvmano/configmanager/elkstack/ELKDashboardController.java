

package it.nextworks.nfvmano.configmanager.elkstack;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import it.nextworks.nfvmano.configmanager.common.DeleteResponse;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKDashboard;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKDashboardDescription;
import it.nextworks.nfvmano.configmanager.utils.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Set;


public class ELKDashboardController {

    private static final Logger log = LoggerFactory.getLogger(ELKDashboardController.class);

    private ELKDashboardRepo repo;

    public ELKDashboardController(ELKDashboardRepo repo) {
        this.repo = repo;
    }

    public void postDashboard(RoutingContext ctx) {
        log.info("Received call ELK stack POST dashboard.");
        ELKDashboardDescription desc = ctx.get("_parsed");
        log.debug("Dashboard description:\n{}", desc);
        Future<ELKDashboard> ar = repo.save(desc);
        ar.setHandler(
                future -> {
                    if (future.failed()) {
                        ctx.fail(future.cause());
                    } else {
                        log.debug("ELK stack Dashboard saved.");
                        ContextUtils.respond(ctx, 201, future.result());
                        log.info("POST ELK stack dashboard call completed.");
                    }
                }
        );
    }



    public void deleteDashboard(RoutingContext ctx) {
        String dashboardId = ctx.pathParam("dashId");
        log.info("Received call DELETE ELK dashboard {}.", dashboardId);
        Future<Set<String>> ar = repo.deleteById(dashboardId);
        ar.setHandler(
                future -> {
                    if (future.failed()) {
                        ctx.fail(future.cause());
                    } else {
                        Set<String> deleted = future.result();
                        DeleteResponse response = new DeleteResponse();
                        response.setDeleted(new ArrayList<>(deleted));
                        ContextUtils.respond(ctx, response);
                        log.info("DELETE ELK dashboard {} call completed.", dashboardId);
                    }
                }
        );
    }
}
