package plugin.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by bdanglot on 9/12/16.
 */
public class ActorManager {

    public static void createActorSystem(ClassLoader classLoader) {
        akkaConfig = ConfigFactory.load(classLoader, "common");

        actorSystemNopol = akkaConfig.getString("nopol.system.name");
        addressNopol = akkaConfig.getString("nopol.akka.remote.netty.tcp.hostname");
        portNopol = akkaConfig.getString("nopol.akka.remote.netty.tcp.port");
        nameActorNopol = akkaConfig.getString("nopol.actor.name");

        system = ActorSystem.create("PluginActorSystem", akkaConfig, classLoader);
        buildRemoteActor("127.0.0.1", "2553");
    }

    public static void buildRemoteActor(String address, String port) {
        remoteActor = system.actorFor("akka.tcp://" + actorSystemNopol + "@" + address + ":" + port + "/user/" + nameActorNopol);
        System.out.println(remoteActor);
    }

    public static void launchNopol() {
        try {
            final String pathToNopolJar = new File(ActorManager.class.getResource(String.valueOf(Plugin.properties.get("pathToNopolServerJar"))).getPath()).getCanonicalPath();
            final String pathToToolsJar = System.getProperty("java.home") + "/../lib/tools.jar";
            final String fullQualifiedNameMain = String.valueOf(Plugin.properties.get("fullQualifiedOfMainClass"));
            final String cmd = "java -cp " + pathToToolsJar + ":" + pathToNopolJar + " " + fullQualifiedNameMain;
            nopolProcess = Runtime.getRuntime().exec(cmd);
            nopolIsRunning = true;
            remoteActor = system.actorFor("akka.tcp://NopolActorSystem@127.0.0.1:2553/user/NopolActor");
        } catch (Exception ignored) {
            nopolIsRunning = false;
            throw new RuntimeException(ignored);
            //should give to the client the reason that we could not run nopol locally
        }
    }


    public static void stopNopolLocally() {
        if (nopolIsRunning) {
            System.err.println("Stopping nopol locally");
            runNopolLocally = false;
            nopolIsRunning = false;
            nopolProcess.destroy();
            try {
                nopolProcess.waitFor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static ActorSystem system;
    public static ActorRef remoteActor;

    private static String actorSystemNopol;
    public static String addressNopol;
    public static String portNopol;
    public static String nameActorNopol;

    public static Config akkaConfig;

    public static boolean runNopolLocally = true;
    public static boolean nopolIsRunning = false;
    private static Process nopolProcess;
}