package actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.google.common.io.Files;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.nopol.NoFailingTestCaseException;
import fr.inria.lille.repair.nopol.NoPolLauncher;
import fr.inria.lille.repair.nopol.NoSuspiciousStatementException;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static actor.NoPolActor.pathToSolver;

/**
 * Created by bdanglot on 9/16/16.
 */
public class InternalNopolActor extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ConfigActor) {

			ConfigActor configActor = (ConfigActor) message;

			Config config = configActor.getConfig();
			File tempDirectory = Files.createTempDir();
			UnZiper.unZipIt(configActor.getContent(), tempDirectory.getAbsolutePath());
			ActorRef client = configActor.getClient();

			if (config.getSynthesis() == Config.NopolSynthesis.SMT) {
				config.setSolverPath(pathToSolver);
			}

			config.setProjectSourcePath(new String[] {tempDirectory.toString() + "/src/"});
			config.setProjectClasspath(getClasspathFromTargetFolder(new File(tempDirectory.getCanonicalPath() + "/target")));

			System.out.println(tempDirectory);

			List<Patch> patches = Collections.EMPTY_LIST;
			try {
				patches = NoPolLauncher.launch(config.buildSourceFiles(), config.buildClasspath(), config);
			} catch (NoSuspiciousStatementException | NoFailingTestCaseException noFix) {
				client.tell(noFix, ActorRef.noSender());
			} catch (Exception e) {
				throw new RuntimeException("Error launch NoPol", e);
			} finally {
				getSender().tell(NoPolActor.Message.AVAILABLE, getSelf());
			}
			client.tell(patches, ActorRef.noSender());
		} else
			unhandled(message);//Unsupported message type
	}

	private String getClasspathFromTargetFolder(File folder) {
		File[] files = folder.listFiles();
		String cp = folder.getAbsolutePath() + ":";
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.getName().endsWith("jar")) {
				cp += file.getAbsolutePath() + ":";
			}
		}
		return cp;
	}

}
