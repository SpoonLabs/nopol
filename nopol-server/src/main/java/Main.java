import actor.NoPolActor;
import counter.Counter;

/**
 * Created by bdanglot on 11/15/16.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        NoPolActor.run();
        Counter.run();
    }
}
