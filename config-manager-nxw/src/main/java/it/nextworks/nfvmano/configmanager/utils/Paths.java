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

package it.nextworks.nfvmano.configmanager.utils;

/**
 * Created by Marco Capitani on 01/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class Paths {

    public static class Rest {

        public static final String EXP = "/prom-manager/exporter";
        public static final String ONE_EXP = "/prom-manager/exporter/:expId";

        public static final String ALERT = "/prom-manager/alert";
        public static final String ONE_ALERT = "/prom-manager/alert/:alertId";

        public static final String DASHBOARD = "/prom-manager/dashboard";
        public static final String ONE_DASHBOARD = "/prom-manager/dashboard/:dashId";

        public static final String TOPIC = "/prom-manager/topic";

        public static final String AGENT  = "/prom-manager/agent";
        public static final String ONE_AGENT  = "/prom-manager/agent/:agentId";
        public static final String COMMAND  = "/prom-manager/agent_command";
        public static final String COMMAND_GET  = "/prom-manager/agent_command/:agentId/:commandId";
        public static final String INSTALL_EXPORTER = "/prom-manager/install_exporter";
        public static final String UNINSTALL_EXPORTER = "/prom-manager/uninstall_exporter/:agentId/:exporterId";
        public static final String ADD_PROMETHEUS_COLLECTOR = "/prom-manager/add_prometheus_collector";
        public static final String DELETE_PROMETHEUS_COLLECTOR = "/prom-manager/del_prometheus_collector/:agentId/:prometheusCollectorId";
        public static final String GET_RESOURCES_SCRIPTS = "/resources/scripts/:scriptName";
        public static final String GET_RESOURCES_AGENTS = "/resources/agents/:agentName";
        public static final String GET_RESOURCES_FILES = "/resources/files/*";

        public static final String PUSH_GATEWAY_GET_METRICS = "/metrics/*";

        public static final String PROMETHEUS_SCRAPER  = "/prom-manager/prometheus_scraper";
        public static final String DELETE_PROMETHEUS_SCRAPER  = "/prom-manager/prometheus_scraper/:scraperId";
        public static final String KIBANA_DASHBOARD = "/prom-manager/kibanaDashboard";
        public static final String ONE_KIBANA_DASHBOARD = "/prom-manager/kibanaDashboard/:dashId";
        public static final String ELK_STACK_ALERT = "/prom-manager/elk/alert";
        public static final String ONE_ELK_STACK_ALERT = "/prom-manager/elk/alert/:alertId";

    }
}
