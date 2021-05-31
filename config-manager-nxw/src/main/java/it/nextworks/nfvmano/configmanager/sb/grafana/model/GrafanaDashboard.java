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

package it.nextworks.nfvmano.configmanager.sb.grafana.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class GrafanaDashboard {

    private String id;
    private String uid;
    private List<String> tags;
    private int version;
    private String title;
    private List<Row> rows;
    private String timezone;
    private int schemaVersion;
    private DashboardTime dashboardTime;
    private String dashboardRefreshInterval;


    public GrafanaDashboard() {

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GrafanaDashboard id(String id) {
        this.id = id;
        return this;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public GrafanaDashboard uid(String uid) {
        this.uid = uid;
        return this;
    }

    @JsonProperty("tags")
    public List<String> getTags() {
        return tags;
    }

    @JsonProperty("tags")
    private void setTags(List<String> tags) {
        this.tags = tags;
    }

    public GrafanaDashboard tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    @JsonProperty("version")
    public int getVersion() {
        return version;
    }

    @JsonProperty("version")
    private void setVersion(int version) {
        this.version = version;
    }

    public GrafanaDashboard version(int version) {
        this.version = version;
        return this;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    private void setTitle(String title) {
        this.title = title;
    }

    public GrafanaDashboard title(String title) {
        this.title = title;
        return this;
    }

    @JsonProperty("rows")
    public List<Row> getRows() {
        return rows;
    }

    @JsonProperty("rows")
    private void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public GrafanaDashboard rows(List<Row> rows) {
        this.rows = rows;
        return this;
    }

    @JsonProperty("timezone")
    public String getTimezone() {
        return timezone;
    }

    @JsonProperty("timezone")
    private void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public GrafanaDashboard timezone(String timezone) {
        this.timezone = timezone;
        return this;
    }

    @JsonProperty("schemaVersion")
    public int getSchemaVersion() {
        return schemaVersion;
    }

    @JsonProperty("schemaVersion")
    private void setSchemaVersion(int schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public GrafanaDashboard schemaVersion(int schemaversion) {
        this.schemaVersion = schemaversion;
        return this;
    }

    @JsonProperty("time")
    public DashboardTime getDashboardTime() {
        return dashboardTime;
    }

    @JsonProperty("time")
    public void setDashboardTime(DashboardTime dashboardTime) {
        this.dashboardTime = dashboardTime;
    }

    public GrafanaDashboard time(DashboardTime dashboardTime) {
        this.dashboardTime = dashboardTime;
        return this;
    }

    @JsonProperty("refresh")
    public String getDashboardRefreshInterval() {
        return dashboardRefreshInterval;
    }

    @JsonProperty("refresh")
    public void setDashboardRefreshInterval(String dashboardRefreshInterval) {
        this.dashboardRefreshInterval = dashboardRefreshInterval;
    }

    public GrafanaDashboard dashboardRefreshInterval(String dashboardRefreshInterval) {
        this.dashboardRefreshInterval = dashboardRefreshInterval;
        return this;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GrafanaDashboard that = (GrafanaDashboard) o;

        if (version != that.version) return false;
        if (schemaVersion != that.schemaVersion) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;
        if (tags != null ? !tags.equals(that.tags) : that.tags != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (rows != null ? rows.equals(that.rows) : that.rows != null) return false;
        return timezone != null ? timezone.equals(that.timezone) : that.timezone == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + version;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (rows != null ? rows.hashCode() : 0);
        result = 31 * result + (timezone != null ? timezone.hashCode() : 0);
        result = 31 * result + schemaVersion;
        return result;
    }


}
