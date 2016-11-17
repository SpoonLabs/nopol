package actor;

import akka.actor.ActorRef;
import fr.inria.lille.repair.common.config.Config;

import java.io.Serializable;

/**
 * Created by bdanglot on 11/17/16.
 */
public class ConfigActorImpl implements ConfigActor, Serializable {

    public ConfigActorImpl(Config config, byte[] content) {
        this.config = config;
        this.content = content;
    }

    public Config getConfig() {
        return config;
    }

    public byte[] getContent() {
        return content;
    }

    public ActorRef getClient() {
        return client;
    }

    public  void setClient(ActorRef client) {
        this.client = client;
    }

    private final Config config;
    private final byte[] content;
    private ActorRef client;

    private static final long serialVersionUID = -8141656618548268847L;

}
