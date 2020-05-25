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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class TopicQueryResult {
    @JsonProperty("topic")
    private List<Topic> topic;

    public TopicQueryResult(Topic... topics) {
        this.topic = Arrays.asList(topics);
    }

    public TopicQueryResult(Collection<Topic> topics) {
        this.topic = new ArrayList<>(topics);
    }

    public TopicQueryResult topic(List<Topic> topic) {
        this.topic = topic;
        return this;
    }

    public TopicQueryResult addTopicItem(Topic topicItem) {
        if (this.topic == null) {
            this.topic = new ArrayList<>();
        }
        this.topic.add(topicItem);
        return this;
    }

    /**
     * The list of topics matching the query
     *
     * @return topic
     **/
    public List<Topic> getTopic() {
        return topic;
    }

    public void setTopic(List<Topic> topic) {
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
        TopicQueryResult topicQueryResult = (TopicQueryResult) o;
        return Objects.equals(this.topic, topicQueryResult.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic);
    }


    @Override
    public String toString() {

        return "class TopicQueryResult {\n" +
                "    topic: " + toIndentedString(topic) + "\n" +
                "}";
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

