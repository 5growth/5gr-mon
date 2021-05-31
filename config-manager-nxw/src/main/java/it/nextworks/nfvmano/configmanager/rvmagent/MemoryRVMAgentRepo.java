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
import it.nextworks.nfvmano.configmanager.rvmagent.messages.*;
import it.nextworks.nfvmano.configmanager.rvmagent.model.PrometheusCollector;
import it.nextworks.nfvmano.configmanager.rvmagent.model.RVMAgent;
import it.nextworks.nfvmano.configmanager.rvmagent.model.RVMAgentExporter;
import it.nextworks.nfvmano.configmanager.sb.kafkaRVMAgent.model.KafkaRVMAgentCommandResponse;

import java.util.*;

public class MemoryRVMAgentRepo implements RVMAgentRepo {
    private Map<String, RVMAgent> rvmAgentMap = new HashMap<>();

    @Override
    public Future<RVMAgentCreateResponse> save(RVMAgent rvmAgent) {
        rvmAgentMap.put(rvmAgent.getRvmAgentId(), rvmAgent);
        return (Future<RVMAgentCreateResponse>) Future.succeededFuture(rvmAgent.getRVMAgentCreateResponse());
    }

    @Override
    public Future<Optional<List<RVMAgent>>> findAll() {
        return Future.succeededFuture(Optional.ofNullable(new ArrayList(rvmAgentMap.values())));
    }

    @Override
    public Future<Set<String>> deleteAgentById(String agentId) {
        rvmAgentMap.remove(agentId);
        return Future.succeededFuture(Collections.singleton(agentId));
    }

    @Override
    public Future<RVMAgentExporter> deletePrometheusExporterById(String agentId, String prometheusExporterId) {
        return null;
    }

    @Override
    public Future<Optional<List<RVMAgent>>> findById(String rvmAgentId) {
        List<RVMAgent> rvmAgentList = null;
        if (rvmAgentMap.get(rvmAgentId) == null){
            rvmAgentList = Arrays.asList();
        }else{
            rvmAgentList = Arrays.asList(rvmAgentMap.get(rvmAgentId));
        }
        return Future.succeededFuture(Optional.ofNullable(rvmAgentList));
    }

    @Override
    public Future<Optional<RVMAgentCommand>> saveRVMAgentCommand(RVMAgentCommand rvmAgentCommand) {
        RVMAgent rvmAgent = rvmAgentMap.get(rvmAgentCommand.getAgentId());
        RVMAgentCommand returnRvmAgentCommand = rvmAgent.getCommandMap().get(rvmAgentCommand.getCommandId().toString());
        return Future.succeededFuture(Optional.ofNullable(returnRvmAgentCommand));
    }

    @Override
    public Future<Optional<RVMAgentExporter>> saveRVMAgentExporter(RVMAgentExporter rvmAgentExporter) {
        return Future.succeededFuture(Optional.ofNullable(rvmAgentExporter));
    }

    @Override
    public Future<RVMAgentCommandResponse> findRVMAgentCommandResponseById(String agentId, String commandId) {
        RVMAgent rvmAgent = rvmAgentMap.get(agentId);
        KafkaRVMAgentCommandResponse returnKafkaRvmAgentCommandResponse = rvmAgent.getCommandResultMap().get(commandId);
        RVMAgentCommandResponse returnRvmAgentCommandResponse;
        if (returnKafkaRvmAgentCommandResponse == null) {
            returnRvmAgentCommandResponse = null;
        }else{
            returnRvmAgentCommandResponse = new RVMAgentCommandResponse(returnKafkaRvmAgentCommandResponse);
        }
        return Future.succeededFuture(returnRvmAgentCommandResponse);
    }

    @Override
    public Future<AddPrometheusCollectorResponse> addPrometheusCollector(PrometheusCollector prometheusCollector) {
        RVMAgent rvmAgent = rvmAgentMap.get(prometheusCollector.getRvmAgentId());
        PrometheusCollector returnPrometheusCollector = rvmAgent.getPrometheusCollectorMap().get(prometheusCollector.getCollectorId());
        return Future.succeededFuture(returnPrometheusCollector.getAddPrometheusCollectorResponse());
    }

    @Override
    public Future<Set<String>> deletePrometheusCollectorById(String agentId, String prometheusCollectorId) {
        return null;
    }

}
