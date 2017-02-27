package fr.inria.lille.repair.synthesis;

import fr.inria.lille.repair.TestUtility;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.expression.Expression;
import org.junit.Assert;
import org.junit.Test;
import xxl.java.library.JavaLibrary;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class DynamothCodeGenesisTest {

    @Test
    public void test1() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("test1", new Object[]{true});
        oracle.put("test2", new Object[]{false});
        oracle.put("test3", new Object[]{false});
        oracle.put("test4", new Object[]{false});
        oracle.put("test5", new Object[]{true});
        oracle.put("test9", new Object[]{false});

        NopolContext nopolContext = TestUtility.configForExample("nopol",1);

		test(1, oracle, 12, new String[] {"index <= 0", "index < 1"}, nopolContext);
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

        NopolContext nopolContext = TestUtility.configForExample("nopol",2);

        test(2, oracle, 11, new String[] {"a < b"}, nopolContext);
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

        NopolContext nopolContext = TestUtility.configForExample("nopol",3);

		test(3, oracle, 11, new String[] {"tmp == 0", "0 == tmp"}, nopolContext);
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

        NopolContext nopolContext = TestUtility.configForExample("nopol",4);

        DynamothCodeGenesis dynamothCodeGenesis = createSynthesizer(4, oracle, 27, nopolContext);
        System.out.println("basic: "+ dynamothCodeGenesis.getCollectedExpressions());
        check(dynamothCodeGenesis.getCollectedExpressions(), "initializedVariableShouldBeCollected");
        check(dynamothCodeGenesis.getCollectedExpressions(), "otherInitializedVariableShouldBeCollected");
        check(dynamothCodeGenesis.getValidExpressions(), new String[] {"1 != ((java.lang.String) a).length()", "a.length() != 1"});
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

        NopolContext nopolContext = TestUtility.configForExample("nopol",5);

		test(5, oracle, 20, new String[] {"0 <= a", "1 <= a", "r <= a"}, nopolContext);
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

        NopolContext nopolContext = TestUtility.configForExample("nopol",6);

		test(6, oracle, 7, new String[] {"a < b"}, nopolContext);
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

        NopolContext nopolContext = TestUtility.configForExample("nopol",8);

        test(8, oracle, 12, new String[] {"(b * a) <= 100", "(a * b) <= 100", "a <= (100 / b)", "b <= (100 / a)"}, nopolContext);
    }

    @Test
    public void test12() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("test_1", new Object[]{true});
        oracle.put("test_2", new Object[]{false});
        oracle.put("test_3", new Object[]{true});
        oracle.put("test_4", new Object[]{false});

        NopolContext nopolContext = TestUtility.configForExample("nopol",12);

        nopolContext.setOnlyOneSynthesisResult(false);
        
        DynamothCodeGenesis dynamothCodeGenesis = createSynthesizer(12, oracle, 5, nopolContext);
        System.out.println("basic: "+ dynamothCodeGenesis.getCollectedExpressions());
        assertEquals(13, dynamothCodeGenesis.getCollectedExpressions().size());
        
        check(dynamothCodeGenesis.getCollectedExpressions(), "list");
        
        // other constants of the program
        check(dynamothCodeGenesis.getCollectedExpressions(), "3");

        // other variables
        check(dynamothCodeGenesis.getCollectedExpressions(), "list2");
        
        // method calls
        check(dynamothCodeGenesis.getCollectedExpressions(), "this.foo((java.util.List) list)");
        check(dynamothCodeGenesis.getCollectedExpressions(), "this.foo((java.util.List) list2)");
        
        // the valid patches
        check(dynamothCodeGenesis.getValidExpressions(), "(list == null) || (list.size() == 0)");
        check(dynamothCodeGenesis.getValidExpressions(), "(list == null) || list.isEmpty()");
    }

    @Test
    public void test13() throws InterruptedException {
        Map<String, Object[]> oracle = new HashMap<>();
        oracle.put("test_1", new Object[]{true, true});
        oracle.put("test_2", new Object[]{false});
        oracle.put("test_3", new Object[]{false});

		NopolContext nopolContext = TestUtility.configForExample("nopol",13);
        nopolContext.setOnlyOneSynthesisResult(false);

        DynamothCodeGenesis dynamothCodeGenesis = createSynthesizer(13, oracle, 4, nopolContext);
        System.out.println("basic: "+ dynamothCodeGenesis.getCollectedExpressions());
        //assertEquals(12,dynamothCodeGenesis.getCollectedExpressions().size());

        // the valid patches
        check(dynamothCodeGenesis.getValidExpressions(), "(list == null) || list.isEmpty()");
    }

    private DynamothCodeGenesis createSynthesizer(int nopolExampleNumber, Map<String, Object[]> o, int line, NopolContext nopolContext) {
        String executionType = "nopol";
        String pack = executionType + "_examples." + executionType + "_example_" + nopolExampleNumber;
        String className = pack + ".NopolExample";
        String testName = pack + ".NopolExampleTest";
        List<String> tests = new ArrayList<>();

        Map<String, Object[]> oracle = new HashMap<>();

        for (String next : o.keySet()) {
            oracle.put(testName + "#" + next, o.get(next));
            tests.add(testName + "#" + next);
        }

        nopolContext.setDataCollectionTimeoutInSecondForSynthesis(5);

        String classpath = "../test-projects/target/test-classes"+File.pathSeparatorChar+"../test-projects/target/classes/"+File.pathSeparatorChar+"lib/junit-4.11.jar";
        SourceLocation location = new SourceLocation(className, line);
        File[] files = new File []{new File("../test-projects/src/main/java/"), new File("../test-projects/src/test/java/")};

        DynamothCodeGenesis dynamothCodeGenesis = new DynamothCodeGenesisImpl(files, location, JavaLibrary.classpathFrom(classpath), oracle, tests.toArray(new String[0]),nopolContext);

        dynamothCodeGenesis.run(TimeUnit.MINUTES.toMillis(15));
        return dynamothCodeGenesis;
    }
    
    private DynamothCodeGenesis test(int nopolExampleNumber, Map<String, Object[]> o, int line, String[] patch, NopolContext nopolContext) {
		DynamothCodeGenesis dynamothCodeGenesis = createSynthesizer(nopolExampleNumber, o, line, nopolContext);
        Candidates expressions = dynamothCodeGenesis.getValidExpressions();
        check(expressions, patch);
        return dynamothCodeGenesis;
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
                    return; // found!
                }
            }
            /*for (int j = 0; j < o.getAlternatives().size(); j++) {
                Expression expression1 = o.getAlternatives().get(j);
                position++;
                for (int k = 0; k < patch.length; k++) {
                    String s = patch[k];
                    if (expression1.toString().equals(s)) {
                        return; // found!
                    }
                }
            }*/
        }

        Assert.fail("No valid patch in candidates: " + expression);
    }

}
