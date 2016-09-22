package fr.inria.lille.repair.nopol.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import fr.inria.lille.repair.actor.NoPolActor;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;

import java.util.List;

import static fr.inria.lille.repair.nopol.actor.NopolActorTest.actorNopol;
import static org.junit.Assert.assertEquals;

/**
 * Created by bdanglot on 9/20/16.
 */
public class ActorClient extends UntypedActor {

	/**
	 * This class simulate a "client" using NoPol with the actorSystem.
	 */

	private String [] sourcePath;
	private String classPath;
	private String fullQualifiedNameTest;
	private String pathToSolver;
	private ActorRef sender;
	private String patchAsString;

	public ActorClient(String[] sourcePath, String classPath, String fullQualifiedNameTest, String pathToSolver, String patchAsString) {
		this.sourcePath = sourcePath;
		this.classPath = classPath;
		this.fullQualifiedNameTest = fullQualifiedNameTest;
		this.pathToSolver = pathToSolver;
		this.patchAsString = patchAsString;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof String) {//The executed  test must a string to start the client
			Config config = new Config();
			this.sender = getSender();//keeping the executed test in order to send it the result
			config.setProjectSourcePath(sourcePath);
			config.setProjectClasspath(classPath);
			config.setType(StatementType.CONDITIONAL);
			config.setSolverPath(pathToSolver);
			config.setProjectTests(new String[]{fullQualifiedNameTest});
			actorNopol.tell(config, getSelf());
			//NoPol's response handeling
		} else if (message instanceof List && (!((List) message).isEmpty() && ((List) message).get(0) instanceof Patch)) {
			Patch patch = (Patch) ((List) message).get(0);
			assertEquals(patchAsString, patch.asString());
			sender.tell(patch, getSelf());
		}
	}
}
