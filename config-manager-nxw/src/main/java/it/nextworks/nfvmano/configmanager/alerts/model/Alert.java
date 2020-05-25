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

/*
 * Prometheus Manager API
 * The API of the Prometheus Manager.
 *
 * OpenAPI spec version: 0.1
 * Contact: m.capitani@nextworks.it
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package it.nextworks.nfvmano.configmanager.alerts.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.vertx.ext.web.api.validation.ValidationException;
import it.nextworks.nfvmano.configmanager.common.KVP;
import it.nextworks.nfvmano.configmanager.utils.Validated;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Alert
 */
public class Alert implements Validated {

    private static Pattern FOR_RE = Pattern.compile("^(\\d+)([sm])$");

    public Optional<ValidationException> validate() {
        if (alertName == null) {
            return Optional.of(new ValidationException("ALERT: alertname cannot be null"));
        }
        if (query == null) {
            return Optional.of(new ValidationException("ALERT: query cannot be null"));
        }
        if (value == null) {
            return Optional.of(new ValidationException("ALERT: value cannot be null"));
        }
        if (kind == null) {
            return Optional.of(new ValidationException("ALERT: kind cannot be null"));
        }
        if (severity == null) {
            return Optional.of(new ValidationException("ALERT: severity cannot be null"));
        }
        if (target == null) {
            return Optional.of(new ValidationException("ALERT: target cannot be null"));
        }
        for (KVP label : labels) {
            Optional<ValidationException> error = label.validate();
            if (error.isPresent()) {
                String message = error.get().getMessage();
                return Optional.of(new ValidationException("ALERT." + message));
            }
        }
        return Optional.empty();
    }

    @JsonProperty("alertId")
    private String alertId = null;

    @JsonProperty("alertName")
    private String alertName = null;

    @JsonProperty("labels")
    private List<KVP> labels = new ArrayList<>();

    @JsonProperty("query")
    private String query = null;

    @JsonProperty("severity")
    private String severity = null;

    @JsonProperty("for")
    private String forTime = null;

    @JsonProperty("target")
    private URI target = null;

    @JsonProperty("value")
    private Double value = null;

    @JsonProperty("kind")
    private KindEnum kind = null;

    public Alert alertId(String alertId) {
        this.alertId = alertId;
        return this;
    }

    /**
     * the ID assigned to the alert
     *
     * @return alertId
     **/
    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public Alert query(String query) {
        this.query = query;
        return this;
    }

    /**
     * the query whose value should be monitored. See https://prometheus.io/docs/prometheus/latest/querying/basics/ for details
     *
     * @return query
     **/
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Alert value(Double value) {
        this.value = value;
        return this;
    }

    /**
     * the value associated to the threshold
     *
     * @return value
     **/
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Alert kind(KindEnum kind) {
        this.kind = kind;
        return this;
    }

    /**
     * the kind of inequality the query should satisfy related to the value
     *
     * @return kind
     **/
    public KindEnum getKind() {
        return kind;
    }

    public void setKind(KindEnum kind) {
        this.kind = kind;
    }

    public List<KVP> getLabels() {
        return labels;
    }

    public void setLabels(List<KVP> labels) {
        this.labels = labels;
    }

    public URI getTarget() {
        return target;
    }

    public void setTarget(URI target) {
        this.target = target;
    }

    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getForTime() {
        return forTime;
    }

    public int getForSeconds() {
        Matcher match = FOR_RE.matcher(forTime + "\n");
        boolean b = match.find();
        if (!b) {
            throw new IllegalStateException(String.format(
                    "Illegal for value: %s",
                    forTime
            ));
        }
        String num = match.group(1);
        String unit = match.group(2);
        if (unit.equals("m")) {
            return 60 * Integer.valueOf(num);
        } else { // seconds
            return Integer.valueOf(num);
        }
    }

    public void setForSeconds(int forSeconds) {
        if (forSeconds == 0) {
            forTime = "0s";
        } else if (forSeconds % 60 == 0) {
            forTime = forSeconds / 60 + "m";
        } else {
            forTime = forSeconds + "s";
        }
    }

    public void setForMinutes(int forMinutes) {
        if (forMinutes == 0) {
            forTime = "0s";
        } else {
            forTime = forMinutes + "m";
        }
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Alert alert = (Alert) o;
        return (
                Objects.equals(this.alertId, alert.alertId) &&
                this.alertId != null
        ) || (
                this.alertId == null && alert.alertId == null &&
                Objects.equals(this.alertName, alert.alertName) &&
                Objects.equals(this.query, alert.query) &&
                Objects.equals(this.value, alert.value) &&
                Objects.equals(this.kind, alert.kind) &&
                Objects.equals(this.severity, alert.severity) &&
                Objects.equals(this.labels, alert.labels) &&
                Objects.equals(this.target, alert.target)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(alertId, query, value, kind);
    }

    @Override
    public String toString() {

        return "class Alert {\n" +
                "    alertId: " + toIndentedString(alertId) + "\n" +
                "    alertName: " + toIndentedString(alertName) + "\n" +
                "    query: " + toIndentedString(query) + "\n" +
                "    value: " + toIndentedString(value) + "\n" +
                "    kind: " + toIndentedString(kind) + "\n" +
                "    labels: " + toIndentedString(labels) + "\n" +
                "    severity: " + toIndentedString(severity) + "\n" +
                "    target: " + toIndentedString(target) + "\n" +
                "}";
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    /**
     * the kind of inequality the query should satisfy related to the value
     */
    public enum KindEnum {
        G("G", ">"),

        GEQ("GEQ", ">="),

        L("L", "<"),

        LEQ("LEQ", "<="),

        EQ("EQ", "=="),

        NEQ("NEQ", "!=");

        private String value;

        private String operator;

        KindEnum(String value, String operator) {
            this.value = value;
            this.operator = operator;
        }

        @JsonCreator
        public static KindEnum fromValue(String text) {
            for (KindEnum b : KindEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public String operator() {
            return operator;
        }
    }
}
