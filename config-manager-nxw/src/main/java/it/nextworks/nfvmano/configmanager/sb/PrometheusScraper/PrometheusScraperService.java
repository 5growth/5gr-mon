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

package it.nextworks.nfvmano.configmanager.sb.PrometheusScraper;

import io.vertx.core.Future;
import io.vertx.ext.web.client.WebClient;
import it.nextworks.nfvmano.configmanager.prometheusScraper.PrometheusScraperRepo;
import it.nextworks.nfvmano.configmanager.prometheusScraper.model.PrometheusScraper;
import it.nextworks.nfvmano.configmanager.sb.prometheus.PrometheusConnector;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.CalcGroups;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.CalcLabels;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.CalcRules;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.CalculateRules;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class PrometheusScraperService implements PrometheusScraperRepo {
    static String GROUP_NAME = "performanceMetric";
    private final WebClient webClient;
    private final PrometheusConnector pConnector;
    private PrometheusScraperConnector rvmAgentConnector;
    private PrometheusScraperRepo db;
    private ConcurrentHashMap<String, HashMap<String, String>> concurrentHashMap;
    private ConcurrentHashMap<String, Timer> scraperIdTimerMap = new ConcurrentHashMap<String, Timer>();
    private KafkaProducer kafkaProducer;
    private String promHost;
    private Integer promPort;

    public PrometheusScraperService(PrometheusScraperConnector rvmAgentConnector,
                                    PrometheusScraperRepo db,
                                    ConcurrentHashMap<String,
                                    HashMap<String, String>> concurrentHashMap,
                                    KafkaProducer kafkaProducer,
                                    WebClient webClient,
                                    PrometheusConnector pConnector,
                                    String promHost,
                                    Integer promPort
                                    ) {
        this.rvmAgentConnector = rvmAgentConnector;
        this.db = db;
        this.concurrentHashMap = concurrentHashMap;
        this.kafkaProducer = kafkaProducer;
        this.promHost = promHost;
        this.promPort = promPort;
        this.webClient = webClient;
        this.pConnector = pConnector;
    }

    @Override
    public Future<PrometheusScraper> save(PrometheusScraper prometheusScraper) {
        prometheusScraper.setKafkaProducer(this.kafkaProducer);
        prometheusScraper.setPrometheus(promHost, promPort);
        prometheusScraper.setWebClient(webClient);
        prometheusScraper.setScraperId(UUID.randomUUID().toString());
        if(prometheusScraper.getInterval() != null) {
            Timer timer = new Timer();
            timer.schedule(prometheusScraper, 0, prometheusScraper.getInterval() * 1000);
            scraperIdTimerMap.put(prometheusScraper.getScraperId(), timer);
        }
        Future<PrometheusScraper> future = db.save(prometheusScraper);
        String key = prometheusScraper.getNsid() + "_" +  prometheusScraper.getVnfid();
        HashMap<String, String> metricTopicMap = concurrentHashMap.get(key);
        if (metricTopicMap == null){
            metricTopicMap = new HashMap<String, String>();
            concurrentHashMap.put(key, metricTopicMap);
        }
        concurrentHashMap.get(key).put(prometheusScraper.getPerformanceMetric(), prometheusScraper.getKafkaTopic());
        Future<Void> voidFuture = loadCalculateInPrometheus(prometheusScraper);
        return future;
    }

    @Override
    public Future<Set<String>> deleteById(String scraperId) {
        Future<Optional<PrometheusScraper>> optionalFuture = db.findById(scraperId);
        if(optionalFuture.result().isPresent() == false){
            return Future.succeededFuture(Collections.emptySet());
        }
        PrometheusScraper prometheusScraper = optionalFuture.result().get();
        CalculateRules rules = pConnector.getCalculateRules();
        List<CalcGroups> groups = rules.getGroups();
        CalcGroups cmGroup = getCMGroup(groups);
        List<CalcRules> calcRules = cmGroup.getRules();
        Iterator<CalcRules> iterator = calcRules.iterator();
        while (iterator.hasNext()) {
            CalcRules rule = iterator.next();
            CalcLabels labels = rule.getLabels();
            if (labels.getNsId().equals(prometheusScraper.getNsid())
                    && labels.getVnfdId().equals(prometheusScraper.getVnfid())) {
                iterator.remove();
                break;
            }
        }
        groups.add(cmGroup);
        rules.setGroups(groups);
        pConnector.setCalculateRules(rules);
        String key = prometheusScraper.getNsid() + "_" + prometheusScraper.getVnfid();

        concurrentHashMap.get(key).remove(prometheusScraper.getPerformanceMetric());
        if (concurrentHashMap.get(key).size() == 0){
            concurrentHashMap.remove(key);
        }
        Timer timer =  scraperIdTimerMap.get(scraperId);
        if (timer != null){
            timer.cancel();
            scraperIdTimerMap.remove(scraperId);
        }
        Set<String> set = new HashSet<>();
        set.add(scraperId);
        return Future.succeededFuture(set);
    }

    private CalcGroups getCMGroup(List<CalcGroups> groups) {
        Iterator<CalcGroups> iterator = groups.iterator();
        CalcGroups cmGroup = null;
        while (iterator.hasNext()) {
            CalcGroups g = iterator.next();
            if (g.getName().equals(GROUP_NAME)) {
                cmGroup = g;
                iterator.remove();
                break;
            }
        }
        if (cmGroup == null) {
            cmGroup = new CalcGroups();
            cmGroup.setName(GROUP_NAME);
        }
        return cmGroup;
    }

    static CalcRules convert(PrometheusScraper scraper) {
        CalcLabels calcLabels = new CalcLabels();
        calcLabels.setNsId(scraper.getNsid());
        calcLabels.setVnfdId(scraper.getVnfid());
        CalcRules calcRules = new CalcRules();
        calcRules.setLabels(calcLabels);
        calcRules.setExpr(scraper.getExpression());
        calcRules.setRecord(scraper.getPerformanceMetric());
        return calcRules;
    }

    private void extendGroup(CalcGroups group, CalcRules rule) {
        List<CalcRules> temp = group.getRules();
        temp.add(rule);
        group.setRules(temp);
    }


    @Override
    public Future<Optional<PrometheusScraper>> findById(String scraperId) {
        return db.findById(scraperId);
    }

    private Future<Void> loadCalculateInPrometheus(PrometheusScraper scraper) {
        CalculateRules rules = pConnector.getCalculateRules();
        List<CalcGroups> groups = rules.getGroups();
        CalcGroups cmGroup = getCMGroup(groups);
        CalcRules converted = convert(scraper);
        extendGroup(cmGroup, converted);
        groups.add(cmGroup);
        rules.setGroups(groups);
        pConnector.setCalculateRules(rules);
        return Future.succeededFuture();
    }

}
