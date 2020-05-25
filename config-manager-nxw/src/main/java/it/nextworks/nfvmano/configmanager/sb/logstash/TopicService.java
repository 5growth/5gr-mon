package it.nextworks.nfvmano.configmanager.sb.logstash;

import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.topics.TopicRepo;
import it.nextworks.nfvmano.configmanager.topics.model.Topic;

import java.util.List;

public class TopicService implements TopicRepo {

    private LogstashConnector lConnector;

    private TopicRepo db;

    public TopicService(LogstashConnector lConnector, TopicRepo db) {
        this.lConnector = lConnector;
        this.db = db;
    }

    @Override
    public Future<List<Topic>> save(Topic topic) {
        Future<List<Topic>> future = db.save(topic);
        return future.compose(newTopics -> {
            Future<Void> aux = lConnector.setTopic(newTopics);
            return aux.map(newTopics);
        });
    }

    @Override
    public Future<List<Topic>> findAll() {
        return db.findAll();
    }

    @Override
    public Future<List<Topic>> delete(Topic topic) {
        Future<List<Topic>> future = db.delete(topic);
        return future.compose(newTopics -> {
            Future<Void> aux = lConnector.setTopic(newTopics);
            return aux.map(newTopics);
        });
    }
}
