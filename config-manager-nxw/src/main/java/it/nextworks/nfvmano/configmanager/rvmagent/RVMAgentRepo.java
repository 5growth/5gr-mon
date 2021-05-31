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

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface RVMAgentRepo {
    
    Future<RVMAgentCreateResponse> save(RVMAgent rvmAgent);

    Future<Optional<List<RVMAgent>>> findById(String rvmAgentId);

    Future<Optional<RVMAgentCommand>> saveRVMAgentCommand(RVMAgentCommand rvmAgentCommand);

    Future<Optional<RVMAgentExporter>> saveRVMAgentExporter(RVMAgentExporter rvmAgentExporter);

    Future<RVMAgentCommandResponse> findRVMAgentCommandResponseById(String agentId, String commandId);

    Future<AddPrometheusCollectorResponse> addPrometheusCollector(PrometheusCollector prometheusCollector);

    Future<Set<String>> deletePrometheusCollectorById(String agentId, String prometheusCollectorId);

    Future<Optional<List<RVMAgent>>> findAll();

    Future<Set<String>> deleteAgentById(String agentId);

    Future<RVMAgentExporter> deletePrometheusExporterById(String agentId, String prometheusExporterId);
}
