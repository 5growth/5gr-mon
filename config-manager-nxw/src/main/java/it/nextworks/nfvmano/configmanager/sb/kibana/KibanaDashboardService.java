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

package it.nextworks.nfvmano.configmanager.sb.kibana;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import com.telcaria.kibana.dashboards.Generator;
import com.telcaria.kibana.dashboards.model.KibanaDashboardVisualization;
import com.telcaria.kibana.dashboards.model.KibanaDashboardDescription;
import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.dashboards.DashboardController;
import it.nextworks.nfvmano.configmanager.dashboards.DashboardRepo;
import it.nextworks.nfvmano.configmanager.dashboards.kibana.KibanaDashboardRepo;
import it.nextworks.nfvmano.configmanager.dashboards.model.Dashboard;
import it.nextworks.nfvmano.configmanager.dashboards.model.DashboardDescription;
import it.nextworks.nfvmano.configmanager.dashboards.model.DashboardPanel;
import it.nextworks.nfvmano.configmanager.sb.grafana.GrafanaConnector;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.GrafanaDashboard;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.GrafanaDashboardWrapper;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.Meta;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.Panel;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.PostDashboardResponse;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.Row;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.Target;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.Xaxis;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.Yaxes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Marco Capitani on 25/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class KibanaDashboardService implements KibanaDashboardRepo {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private KibanaConnector connector;

    private KibanaDashboardRepo repo;

    public KibanaDashboardService(KibanaConnector connector, KibanaDashboardRepo repo) {
        this.connector = connector;
        this.repo = repo;
    }


    @Override
    public Future<KibanaDashboardDescription> save(KibanaDashboardDescription description) {
        Generator generator = new Generator();

        List<String> translations = generator.translate(description);
        translations.forEach(object -> {
            //TODO: Handle HTTP response
            connector.postKibanaObject(object);
        });
        return repo.save(description);
    }

    @Override
    public Future<KibanaDashboardDescription> findById(String dashboardId) {
        return repo.findById(dashboardId);
    }

    @Override
    public Future<Set<KibanaDashboardDescription>> findAll() {
        return repo.findAll();
    }

}
