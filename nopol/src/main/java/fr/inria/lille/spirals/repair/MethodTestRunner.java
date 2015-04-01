package fr.inria.lille.spirals.repair;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;

/**
 * Created by Thomas Durieux on 12/03/15.
 */
public class MethodTestRunner {
    public static void main(String... args) {
        String[] tests = args[0].split(" ");
        for (int i = 0; i < tests.length; i++) {
            String test = tests[i];
            runTest(test);
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
