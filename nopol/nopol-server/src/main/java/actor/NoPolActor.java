package actor;


import akka.actor.*;
import com.typesafe.config.ConfigFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bdanglot on 9/12/16.
 */
public class NoPolActor extends UntypedActor {

	private Map<ActorRef, Boolean> pool = new HashMap<>();

	public NoPolActor(ActorSystem system) {
		for (int i = 0; i < 10; i++)
			this.pool.put(system.actorOf(Props.create(InternalNopolActor.class), "InternalNopolActor_" + i), Boolean.TRUE);
	}

	@Override
	public void onReceive(Object message) {
		if (message instanceof ConfigActor) {
			boolean taskSent = false;
			((ConfigActor)message).setClient(getSender());
			//Looking for free actor
			for (ActorRef actorRef : this.pool.keySet()) {
				if (this.pool.get(actorRef)) {
					taskSent = true;
					actorRef.tell(message, getSelf());
					this.pool.put(actorRef, Boolean.FALSE);
					break;
				}
			}
			if (!taskSent)
				this.pool.keySet().iterator().next().tell(message, getSender());//send to the first actor of the keyset
		} else if (message instanceof Message) {
			switch ((Message) message) {
				case AVAILABLE:
					this.pool.put(getSender(), Boolean.TRUE);
					break;
				default:
					unhandled(message);//Unsupported message type
			}
		} else
			unhandled(message);//Unsupported message type
	}

	enum Message {AVAILABLE}

	static String pathToSolver;

	public static void run() {
		com.typesafe.config.Config config = ConfigFactory.load("nopol");
		pathToSolver = config.getString("nopol.solver.path");
		String ACTOR_SYSTEM_NAME = config.getString("nopol.system.name");
		String ACTOR_NAME = config.getString("nopol.actor.name");
		ActorSystem system = ActorSystem.create(ACTOR_SYSTEM_NAME, config);
		ActorRef actorNopol = system.actorOf(Props.create(NoPolActor.class, system), ACTOR_NAME);
		System.out.println(actorNopol);
	}

	public static void main(String[] args) {
		run();
	}

}
