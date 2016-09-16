package fr.inria.lille.repair.actor;


import akka.actor.*;
import com.martiansoftware.jsap.JSAPException;
import com.typesafe.config.ConfigFactory;
import fr.inria.lille.repair.Main;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bdanglot on 9/12/16.
 */
public class NoPolActor extends UntypedActor {

    public enum Message {AVAILABLE}

    private Map<ActorRef, Boolean> pool = new HashMap<>();

    public NoPolActor() {
        for (int i = 0; i < 10; i++)
            pool.put(system.actorOf(Props.create(InternalNopolActor.class), "InternalNopolActor_" + i), Boolean.TRUE);
    }

    @Override
    public void onReceive(Object o) {
        if (o instanceof String) {
            boolean taskSent = false;
            for (ActorRef actorRef : this.pool.keySet()) {
                if (this.pool.get(actorRef)) {
                    taskSent = true;
                    actorRef.tell(o, getSender());
                    this.pool.put(actorRef, Boolean.FALSE);
                    break;
                }
            }
            if (!taskSent)
                this.pool.keySet().iterator().next().tell(o, getSender());//send to the first actor of the keyset
        } else if (o instanceof Message) {
            switch ((Message) o) {
                case AVAILABLE:
                    this.pool.put(getSender(), Boolean.TRUE);
                    break;
                default:
                    //do not know
            }
        }
    }

    static ActorSystem system;
    static ActorRef actorNopol;

    public static void main(String[] args) {
        try {
            Main.initJSAP();
        } catch (JSAPException e) {
            e.printStackTrace();
        }
        com.typesafe.config.Config config = ConfigFactory.load("nopol");
        String ACTOR_SYSTEM_NAME = config.getString("nopol.system.name");
        String ACTOR_NAME = config.getString("nopol.actor.name");
        system = ActorSystem.create(ACTOR_SYSTEM_NAME, config);
        actorNopol = system.actorOf(Props.create(NoPolActor.class), ACTOR_NAME);
        System.out.println(actorNopol);
    }

}
