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
		if (message instanceof Object[]) {

			Object [] messageAsArray = (Object[]) message;

			if (!(messageAsArray[0] instanceof Config)) {
				unhandled(message);//Unsupported message type
			}

			Config config = ((Config) messageAsArray[0]);
			File tempDirectory = Files.createTempDir();
			UnZiper.unZipIt(((byte[]) messageAsArray[1]), tempDirectory.getAbsolutePath());
			ActorRef client = ((ActorRef) messageAsArray[2]);

			if (config.getSynthesis() == Config.NopolSynthesis.SMT) {
				config.setSolverPath(pathToSolver);
			}

			config.setProjectSourcePath(new String[] {tempDirectory.toString() + "/src/"});
			config.setProjectClasspath(getClasspathFromTargetFolder(new File(tempDirectory.getCanonicalPath() + "/target")));

			List<Patch> patches = Collections.EMPTY_LIST;
			try {
				patches = NoPolLauncher.launch(config.buildSourceFiles(), config.buildClasspath(), config);

				//Internal debug
				if (patches.isEmpty())
					System.out.println("No Patch Found!");
				else
					for (Patch patch : patches)
						System.out.println(patch);

			} catch (NoSuspiciousStatementException | NoFailingTestCaseException noFix) {
				//internal debug
				System.out.println(noFix.toString());
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
