
package it.nextworks.nfvmano.configmanager.sb.elkstack;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import it.nextworks.nfvmano.configmanager.common.BodyCodecs;
import it.nextworks.nfvmano.configmanager.common.CreatedResponse;
import it.nextworks.nfvmano.configmanager.common.DeleteResponse;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKAlert;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKAlertDescription;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKDashboard;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKDashboardDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ELKStackConnector {

    private static final Logger log = LoggerFactory.getLogger(ELKStackConnector.class);

    private WebClient client;

    private String baseUrl;


    public ELKStackConnector(WebClient client, String baseUrl) {
        this.client = client;
        this.baseUrl = baseUrl;
    }

    public Future<ELKDashboard> postDashboard(
            ELKDashboardDescription dashboard
    ) {
        log.debug("KibanaDashboard:\n{}", dashboard.toString());
        log.debug("Requesting new dashboard {} to Kibana", dashboard);
        HttpRequest<ELKDashboard> post =
                client.post("/kibanaDashboard")
                        .as(BodyCodecs.jsonCatching( ELKDashboard.class, "ELK Stack"));


        Future<HttpResponse<ELKDashboard>> future = Future.future();
        post.sendJson(dashboard, future);
        // Now the post result is stored in future
        // Check output before returning the value
        return future.compose(this::checkELKDashboardOutput);
    }

    public Future<CreatedResponse> postJob(ELKDashboardDescription dashboard) {
        log.debug("KibanaDashboard:\n{}", dashboard.toString());
        log.debug("Requesting new dashboard {} to Kibana", dashboard);
        HttpRequest<CreatedResponse> post =
                client.post("/job/" + dashboard.getNsId())
                        .as(BodyCodecs.jsonCatching( CreatedResponse.class, "ELK Stack"));


        Future<HttpResponse<CreatedResponse>> future = Future.future();
        JsonObject requestBody = new JsonObject();
        post.sendJson(requestBody, future);
        // Now the post result is stored in future

        // Check output before returning the value
        return future.compose(this::checkELKStackJobCreateOutput);
    }


    public Future<HttpResponse<DeleteResponse>> deleteDashboard(
            String uid
    ) {
        HttpRequest<DeleteResponse> delete = client.delete("/kibanaDashboard/" + uid)
                .as(BodyCodecs.jsonCatching( DeleteResponse.class, "ELK Stack"));

        Future<HttpResponse<DeleteResponse>> future = Future.future();
        delete.send(future);
        return future;
    }

    public Future<HttpResponse<DeleteResponse>> deleteJob(
            String uid
    ) {
        HttpRequest<DeleteResponse> delete = client.delete("/job/" + uid)
                .as(BodyCodecs.jsonCatching( DeleteResponse.class, "ELK Stack"));
        Future<HttpResponse<DeleteResponse>> future = Future.future();
        delete.send(future);
        return future;
    }

    public Future<ELKAlert> postAlert(
            ELKAlertDescription elkAlertDescription
    ) {
        log.debug("ELKAlert:\n{}", elkAlertDescription.toString());
        log.debug("Requesting new Alert {} to ELK", elkAlertDescription);
        HttpRequest<ELKAlert> post =
                client.post("/alert")
                        .as(BodyCodecs.jsonCatching( ELKAlert.class, "ELK Stack"));


        Future<HttpResponse<ELKAlert>> future = Future.future();
        post.sendJson(elkAlertDescription, future);
        // Now the post result is stored in future
        // Check output before returning the value
        return future.compose(this::checkELKAlertOutput);
    }

    public Future<HttpResponse<DeleteResponse>> deleteAlert(
            String uid
    ) {
        HttpRequest<DeleteResponse> delete = client.delete("/alert/" + uid)
                .as(BodyCodecs.jsonCatching( DeleteResponse.class, "ELK Stack"));
        Future<HttpResponse<DeleteResponse>> future = Future.future();
        delete.send(future);
        return future;
    }


    private Future<ELKAlert> checkELKAlertOutput(
            HttpResponse<ELKAlert> response
    ) {
        int code = response.statusCode();
        if (code == 412) {
            // 412 == precondition failed == name conflict
            return Future.failedFuture(
                    new HttpStatusException(409, "ELK Alert name conflict, please use a different name")
            );
        } else if (code < 200 || 300 <= code) {
            // Problematic code or body not parsed correctly
            log.warn("ELK unexpected error: code {}, response: {}", code, response.body());
            return Future.failedFuture(new IllegalArgumentException(
                    String.format("Unexpected response from ELK stack during Dashboard creation: status code %s, response %s", code, response.body())
            ));
        } else {
            // All good
            // edit the URL adding the base
            ELKAlert out = response.body();
            return Future.succeededFuture(out);
        }
    }

    private Future<ELKDashboard> checkELKDashboardOutput(
            HttpResponse<ELKDashboard> response
    ) {
        int code = response.statusCode();
        if (code == 412) {
            // 412 == precondition failed == name conflict
            return Future.failedFuture(
                    new HttpStatusException(409, "ELK Dashboard name conflict, please use a different name")
            );
        } else if (code < 200 || 300 <= code) {
            // Problematic code or body not parsed correctly
            log.warn("ELK unexpected error: code {}, response: {}", code, response.body());
            return Future.failedFuture(new IllegalArgumentException(
                    String.format("Unexpected response from ELK stack during Dashboard creation: status code %s, response %s", code, response.body())
            ));
        } else {
            // All good
            // edit the URL adding the base
            ELKDashboard out = response.body();
            return Future.succeededFuture(out);
        }
    }

    private Future<CreatedResponse> checkELKStackJobCreateOutput(
            HttpResponse<CreatedResponse> response
    ) {
        int code = response.statusCode();
        if (code == 412) {
            // 412 == precondition failed == name conflict
            return Future.failedFuture(
                    new HttpStatusException(409, "ELK Job name conflict, please use a different name")
            );
        } else if (code < 200 || 300 <= code) {
            // Problematic code or body not parsed correctly
            log.warn("ELK unexpected error: code {}, response: {}", code, response.body());
            return Future.failedFuture(new IllegalArgumentException(
                    String.format("Unexpected response from ELK stack during Job creation: status code %s, response %s", code, response.body())
            ));
        } else {
            // All good
            // edit the URL adding the base
            CreatedResponse out = response.body();
            return Future.succeededFuture(out);
        }
    }

}
