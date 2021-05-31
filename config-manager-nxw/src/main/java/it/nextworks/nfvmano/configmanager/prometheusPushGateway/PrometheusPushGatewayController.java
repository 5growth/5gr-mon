package it.nextworks.nfvmano.configmanager.prometheusPushGateway;

import io.vertx.core.Future;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.validation.ValidationException;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import it.nextworks.nfvmano.configmanager.utils.ContextUtils;
import it.nextworks.nfvmano.configmanager.utils.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PrometheusPushGatewayController {

    private static final Logger log = LoggerFactory.getLogger(PrometheusPushGatewayController.class);

    private PrometheusPushGatewayRepo repo;

    private static void validate(RoutingContext ctx) {
        Validated raw = ctx.get("_parsed");
        Optional<ValidationException> error = raw.validate();
        if (error.isPresent()) {
            ctx.fail(new HttpStatusException(400, error.get().getMessage()));
        } else {
            ctx.next();
        }
    }

    public PrometheusPushGatewayController(PrometheusPushGatewayRepo repo
    ) {
        this.repo = repo;
    }


    public void getPrometheusMetrics(Route route) {
        String loggedOp = "getPrometheusMetrics";
        // Business
        route.handler(ctx -> {
            // Transform
            String fullURLPath = ctx.normalisedPath();
            log.debug("Validation successful, executing op {} on {}", loggedOp, fullURLPath);
            Future<Optional<String>> future = repo.findByUrlSuffix(fullURLPath);
            ContextUtils.await(future, ctx);
        });
        // Respond
        route.handler(ctx -> {
            log.debug("Sending response for op {}", loggedOp);
            Optional<String> response = ctx.<Optional>get("_awaited");
            log.debug("Response: {}", response);
            ctx.setAcceptableContentType("text/plain; charset=utf-8");
            if(response.isPresent() == true){
                ContextUtils.respond(ctx, 200, response.get());
            }else{
                ContextUtils.respond(ctx, 404);
            }
        });
    }
}
