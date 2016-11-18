package actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static actor.NopolActorTest.actorNopol;
import static org.junit.Assert.assertEquals;

/**
 * Created by bdanglot on 9/20/16.
 */
public class ActorClient extends UntypedActor {

	/**
	 * This class simulate a "client" using NoPol with the actorSystem.
	 */
	private String rootProject;
	private String fullQualifiedNameTest;
	private ActorRef sender;
	private String patchAsString;

	public ActorClient(String rootProject, String fullQualifiedNameTest, String patchAsString) {
		this.rootProject = rootProject;
		this.fullQualifiedNameTest = fullQualifiedNameTest;
		this.patchAsString = patchAsString;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof String) {//The executed test must a string to start the client
			File outputZip = File.createTempFile("test", ".zip");
			Ziper ziper = new Ziper(outputZip.getAbsolutePath(), rootProject);
			ziper.zipIt("src", new File(rootProject + "/src/"));
			ziper.zipIt("target", new File(rootProject + "/target/classes"));
			ziper.zipIt("target", new File(rootProject + "/target/test-classes"));
			ziper.zipIt("target", new File(System.getenv().get("HOME")+"/.m2/repository/junit/junit/4.11/junit-4.11.jar"));
			ziper.close();
			byte [] content = Files.readAllBytes(Paths.get(outputZip.getAbsolutePath()));
			Config config = new Config();
			this.sender = getSender();//keeping the executed test in order to send it the result
			config.setType(StatementType.CONDITIONAL);
			config.setSynthesis(Config.NopolSynthesis.DYNAMOTH);
			config.setProjectTests(new String[]{fullQualifiedNameTest});
			ConfigActor configActor = new ConfigActorImpl(config, content);
			configActor.setClient(getSelf());
			actorNopol.tell(configActor, getSelf());
			//NoPol's response handeling
		} else if (message instanceof List && (!((List) message).isEmpty() && ((List) message).get(0) instanceof Patch)) {
			Patch patch = (Patch) ((List) message).get(0);
			assertEquals(patchAsString, patch.asString());
			sender.tell(patch, getSelf());
		}
	}
}
