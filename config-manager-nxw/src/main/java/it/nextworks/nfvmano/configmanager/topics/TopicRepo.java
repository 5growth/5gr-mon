package it.nextworks.nfvmano.configmanager.topics;

import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.topics.model.Topic;

import java.util.List;


/**
 * Created by Pedro Bermúdez on 17/02/2020.
 *
 * @author Pedro Bermudez
 */
public interface TopicRepo {

    Future<List<Topic>> save(Topic topic);

    Future<List<Topic>> findAll();

    Future<List<Topic>> delete(Topic topic);

}
