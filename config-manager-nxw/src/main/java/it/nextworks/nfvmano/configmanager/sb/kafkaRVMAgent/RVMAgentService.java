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

package it.nextworks.nfvmano.configmanager.sb.kafkaRVMAgent;

import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.rvmagent.RVMAgentRepo;
import it.nextworks.nfvmano.configmanager.rvmagent.messages.*;
import it.nextworks.nfvmano.configmanager.rvmagent.model.PrometheusCollector;
import it.nextworks.nfvmano.configmanager.rvmagent.model.RVMAgent;
import it.nextworks.nfvmano.configmanager.rvmagent.model.RVMAgentExporter;

import java.util.List;
import java.util.Optional;


public class RVMAgentService implements RVMAgentRepo {

    private RVMAgentConnector rvmAgentConnector;

    private RVMAgentRepo db;
    private RVMAgent rvmAgent;
    private static Integer id = 0;

    public RVMAgentService(RVMAgentConnector rvmAgentConnector, RVMAgentRepo db) {
        this.rvmAgentConnector = rvmAgentConnector;
        this.db = db;
    }

    private String getRVMAgentId(){
        id ++;
        return "vm_agent_" + id.toString();
    }

    @Override
    public Future<RVMAgentCreateResponse> save(RVMAgent rvmAgent) {
        if (rvmAgent.getRvmAgentId() == null){
            rvmAgent.setRvmAgentId(getRVMAgentId());
            rvmAgent.start_kafka_client();
            Thread newThread = new Thread(rvmAgent);
            newThread.start();
            Future<RVMAgentCreateResponse> future = db.save(rvmAgent);
            return future;
        }
        String agent_id = rvmAgent.getRvmAgentId();
        Future<Optional<List<RVMAgent>>> rvmAgentFuture = db.findById(agent_id);
        Integer size = rvmAgentFuture.result().get().size();
        if (size != 0) {
            RVMAgent rvmAgentFromDB = rvmAgentFuture.result().get().get(0);
            return Future.succeededFuture(rvmAgentFromDB.getRVMAgentCreateResponse());
        }else{
            rvmAgent.setRvmAgentId(agent_id);
            rvmAgent.start_kafka_client();
            Thread newThread = new Thread(rvmAgent);
            newThread.start();
            Future<RVMAgentCreateResponse> future = db.save(rvmAgent);
            return future;
        }

    }

    @Override
    public Future<Optional<List<RVMAgent>>> findAll() {
        return db.findAll();
    }

    @Override
    public Future<String> deleteAgentById(String agentId) {
        Future<Optional<List<RVMAgent>>> rvmAgentFuture =  db.findById(agentId);
        if (rvmAgentFuture.result().get().isEmpty()){
            return Future.succeededFuture(null);
        }
        RVMAgent rvmAgent = rvmAgentFuture.result().get().get(0);
        try {
            rvmAgent.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        db.deleteAgentById(agentId);
        return Future.succeededFuture(agentId);
    }



    @Override
    public Future<Optional<List<RVMAgent>>> findById(String rvmAgentId) {
        return db.findById(rvmAgentId);
    }

    @Override
    public Future<Optional<RVMAgentCommand>> saveRVMAgentCommand(RVMAgentCommand rvmAgentCommand) {

        Future<Optional<List<RVMAgent>>> rvmAgentFuture =  db.findById(rvmAgentCommand.getAgentId());
        RVMAgent rvmAgent = rvmAgentFuture.result().get().get(0);
        RVMAgentCommand returnRVMAgentCommand = rvmAgent.executeCommand(rvmAgentCommand);
        return Future.succeededFuture(Optional.ofNullable(returnRVMAgentCommand));
    }

    @Override
    public Future<Optional<RVMAgentExporter>> saveRVMAgentExporter(RVMAgentExporter rvmAgentExporter) {
        RVMAgentExporter retrun_rvmAgentExporter = new RVMAgentExporter();
        Future<Optional<List<RVMAgent>>> rvmAgentFuture =  db.findById(rvmAgentExporter.getAgentId());
        RVMAgent rvmAgent = rvmAgentFuture.result().get().get(0);
//        AddPrometheusCollector addPrometheusCollector = rvmAgent.addPrometheusCollector(rvmAgentInstallExporter.getCollector());
        PrometheusCollector return_addPrometheusCollector = rvmAgent.addPrometheusCollector(rvmAgentExporter.getInstallCollector());
        RVMAgentCommand returnRVMAgentCommand = rvmAgent.executeCommand(rvmAgentExporter.getInstallCommand());
        retrun_rvmAgentExporter.setCollector(return_addPrometheusCollector);
        retrun_rvmAgentExporter.setCommand(returnRVMAgentCommand);
        return Future.succeededFuture(Optional.ofNullable(retrun_rvmAgentExporter));
    }

    @Override
    public Future<RVMAgentExporter> deletePrometheusExporterById(String agentId, String prometheusExporterId) {
        RVMAgentExporter retrun_rvmAgentExporter = new RVMAgentExporter();
        Future<Optional<List<RVMAgent>>> rvmAgentFuture =  db.findById(agentId);
        RVMAgent rvmAgent = rvmAgentFuture.result().get().get(0);
        RVMAgentExporter rvmAgentExporter = new RVMAgentExporter();
        rvmAgentExporter.setAgentId(agentId);
        rvmAgentExporter.setExporter(prometheusExporterId);
        rvmAgent.delPrometheusCollector(rvmAgentExporter.getPrometheusCollectorId());
        RVMAgentCommand returnRVMAgentCommand = rvmAgent.executeCommand(rvmAgentExporter.getUninstallCommand());
        retrun_rvmAgentExporter.setPort(rvmAgentExporter.getPort());
        retrun_rvmAgentExporter.setHost(rvmAgentExporter.getHost());
        retrun_rvmAgentExporter.setCommand(returnRVMAgentCommand);
        return Future.succeededFuture(retrun_rvmAgentExporter);
    }

    @Override
    public Future<RVMAgentCommandResponse> findRVMAgentCommandResponseById(String agentId, String commandId) {
        return db.findRVMAgentCommandResponseById(agentId, commandId);
    }

    @Override
    public Future<AddPrometheusCollectorResponse> addPrometheusCollector(PrometheusCollector prometheusCollector) {
        Future<Optional<List<RVMAgent>>> rvmAgentFuture =  db.findById(prometheusCollector.getRvmAgentId());
        RVMAgent rvmAgent = rvmAgentFuture.result().get().get(0);
        prometheusCollector.setPrometheusTopic("prometheus");
        PrometheusCollector returnPrometheusCollector = rvmAgent.addPrometheusCollector(prometheusCollector);
        return Future.succeededFuture(returnPrometheusCollector.getAddPrometheusCollectorResponse());

    }

    @Override
    public Future<String> deletePrometheusCollectorById(String agentId, String prometheusCollectorId) {
        Future<Optional<List<RVMAgent>>> rvmAgentFuture =  db.findById(agentId);
        RVMAgent rvmAgent = rvmAgentFuture.result().get().get(0);
        rvmAgent.delPrometheusCollector(prometheusCollectorId);
        return Future.succeededFuture(prometheusCollectorId);
    }



}
