package actor;

import akka.actor.ActorRef;
import fr.inria.lille.repair.common.config.Config;

/**
 * Created by bdanglot on 11/17/16.
 */
public class ConfigActorImpl implements ConfigActor {

    public ConfigActorImpl(Config config, byte[] content, ActorRef client) {
        this.config = config;
        this.content = content;
        this.client = client;
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

    private final Config config;
    private final byte[] content;
    private final ActorRef client;

}
