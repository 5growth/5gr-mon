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

package it.nextworks.nfvmano.configmanager.sb.prometheus.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class ScrapeConfigs {

    private List<StaticConfigs> staticConfigs;
    private String jobName;
    private String scrapeInterval;
    private boolean honorLabels;
    private boolean honorTimestamps;
    private String metricsPath;

    public ScrapeConfigs() {

    }

    public ScrapeConfigs(String jobName, String scrapeInterval,boolean honorLabels, boolean honorTimestamps, String metricsPath, StaticConfigs... staticConfigs) {
        this.jobName = jobName;
        this.scrapeInterval = scrapeInterval;
        this.honorLabels = honorLabels;
        this.honorTimestamps = honorTimestamps;
        this.metricsPath = metricsPath;
        this.staticConfigs = Arrays.asList(staticConfigs);
    }

    @JsonProperty("static_configs")
    public List<StaticConfigs> getStaticConfigs() {
        return staticConfigs;
    }

    @JsonProperty("static_configs")
    private void setStaticConfigs(List<StaticConfigs> staticConfigs) {
        this.staticConfigs = staticConfigs;
    }

    @JsonProperty("job_name")
    public String getJobName() {
        return jobName;
    }

    @JsonProperty("job_name")
    private void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @JsonProperty("scrape_interval")
    public String getScrapeInterval() {
        return scrapeInterval;
    }

    @JsonProperty("scrape_interval")
    private void setScrapeInterval(String scrapeInterval) {
        this.scrapeInterval = scrapeInterval;
    }

    @JsonProperty("honor_labels")
    public boolean getHonorLabels() {
        return honorLabels;
    }

    @JsonProperty("honor_labels")
    public void setHonorLabels(boolean honorLabels) {
        this.honorLabels = honorLabels;
    }

    @JsonProperty("honor_timestamps")
    public boolean getHonorTimestamps() {
        return honorTimestamps;
    }

    @JsonProperty("honor_timestamps")
    public void setHonorTimestamps(boolean honorTimestamps) {
        this.honorTimestamps = honorTimestamps;
    }

    @JsonProperty("metrics_path")
    public String getMetricsPath() {
        return metricsPath;
    }

    @JsonProperty("metrics_path")
    public void setMetricsPath(String metricsPath) {
        this.metricsPath = metricsPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScrapeConfigs that = (ScrapeConfigs) o;
        return honorLabels == that.honorLabels &&
                honorTimestamps == that.honorTimestamps &&
                Objects.equals(staticConfigs, that.staticConfigs) &&
                Objects.equals(jobName, that.jobName) &&
                Objects.equals(scrapeInterval, that.scrapeInterval) &&
                Objects.equals(metricsPath, that.metricsPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staticConfigs, jobName, scrapeInterval, honorLabels, honorTimestamps, metricsPath);
    }

    @Override
    public String toString() {
        return "ScrapeConfigs{" +
                "staticConfigs=" + staticConfigs +
                ", jobName='" + jobName + '\'' +
                ", scrapeInterval='" + scrapeInterval + '\'' +
                ", honorLabels=" + honorLabels +
                ", honorTimestamps=" + honorTimestamps +
                ", metricsPath='" + metricsPath + '\'' +
                '}';
    }
}
