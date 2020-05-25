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

package it.nextworks.nfvmano.configmanager.topics.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class Topic {

    @JsonProperty("topic")
    private String topic = null;

    public Topic() {

    }

    public Topic topic(String topic) {
        this.topic = topic;
        return this;
    }

    /**
     * the ID of the exporter. It is also the name assigned to the corresponding scrape job
     *
     * @return exporterId
     **/
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Topic topic = (Topic) o;
        return Objects.equals(this.topic, topic.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic);
    }


    @Override
    public String toString() {
        return topic;
    }
}

