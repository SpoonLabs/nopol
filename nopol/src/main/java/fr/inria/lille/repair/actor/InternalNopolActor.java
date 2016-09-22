package fr.inria.lille.repair.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.nopol.NoPolLauncher;

import java.util.Collections;
import java.util.List;

/**
 * Created by bdanglot on 9/16/16.
 */
public class InternalNopolActor extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof NoPolActor.ConfigActor) {

			Config config = ((NoPolActor.ConfigActor) message).config;
			ActorRef client = ((NoPolActor.ConfigActor) message).client;

			if (config.getSynthesis() == Config.NopolSynthesis.SMT) {
				config.setSolverPath(NoPolActor.pathToSolver);
			}

			List<Patch> patches = Collections.EMPTY_LIST;
			try {
				patches = NoPolLauncher.launch(config.buildSourceFiles(), config.buildClasspath(), config);
			} catch (Exception e) {
				throw new RuntimeException("Error launch NoPol", e);
			} finally {
				getSender().tell(NoPolActor.Message.AVAILABLE, getSelf());
			}

			client.tell(patches, ActorRef.noSender());


			//Internal debug
			if (patches.isEmpty())
				System.out.println("No Patch Found!");
			else
				for (Patch patch : patches)
					System.out.println(patch);
		} else
			unhandled(message);//Unsupported message type

	}
}
