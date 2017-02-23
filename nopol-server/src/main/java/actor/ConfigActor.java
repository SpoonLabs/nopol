package actor;

import akka.actor.ActorRef;
import fr.inria.lille.repair.common.config.NopolContext;

import java.io.Serializable;

/**
 * Created by bdanglot on 11/17/16.
 * Immutable object that encapsulate the configuration of the client project to be fix.
 */
public interface ConfigActor extends Serializable {

    NopolContext getNopolContext();
    byte[] getContent();
    ActorRef getClient();
    void setClient(ActorRef client);

}
