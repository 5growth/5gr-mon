/*
 * Copyright 2018 Nextworks s.r.l.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.nextworks.nfvmano.configmanager.rvmagent;

import io.vertx.core.Future;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.streams.Pump;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.validation.ValidationException;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import it.nextworks.nfvmano.configmanager.common.DeleteResponse;
import it.nextworks.nfvmano.configmanager.rvmagent.messages.AddPrometheusCollectorResponse;
import it.nextworks.nfvmano.configmanager.rvmagent.messages.RVMAgentCommand;
import it.nextworks.nfvmano.configmanager.rvmagent.messages.RVMAgentCommandResponse;
import it.nextworks.nfvmano.configmanager.rvmagent.model.PrometheusCollector;
import it.nextworks.nfvmano.configmanager.rvmagent.model.RVMAgent;
import it.nextworks.nfvmano.configmanager.rvmagent.model.RVMAgentExporter;
import it.nextworks.nfvmano.configmanager.utils.ContextUtils;
import it.nextworks.nfvmano.configmanager.utils.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static it.nextworks.nfvmano.configmanager.utils.Paths.Rest.GET_RESOURCES_FILES;

public class RVMAgentController {

    private static final Logger log = LoggerFactory.getLogger(RVMAgentController.class);

    private RVMAgentRepo repo;
    private String scriptsFilePath = "fileserver/scripts";
    private Object agentsFilePath = "fileserver/agents";
    private Object filesFilePath = "fileserver/files";

    private static void validate(RoutingContext ctx) {
        Validated raw = ctx.get("_parsed");
        Optional<ValidationException> error = raw.validate();
        if (error.isPresent()) {
            ctx.fail(new HttpStatusException(400, error.get().getMessage()));
        } else {
            ctx.next();
        }
    }

    public RVMAgentController(RVMAgentRepo repo) {
        this.repo = repo;
    }

    public static <T extends Validated, S> void installHandler(
            Route route,
            Function<T, Future<S>> main,
            String loggedOp,
            Integer statusCode
    ) {
        // Validation
        route.handler(ctx -> {
            log.info("Received call to {}", loggedOp);
            validate(ctx);
        });
        // Business
        route.handler(ctx -> {
            // Transform
            log.info("Validation successful, executing op {}", loggedOp);
            Future<S> future = main.apply(ctx.get("_parsed"));
            // Await result
            ContextUtils.await(future, ctx);
        });
        // Respond
        route.handler(ctx -> {
            log.info("Sending response for op {}", loggedOp);
            Object response = ctx.get("_awaited");
            log.debug("Response: {}", response);
            ContextUtils.respond(ctx, statusCode, response);
        });
    }

    public static <S> void installHandler(
            Route route,
            Supplier<Future<S>> main,
            String loggedOp
    ) {
        // No input, no validation
        // Business
        route.handler(ctx -> {
            // Transform
            log.info("Validation successful, executing op {}", loggedOp);
            Future<S> future = main.get();
            // Await result
            ContextUtils.await(future, ctx);
        });
        // Respond
        route.handler(ctx -> {
            log.info("Sending response for op {}", loggedOp);
            Object response = ctx.get("_awaited");
            log.debug("Response: {}", response);
            ContextUtils.respond(ctx, response);
        });
    }

    public void postRVMAgent(Route route) {
        String loggedOp = "postRVMAgent";
        route.handler(ContextUtils.parsing(RVMAgent.class));
        installHandler(route, repo::save, "postRVMAgent", 201);
    }

    public void getRVMAgents(Route route) {
        installHandler(route, repo::findAll, "getAllRVMAgents");
    }

    public void getRVMAgent(Route route) {
        String loggedOp = "getRVMAgent";
        // No Validation
        // Business
        route.handler(ctx -> {
            // Transform
            String agentId = ctx.pathParam("agentId");
            log.info("Validation successful, executing op {} on {}", loggedOp, agentId);
            Future<Optional<List<RVMAgent>>> future = repo.findById(agentId);
            future = future.compose(s -> {
                if (s.get().size() == 0) {
                    ctx.fail(new HttpStatusException(404, "No such RVM agent with Id: " + agentId));
                }
                return Future.succeededFuture(s);
            });
            ContextUtils.await(future, ctx);
        });
        // Respond
        route.handler(ctx -> {
            log.info("Sending response for op {}", loggedOp);
            Object response = ctx.get("_awaited");
            log.debug("Response: {}", response);
            ContextUtils.respond(ctx, response);
        });
    }



    public void postRVMAgentCommand(Route route) {
        String loggedOp = "postRVMAgentCommand";
        route.handler(ContextUtils.parsing(RVMAgentCommand.class));
        installHandler(route, repo::saveRVMAgentCommand, "postRVMAgentCommand", 201);
    }

    public void postInstallExporter(Route route) {
        String loggedOp = "postInstallExporter";
        route.handler(ContextUtils.parsing(RVMAgentExporter.class));
        installHandler(route, repo::saveRVMAgentExporter, "postInstallExporter", 201);
    }

    public void deleteUninstallExporter(Route route) {
        String loggedOp = "deleteUninstallExporter";
        // No Validation
        // Business
        route.handler(ctx -> {
            // Transform
            String agentId = ctx.pathParam("agentId");
            String prometheusExporterId = ctx.pathParam("exporterId");
            log.info("Validation successful, executing op {} on {}", loggedOp, agentId);
            Future<Optional<List<RVMAgent>>> future = repo.findById(agentId);
            List<RVMAgent> rvmAgentList = future.result().get();
            if (rvmAgentList.isEmpty()) {
                ctx.fail(new HttpStatusException(404, "No such RVM agent with Id: " + agentId));
                ContextUtils.await(future, ctx);
            }else {
                Future<RVMAgentExporter> future1 = repo.deletePrometheusExporterById(agentId, prometheusExporterId);
                // Translate missing alert into 404 error
                future1 = future1.compose(s -> {
                    if (s == null) {
                        ctx.fail(new HttpStatusException(404, "No such Prometheus Collector"));
                    }
                    return Future.succeededFuture(s);
                });
                ContextUtils.await(future1, ctx);
            };
        });
        // Respond
        route.handler(ctx -> {
            log.info("Sending response for op {}", loggedOp);
            Object response = ctx.get("_awaited");;
            log.debug("Response: {}", response);
            ContextUtils.respond(ctx, response);
        });
    }

    public void deleteRVMAgent(Route route) {
        String loggedOp = "deleteRVMAgent";
        // No Validation
        // Business
        route.handler(ctx -> {
            // Transform
            String alertId = ctx.pathParam("agentId");
            log.info("Validation successful, executing op {} on {}", loggedOp, alertId);
            Future<Set<String>> future = repo.deleteAgentById(alertId);
            // Translate missing agent into 404 error
            future = future.compose(s -> {
                if (s == null) {
                    ctx.fail(new HttpStatusException(404, "No such RVM Agent"));
                }
                return Future.succeededFuture(s);
            });
            ContextUtils.await(future, ctx);
        });
        // Respond
        route.handler(ctx -> {
            log.info("Sending response for op {}", loggedOp);
            DeleteResponse response = new DeleteResponse().deleted(ctx.<Set<String>>get("_awaited"));
            log.debug("Response: {}", response);
            ContextUtils.respond(ctx, response);
        });
    }

    public void getRVMAgentCommand(Route route) {
        String loggedOp = "getRVMAgentCommand";
        // No Validation
        // Business
        route.handler(ctx -> {
            // Transform
            String agentId = ctx.pathParam("agentId");
            String commandId = ctx.pathParam("commandId");
            log.info("Validation successful, executing op {} on {} {}", loggedOp, agentId, commandId);
            Future<Optional<List<RVMAgent>>> future = repo.findById(agentId);
            List<RVMAgent> rvmAgentList = future.result().get();
                if (rvmAgentList.isEmpty()) {
                    ctx.fail(new HttpStatusException(404, "No such RVM agent with Id: " + agentId));
                    ContextUtils.await(future, ctx);
                }else{
                    Future<RVMAgentCommandResponse> future2 = repo.findRVMAgentCommandResponseById(agentId, commandId);
                        if ((future2.result() == null)) {
                            ctx.fail(new HttpStatusException(404, String.format("No response for command id %s agent id %s", commandId, agentId)));
                        }
                        ContextUtils.await(future2, ctx);
                    }
        });
        // Respond
        route.handler(ctx -> {
            log.info("Sending response for op {}", loggedOp);
            Object response = ctx.get("_awaited");
            log.debug("Response: {}", response);
            ContextUtils.respond(ctx, response);
        });
    }

    public void postAddPrometheusCollector(Route route) {
        String loggedOp = "postAddPrometheusCollector";
        route.handler(ContextUtils.parsing(PrometheusCollector.class));
        // Business
        route.handler(ctx -> {
            // Transform
            log.info("Validation successful, executing op {}", loggedOp);
            PrometheusCollector addPrometheusCollector = ctx.get("_parsed");
            String agentId = addPrometheusCollector.getRvmAgentId();
            Future<Optional<List<RVMAgent>>> future = repo.findById(agentId);
            List<RVMAgent> rvmAgentList = future.result().get();
            if (rvmAgentList.isEmpty()) {
                ctx.fail(new HttpStatusException(404, "No such RVM agent with Id: " + agentId));
                ContextUtils.await(future, ctx);
            }else{
                Future<AddPrometheusCollectorResponse> future2 = repo.addPrometheusCollector(addPrometheusCollector);
                ContextUtils.await(future2, ctx);
            }
        });
        // Respond
        route.handler(ctx -> {
            log.info("Sending response for op {}", loggedOp);
            Object response = ctx.get("_awaited");
            log.debug("Response: {}", response);
            ContextUtils.respond(ctx, 201, response);
        });

//        installHandler(route, repo::addPrometheusCollector, "postAddPrometheusCollector", 201);
    }

    public void deletePrometheusCollector(Route route) {
        String loggedOp = "deletePrometheusCollector";
        // No Validation
        // Business
        route.handler(ctx -> {
            // Transform
            String agentId = ctx.pathParam("agentId");
            String prometheusCollectorId = ctx.pathParam("prometheusCollectorId");
            log.info("Validation successful, executing op {} on {}", loggedOp, agentId);
            Future<Set<String>> future = repo.deletePrometheusCollectorById(agentId, prometheusCollectorId);
            // Translate missing alert into 404 error
            future = future.compose(s -> {
                if (s.size() == 0) {
                    ctx.fail(new HttpStatusException(404, "No such Prometheus Collector"));
                }
                return Future.succeededFuture(s);
            });
            ContextUtils.await(future, ctx);
        });
        // Respond
        route.handler(ctx -> {
            log.info("Sending response for op {}", loggedOp);
            DeleteResponse response = new DeleteResponse().deleted(ctx.<Set<String>>get("_awaited"));
            log.debug("Response: {}", response);
            ContextUtils.respond(ctx, response);
        });
    }

    public void getScripts(Route route) {
        String loggedOp = "getScript";
        route.handler(this::downloadScript);
    }

    public void getAgents(Route route) {
        String loggedOp = "getAgents";
        route.handler(this::downloadAgent);
    }

    public void getFiles(Route route) {
        String loggedOp = "getFiles";
        route.handler(this::downloadFile);
    }

    private void downloadScript(RoutingContext routingContext) {
        String scriptName = routingContext.pathParam("scriptName");
        String scriptPath = String.format("%s/%s", this.scriptsFilePath,  scriptName);
        log.info("download script: {}", scriptPath);
        download(routingContext, scriptPath);
    }

    private void downloadAgent(RoutingContext routingContext) {
        String agentName = routingContext.pathParam("agentName");
        String agentPath = String.format("%s/%s", this.agentsFilePath, agentName);
        log.info("download agent: {}", agentName);
        download(routingContext, agentPath);
    }

    private void downloadFile(RoutingContext routingContext) {
        String fullURLPath = routingContext.normalisedPath();
        String URLprefix = GET_RESOURCES_FILES.replace("*", "");
        String fileName = fullURLPath.replace(URLprefix, "");
        String filePath = String.format("%s/%s", this.filesFilePath, fileName);
        log.info("download file: {}", fileName);
        download(routingContext, filePath);
    }

    private void download (RoutingContext routingContext, String path){
            routingContext.vertx().fileSystem().open(path, new OpenOptions(), readEvent -> {

                if (readEvent.failed()) {
                    routingContext.response().setStatusCode(500).end();
                    return;
                }

                AsyncFile asyncFile = readEvent.result();
                routingContext.response().setChunked(true);
                Pump pump = Pump.pump(asyncFile, routingContext.response());
                pump.start();
                asyncFile.endHandler(aVoid -> {
                    asyncFile.close();
                    routingContext.response().end();
                });
            });
        }


}
