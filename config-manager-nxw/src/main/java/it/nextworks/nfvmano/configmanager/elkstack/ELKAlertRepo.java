package it.nextworks.nfvmano.configmanager.elkstack;

import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKAlert;
import it.nextworks.nfvmano.configmanager.elkstack.model.ELKAlertDescription;

import java.util.Set;

public interface ELKAlertRepo {

    Future<ELKAlert> save(ELKAlertDescription description);
    Future<ELKAlert> save(ELKAlert description);

    Future<Set<String>> deleteById(String alertId);
    Future<ELKAlert> findById(String alertId);

}
