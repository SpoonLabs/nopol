package fr.inria.lille.repair.infinitel;

import java.io.File;
import java.net.URL;

public class InfinitelLauncher {

    public static void launch(File[] sourceFile, URL[] classpath, int iterationsThreshold) {
        InfinitelConfiguration.setIterationsThreshold(iterationsThreshold);
        Infinitel.run(sourceFile, classpath);
    }

    public static void launch(File[] sourceFile, URL[] classpath) {
        Infinitel.run(sourceFile, classpath);
    }

}
