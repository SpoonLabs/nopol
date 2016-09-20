package fr.inria.lille.repair.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.nopol.NoPolLauncher;
import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Created by bdanglot on 9/16/16.
 */
class InternalNopolActor extends UntypedActor {

	//TODO
	private final String PATH_TO_SMT_SOLVER = "/home/bdanglot/workspace/nopol/nopol/lib/z3/z3_for_linux";

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Config) {
			List<Patch> patches = Collections.EMPTY_LIST;
			Config config = (Config) message;
			checkSynthesis(config);
			try {
				patches = NoPolLauncher.launch(buildSourceFiles(config) , buildClasspath(config), config.getType(), config.getProjectTests());
			} catch (Exception e) {
				throw new RuntimeException("Error launch NoPol");
			} finally {
				NoPolActor.actorNopol.tell(NoPolActor.Message.AVAILABLE, getSelf());
			}

			//Internal debug
			if (patches.isEmpty())
				System.out.println("No Patch Found!");
			else
				for (Patch patch : patches)
					System.out.println(patch);

			getSender().tell(patches, ActorRef.noSender());
		}
	}

	//TODO move this method in Config Class
	private URL[] buildClasspath(Config config) {
		return JavaLibrary.classpathFrom(config.getProjectClasspath());
	}

	//TODO move this method in Config Class
	private File[] buildSourceFiles(Config config) {
		File[] sourceFiles = new File[config.getProjectSourcePath().length];
		for (int i = 0; i < config.getProjectSourcePath().length; i++) {
			String path = config.getProjectSourcePath()[i];
			File sourceFile = FileLibrary.openFrom(path);
			sourceFiles[i] = sourceFile;
		}
		return sourceFiles;
	}

	private void checkSynthesis(Config config) {
		if (config.getSynthesis() == Config.NopolSynthesis.SMT) {
			config.setSolverPath(PATH_TO_SMT_SOLVER);
		}
	}

}
