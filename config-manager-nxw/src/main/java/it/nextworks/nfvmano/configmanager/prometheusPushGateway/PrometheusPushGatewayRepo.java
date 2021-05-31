package it.nextworks.nfvmano.configmanager.prometheusPushGateway;

import io.vertx.core.Future;

import java.util.Optional;

public interface PrometheusPushGatewayRepo {

    Future<Optional<String>> findByUrlSuffix(String urlSuffix);

}
