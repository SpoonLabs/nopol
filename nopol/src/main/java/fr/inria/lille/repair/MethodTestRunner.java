package fr.inria.lille.repair;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;

import java.util.concurrent.*;

/**
 * Created by Thomas Durieux on 12/03/15.
 */
public class MethodTestRunner {
    public static void main(String... args) {
        String[] tests = args[0].split(" ");
        for (int i = 0; i < tests.length; i++) {
            final String test = tests[i];
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Callable<Object> task = new Callable<Object>() {
                public Object call() {
                    runTest(test);
                    return null;
                }
            };
            Future<Object> future = executor.submit(task);
            try {
                executor.shutdown();
                future.get(4, TimeUnit.MINUTES);
            } catch (Exception ex) {
                // handle other exceptions
                ex.printStackTrace();
            } finally {
                future.cancel(true);
            }
        }
    }

    private static void runTest(String test) {
        try {
            String[] classAndMethod = test.split("#");
            System.out.println(test);
            Request request = Request.method(Class.forName(classAndMethod[0]), classAndMethod[1]);
            JUnitCore junit = new JUnitCore();
            junit.addListener(new TextListener(System.out));
            junit.run(request);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
