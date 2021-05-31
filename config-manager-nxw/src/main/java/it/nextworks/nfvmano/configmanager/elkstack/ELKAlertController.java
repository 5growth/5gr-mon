

package it.nextworks.nfvmano.configmanager.elkstack;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import it.nextworks.nfvmano.configmanager.common.DeleteResponse;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKAlert;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKAlertDescription;
import it.nextworks.nfvmano.configmanager.sb.elkstack.ELKAlertService;
import it.nextworks.nfvmano.configmanager.utils.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Set;


public class ELKAlertController {

    private static final Logger log = LoggerFactory.getLogger(ELKAlertController.class);

    private ELKAlertService repo;

    public ELKAlertController(ELKAlertService repo) {
        this.repo = repo;
    }

    public void postAlert(RoutingContext ctx) {
        log.info("Received call ELK POST alert.");
        ELKAlertDescription desc = ctx.get("_parsed");
        log.debug("ELK alert description:\n{}", desc);
        Future<ELKAlert> ar = repo.save(desc);
        ar.setHandler(
                future -> {
                    if (future.failed()) {
                        ctx.fail(future.cause());
                    } else {
                        log.debug("ELK POST alert saved.");
                        ContextUtils.respond(ctx, 201, future.result());
                        log.info("ELK POST alert call completed.");
                    }
                }
        );
    }
    public void deleteAlert(RoutingContext ctx) {
        String alertId = ctx.pathParam("alertId");
        log.info("Received call DELETE ELK alert {}.", alertId);
        Future<Set<String>> ar = repo.deleteById(alertId);
        ar.setHandler(
                future -> {
                    if (future.failed()) {
                        ctx.fail(future.cause());
                    } else {
                        Set<String> deleted = future.result();
                        DeleteResponse response = new DeleteResponse();
                        response.setDeleted(new ArrayList<>(deleted));
                        ContextUtils.respond(ctx, response);
                        log.info("DELETE ELK alert {} call completed.", alertId);
                    }
                }
        );
    }

}
