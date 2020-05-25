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

package it.nextworks.nfvmano.configmanager.dashboards.kibana;

import com.telcaria.kibana.dashboards.model.KibanaDashboardDescription;
import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.dashboards.DashboardRepo;
import it.nextworks.nfvmano.configmanager.dashboards.model.Dashboard;
import it.nextworks.nfvmano.configmanager.dashboards.model.DashboardDescription;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Marco Capitani on 25/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class MemoryKibanaDashboardRepo implements KibanaDashboardRepo {

    private Map<String, KibanaDashboardDescription> map = new HashMap<>();

    @Override
    public Future<KibanaDashboardDescription> save(KibanaDashboardDescription dashboard) {
        map.put(dashboard.getDashboardId(), dashboard);
        return Future.succeededFuture(dashboard);
    }

    @Override
    public Future<KibanaDashboardDescription> findById(String dashboardId) {
        return Future.succeededFuture(map.get(dashboardId));
    }

    @Override
    public Future<Set<KibanaDashboardDescription>> findAll() {
        return Future.succeededFuture(new HashSet<>(map.values()));
    }
}
