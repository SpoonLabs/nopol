package fr.inria.lille.spirals.repair.synthesizer;

import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.spirals.repair.expression.Expression;
import fr.inria.lille.spirals.repair.commons.Candidates;
import org.junit.Assert;
import org.junit.Test;
import xxl.java.library.JavaLibrary;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by spirals on 06/03/15.
 */
public class SynthesizerTest {

    @Test
    public void test1() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("test1", new Object[]{true});
        oracle.put("test2", new Object[]{false});
        oracle.put("test3", new Object[]{false});
        oracle.put("test4", new Object[]{false});
        oracle.put("test5", new Object[]{true});
        oracle.put("test9", new Object[]{false});

        test(1, oracle, 12, "index <= 0", "index < 1");
    }

    @Test
    public void test2() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("test1", new Object[]{true});
        oracle.put("test2", new Object[]{false});
        oracle.put("test3", new Object[]{false});
        oracle.put("test4", new Object[]{false});
        oracle.put("test5", new Object[]{true});
        oracle.put("test6", new Object[]{false});
        oracle.put("test7", new Object[]{true});
        oracle.put("test8", new Object[]{false});
        oracle.put("test9", new Object[]{false});

        test(2, oracle, 11, "a < b");
    }

    @Test
    public void test3() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("test1", new Object[]{true});
        oracle.put("test2", new Object[]{true});
        oracle.put("test3", new Object[]{true});
        oracle.put("test4", new Object[]{false});
        oracle.put("test5", new Object[]{false});
        oracle.put("test6", new Object[]{true});
        oracle.put("test7", new Object[]{false});
        oracle.put("test8", new Object[]{false});
        oracle.put("test9", new Object[]{false});

        test(3, oracle, 11, "tmp == 0", "0 == tmp");
    }

    @Test
    public void test4() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("test1", new Object[]{false});
        oracle.put("test2", new Object[]{true});
        oracle.put("test3", new Object[]{true});
        oracle.put("test4", new Object[]{true});
        oracle.put("test5", new Object[]{true});
        oracle.put("test6", new Object[]{true});
        oracle.put("test7", new Object[]{true});
        oracle.put("test8", new Object[]{true});
        oracle.put("test9", new Object[]{true});
        oracle.put("test10", new Object[]{false});
        oracle.put("test11", new Object[]{false});

        test(4, oracle, 27, "1 <= a.length()", "1 != a.length()");
    }

    @Test
    public void test5() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("test1", new Object[]{true});
        oracle.put("test2", new Object[]{true});
        oracle.put("test3", new Object[]{false});
        oracle.put("test4", new Object[]{false});
        oracle.put("test5", new Object[]{false});
        oracle.put("test6", new Object[]{true});

        test(5, oracle, 20, "0 <= a", "1 <= a");
    }

    @Test
    public void test6() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("test1", new Object[]{true});
        oracle.put("test2", new Object[]{false});
        oracle.put("test3", new Object[]{false});
        oracle.put("test4", new Object[]{true});
        oracle.put("test5", new Object[]{false});
        oracle.put("test6", new Object[]{false});

        test(6, oracle, 7, "a < b");
    }


    @Test
    public void test8() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("test_1", new Object[]{true});
        oracle.put("test_2", new Object[]{true});
        oracle.put("test_3", new Object[]{true});
        oracle.put("test_4", new Object[]{true});
        oracle.put("test_5", new Object[]{true});
        oracle.put("test_6", new Object[]{true});
        oracle.put("test_7", new Object[]{false});
        oracle.put("test_8", new Object[]{false});
        oracle.put("test_9", new Object[]{false});
        oracle.put("test_10", new Object[]{false});
        oracle.put("test_11", new Object[]{false});

        test(8, oracle, 12, "(a * b) <= 100", "a <= (100 / b)", "b <= (100 / a)");
    }

    @Test
    public void test12() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("test_1", new Object[]{true});
        oracle.put("test_2", new Object[]{false});
        oracle.put("test_3", new Object[]{true});
        oracle.put("test_4", new Object[]{false});

        test(12, oracle, 4, "(list == null) || (0 == list.size())", "(list == null) || list.isEmpty()");
    }

    private void test(int nopolExampleNumber, Map<String, Object[]> o, int line, String... patch) {
        String executionType = "nopol";
        String pack = executionType + "_examples." + executionType + "_example_" + nopolExampleNumber;
        String className = pack + ".NopolExample";
        String testName = pack + ".NopolExampleTest";
        List<String> tests = new ArrayList<>();

        Map<String, Object[]> oracle = new HashMap<>();

        Iterator<String> it = o.keySet().iterator();
        while (it.hasNext()) {
            String next = it.next();
            oracle.put(testName + "#" + next, o.get(next));
            tests.add(testName + "#" + next);
        }

        String classpath = "../test-projects/target/test-classes:../test-projects/target/classes/:/home/thomas/.m2/repository/junit/junit/4.11/junit-4.11.jar:/home/thomas/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar";
        SourceLocation location = new SourceLocation(className, line);
        File[] files = new File []{new File("../test-projects/src/main/java/"), new File("../test-projects/src/test/java/")};
        Synthesizer synthesizer = new SynthesizerImpl(files, location, JavaLibrary.classpathFrom(classpath), oracle, tests.toArray(new String[0]));
        Candidates expression = synthesizer.run(TimeUnit.MINUTES.toMillis(15));
        check(expression, patch);
    }


    private void check(Candidates expression , String... patch) {
        if(expression == null || expression.isEmpty()) {
            Assert.fail("No patch");
        }
        int position = 0;
        for (int i = 0; i < expression.size(); i++) {
            Expression o =  expression.get(i);

            position++;
            for (int j = 0; j < patch.length; j++) {
                String s = patch[j];
                if (o.toString().equals(s)) {
                    printSynt(o, position);
                    return;
                }
            }
            for (int j = 0; j < o.getAlternatives().size(); j++) {
                Expression expression1 = o.getAlternatives().get(j);
                position++;
                for (int k = 0; k < patch.length; k++) {
                    String s = patch[k];
                    if (expression1.toString().equals(s)) {
                        printSynt(expression1, position);
                        return;
                    }
                }
            }
        }

        Assert.fail("No valid patch in candidates: " + expression);
    }

    private void printSynt(Expression expression, int position) {
        int size  = 1;
        size += expression.getAlternatives().size();
        System.out.println("# candidate: " + size);
        System.out.println("Candidate: " + expression);
    }
}
