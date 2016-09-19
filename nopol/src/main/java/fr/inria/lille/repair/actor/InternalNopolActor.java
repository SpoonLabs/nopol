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
        if (message instanceof String) {
            String[] args = ((String) message).split(" ");
            System.out.println(Arrays.toString(args));
            List<Patch> patches = Collections.EMPTY_LIST;
            if (Main.parseArguments(args)) {
                File[] sourceFiles = new File[Config.INSTANCE.getProjectSourcePath().length];
                for (int i = 0; i < Config.INSTANCE.getProjectSourcePath().length; i++) {
                    String path = Config.INSTANCE.getProjectSourcePath()[i];
                    File sourceFile = FileLibrary.openFrom(path);
                    sourceFiles[i] = sourceFile;
                }
                URL[] classpath = JavaLibrary.classpathFrom(Config.INSTANCE.getProjectClasspath());
                try {
                    patches = NoPolLauncher.launch(sourceFiles, classpath, Config.INSTANCE.getType(), Config.INSTANCE.getProjectTests());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
