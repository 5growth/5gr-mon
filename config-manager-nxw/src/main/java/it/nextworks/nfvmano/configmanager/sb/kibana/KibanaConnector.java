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

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Marco Capitani on 22/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class KibanaConnector {

    private static final Logger log = LoggerFactory.getLogger(KibanaConnector.class);

    private WebClient client;

    public KibanaConnector(WebClient client) {
        this.client = client;
    }

    void postKibanaObject(String kibanaJsonObject) {
        log.debug("Kibana object:\n{}", kibanaJsonObject);
        log.debug("Requesting new object to Kibana");

        client.post("/api/kibana/dashboards/import?force=true")
                    .putHeader(HttpHeaderNames.AUTHORIZATION.toString(), "Basic ZWxhc3RpYzpjaGFuZ2VtZQ==")
                    .putHeader("kbn-xsrf", "true")
                    .putHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json")
                    .sendBuffer(Buffer.buffer(kibanaJsonObject), ar -> {
                        //TODO: Handle HTTP response
                        });
    }
}
