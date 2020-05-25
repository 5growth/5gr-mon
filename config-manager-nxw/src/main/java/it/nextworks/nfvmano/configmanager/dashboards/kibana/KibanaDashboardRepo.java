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

import java.util.Set;

/**
 * Created by Marco Capitani on 25/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public interface KibanaDashboardRepo {

    Future<KibanaDashboardDescription> save(KibanaDashboardDescription dashboard);

    Future<KibanaDashboardDescription> findById(String dashboardId);

    Future<Set<KibanaDashboardDescription>> findAll();
}
