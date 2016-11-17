package actor;

import akka.actor.ActorRef;
import fr.inria.lille.repair.common.config.Config;

/**
 * Created by bdanglot on 11/17/16.
 * Immutable object that encapsulate the configuration of the client project to be fix.
 */
public interface ConfigActor {

    Config getConfig();
    byte[] getContent();
    public ActorRef getClient();

}
