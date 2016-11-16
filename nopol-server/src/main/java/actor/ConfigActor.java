package actor;

import akka.actor.ActorRef;
import fr.inria.lille.repair.common.config.Config;

import java.io.Serializable;

/**
 * Wrap all information needed to run NoPol in Internal Nodes
 */
public final class ConfigActor implements Serializable {

	private static final long serialVersionUID = -6719179615014953090L;

	final Config config;
	final byte[] content;
	public ActorRef client;

	public ConfigActor(Config config, byte[] content) {
		this.config = config;
		this.content = content;
	}

	public void setClient(ActorRef client) {
		this.client = client;
	}
}
