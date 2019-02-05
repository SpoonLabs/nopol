package actor;

import akka.actor.ActorRef;
import fr.inria.lille.repair.common.config.NopolContext;

import java.io.Serializable;

/**
 * Created by bdanglot on 11/17/16.
 */
public class ConfigActorImpl implements ConfigActor, Serializable {

    private final NopolContext nopolContext;
    private final byte[] content;
    private ActorRef client;

    public ConfigActorImpl(NopolContext nopolContext, byte[] content) {
        this.nopolContext = nopolContext;
        this.content = content;
    }

    public NopolContext getNopolContext() {
        return nopolContext;
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

    private static final long serialVersionUID = -8141656618548268847L;

}
