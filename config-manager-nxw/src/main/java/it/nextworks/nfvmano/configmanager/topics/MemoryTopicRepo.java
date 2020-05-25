package it.nextworks.nfvmano.configmanager.topics;

import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.topics.model.Topic;

import java.util.ArrayList;
import java.util.List;

public class MemoryTopicRepo implements TopicRepo {

    private List<Topic> topics = new ArrayList<>();

    @Override
    public Future<List<Topic>> save(Topic topic) {
        topics.add(topic);
        return Future.succeededFuture(topics);
    }

    @Override
    public Future<List<Topic>> findAll() {
        return Future.succeededFuture(topics);
    }

    @Override
    public Future<List<Topic>> delete(Topic topic) {
        topics.remove(topic);
        return Future.succeededFuture(topics);
    }

}
