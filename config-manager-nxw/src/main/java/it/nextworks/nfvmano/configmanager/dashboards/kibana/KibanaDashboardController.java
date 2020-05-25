package it.nextworks.nfvmano.configmanager.dashboards.kibana;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import com.telcaria.kibana.dashboards.model.KibanaDashboardDescription;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import it.nextworks.nfvmano.configmanager.dashboards.DashboardController;
import it.nextworks.nfvmano.configmanager.utils.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class KibanaDashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private KibanaDashboardRepo repo;

    public KibanaDashboardController(KibanaDashboardRepo repo) {
        this.repo = repo;
    }

    public void postDashboard(RoutingContext ctx) {
        log.info("Received call POST Kibana dashboard.");
        KibanaDashboardDescription desc = ctx.get("_parsed");
        log.debug("Dashboard description:\n{}", desc);
        Future<KibanaDashboardDescription> ar = repo.save(desc);
        ar.setHandler(
                future -> {
                    if (future.failed()) {
                        ctx.fail(future.cause());
                    } else {
                        log.debug("Dashboard saved.");
                        ContextUtils.respond(ctx, 201, future.result());
                        log.info("POST dashboard call completed.");
                    }
                }
        );
    }
}
