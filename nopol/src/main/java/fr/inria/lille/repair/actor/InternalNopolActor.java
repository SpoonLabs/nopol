package fr.inria.lille.repair.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import fr.inria.lille.repair.Main;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.nopol.NoPolLauncher;
import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by bdanglot on 9/16/16.
 */
class InternalNopolActor extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Config) {
			List<Patch> patches = Collections.EMPTY_LIST;
			Config config = (Config) message;
			File[] sourceFiles = new File[config.getProjectSourcePath().length];
			for (int i = 0; i < config.getProjectSourcePath().length; i++) {
				String path = config.getProjectSourcePath()[i];
				File sourceFile = FileLibrary.openFrom(path);
				sourceFiles[i] = sourceFile;
			}
			URL[] classpath = JavaLibrary.classpathFrom(config.getProjectClasspath());
			try {
				patches = NoPolLauncher.launch(sourceFiles, classpath, config.getType(), config.getProjectTests());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (patches.isEmpty())
				System.out.println("No Patch Found!");
			else
				for (Patch patch : patches)
					System.out.println(patch);
			getSender().tell(patches, ActorRef.noSender());
			NoPolActor.actorNopol.tell(NoPolActor.Message.AVAILABLE, getSelf());
		}
	}
}
