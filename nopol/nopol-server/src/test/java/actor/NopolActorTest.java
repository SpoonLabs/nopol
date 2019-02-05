package actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.typesafe.config.ConfigFactory;
import fr.inria.lille.repair.common.patch.Patch;
import org.junit.Test;
import scala.concurrent.Await;
import scala.concurrent.Future;

import static org.junit.Assert.assertEquals;

/**
 * Created by bdanglot on 9/20/16.
 */
public class NopolActorTest {

	private static final String fullQualifiedNameTest = "nopol_examples.nopol_example_1.NopolExampleTest";
	private static final String patchAsString = "index <= 0";

	static ActorSystem system;
	static ActorRef actorNopol;

	@Test
	public void nopolActor() throws Exception {
		com.typesafe.config.Config config = ConfigFactory.load("nopol");
		String ACTOR_SYSTEM_NAME = config.getString("nopol.system.name");
		String ACTOR_NAME = config.getString("nopol.actor.name");
		system = ActorSystem.create(ACTOR_SYSTEM_NAME, config);
		actorNopol = system.actorOf(Props.create(NoPolActor.class, system), ACTOR_NAME);

		ActorRef actorClient = system.actorOf(Props.create(ActorClient.class, "../test-projects",fullQualifiedNameTest, patchAsString), "Client");
		Timeout timeout = new Timeout(30000);
		Future<Object> future = Patterns.ask(actorClient, "start", timeout);
		Patch patch = (Patch) Await.result(future, timeout.duration());
		assertEquals(patchAsString, patch.asString());
	}
}
