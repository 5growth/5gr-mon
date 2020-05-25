package it.nextworks.nfvmano.configmanager.topics;

import io.vertx.ext.web.RoutingContext;
import it.nextworks.nfvmano.configmanager.topics.model.Topic;
import it.nextworks.nfvmano.configmanager.topics.model.TopicQueryResult;
import it.nextworks.nfvmano.configmanager.utils.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicController {

    private static final Logger log = LoggerFactory.getLogger(TopicController.class);

    private final TopicRepo repo;

    public TopicController(TopicRepo repo) {
        this.repo = repo;
    }

    public void getAllTopics(RoutingContext ctx) {
        log.info("Received call GET all topics.");
        ContextUtils.handleAndRespond(
                ctx,
                () -> repo.findAll().map(TopicQueryResult::new),
                "get all topics",
                "GET all topics"
        );
    }

    public void postTopic(RoutingContext ctx) {
        log.info("Received call POST topic.");
        Topic topic = ctx.get("_parsed");
        log.debug("Topic:\n{}", topic);
        ContextUtils.handleAndRespond(
                ctx,
                () -> repo.save(topic),
                "save topic",
                "POST topic",
                201
        );
    }

    public void deleteTopic(RoutingContext ctx) {
        log.info("Received call DELETE topic.");
        Topic topic = ctx.get("_parsed");
        log.debug("Topic:\n{}", topic);
        ContextUtils.handleAndRespond(
                ctx,
                () -> repo.delete(topic),
                "delete topic",
                "DELETE topic",
                200
        );
    }
}
