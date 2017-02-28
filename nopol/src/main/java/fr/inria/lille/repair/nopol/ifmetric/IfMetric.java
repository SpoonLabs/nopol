package fr.inria.lille.repair.nopol.ifmetric;

import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.repair.common.finder.TestClassesFinder;
import fr.inria.lille.repair.common.config.NopolContext;
import xxl.java.junit.TestSuiteExecution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;

public class IfMetric {

    private final URL[] classpath;
    private static Set<IfPosition> thenStatementsExecuted = new HashSet<>();
    private static Set<IfPosition> elseStatementsExecuted = new HashSet<>();

    /*
     * IfPosition : line number and class of the if
     * String : testCase name
     * IfBranch : number of branches executed
     */
    private static Map<IfPosition, Map<String, IfBranch>> executedIf = new HashMap<IfPosition, Map<String, IfBranch>>();
    final List<String> modifyClass;

    private final File sourceFolder;
    private static File output1;
    private File output2;
    private NopolContext nopolContext;

    private static FileWriter writer;

    static final String THEN_EXECUTED_CALL = IfMetric.class.getName()
            + ".thenStatementExecuted(";
    static final String ELSE_EXECUTED_CALL = IfMetric.class.getName()
            + ".elseStatementExecuted(";
    static final String RESET_METRIC_CALL = IfMetric.class.getName()
            + ".resetIfMetric()";
    static final String COMPUTE_METRIC_CALL = IfMetric.class.getName()
            + ".computeIfMetric(";

    public IfMetric(NopolContext nopolContext) {
        this.nopolContext = nopolContext;
        this.sourceFolder = nopolContext.getProjectSources()[0];
        this.classpath = nopolContext.getProjectClasspath();
        output1 = new File(sourceFolder.getAbsolutePath() + File.separatorChar + ".." + File.separatorChar + "IfMetricPurAndImpur");
        output2 = new File(sourceFolder.getAbsolutePath() + File.separatorChar + ".." + File.separatorChar + "IfMetricExecutionDuringTest");
        FileWriter writer = null;
        modifyClass = new ArrayList<>();
        try {
            writer = new FileWriter(output1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        IfMetric.writer = writer;
    }

    private static URL[] createUrls(String[] paths) {
        int size = paths.length;
        URL[] urls = new URL[size];
        for (int i = 0; i < size; i += 1) {
            try {
                urls[i] = new File(paths[i]).toURI().toURL();
            } catch (MalformedURLException e) {
                printUsage();
                throw new RuntimeException(e);
            }
        }
        return urls;
    }

    public static void main(String[] args) {

        if (2 != args.length) {
            printUsage();
            return;
        }
        File sourceFolder = new File(args[0]);
        checkArgument(sourceFolder.exists(), "%s: does not exist.",
                sourceFolder);
        checkArgument(sourceFolder.isDirectory(), "%s: is not a directory.",
                sourceFolder);

        System.setProperty("java.class.path",
                System.getProperty("java.class.path") + File.pathSeparatorChar
                        + args[1]);
        String[] paths = args[1].split(Character
                .toString(File.pathSeparatorChar));

        URL[] classpath = IfMetric.createUrls(paths);

        new IfMetric(new NopolContext(new File[]{ sourceFolder }, classpath, null)).run();
    }

    private void run() {
        String[] testClasses = new TestClassesFinder().findIn(classpath, false);
        compute(testClasses);
    }


    private static void writeOutPut(String s) {
        try {
            System.out.println(s);
            if (writer == null) {
                writer = new FileWriter(output1);
            }
            writer.write(s + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compute(String[] testClasses) {
        IfCollectorProcessor collectorProcessor = new IfCollectorProcessor();
        IfCountingInstrumentingProcessor instrumentingProcessor = new IfCountingInstrumentingProcessor(this);
        SpoonedProject project = new SpoonedProject(new File[]{sourceFolder}, this.nopolContext);
        ClassLoader loader = project.processedAndDumpedToClassLoader(modifyClass, asList(collectorProcessor, instrumentingProcessor));

        writeOutPut("ClassName.TestCaseName\t\t\tNbInpurIf\tNbPurIf");

        TestSuiteExecution.runCasesIn(testClasses, loader, this.nopolContext);

        System.out.println("First metric has been compute in : " + output1.getAbsoluteFile());

        try {
            writer = new FileWriter(output2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        computeSecondIfMetric();
        System.out.println("Second metric has been compute in : " + output2.getAbsoluteFile());

    }

    private static void printUsage() {

    }

    public static void thenStatementExecuted(String className, int ifLine) {
        IfPosition ifPos = IfPosition.create(className, ifLine);
        thenStatementsExecuted.add(ifPos);
    }

    public static void elseStatementExecuted(String className, int ifLine) {
        IfPosition ifPos = IfPosition.create(className, ifLine);
        elseStatementsExecuted.add(ifPos);
    }

    public static void resetIfMetric() {
        thenStatementsExecuted.clear();
        elseStatementsExecuted.clear();
    }

    public static void computeIfMetric(String testCaseName) {
        Set<IfPosition> inpur = new HashSet<>();
        Set<IfPosition> thenPur = new HashSet<>();
        Set<IfPosition> elsePur = new HashSet<>();

		/*
         * Compute inpur if
		 */
        for (IfPosition tmp : thenStatementsExecuted) {
            if (elseStatementsExecuted.contains(tmp)) {
                inpur.add(tmp);
                executedIf.get(tmp).put(testCaseName, IfBranch.BOTH);
            }
        }

		/*
		 * Compute pur if
		 */
        for (IfPosition tmp : thenStatementsExecuted) {
            if (!elseStatementsExecuted.contains(tmp)) {
                thenPur.add(tmp);
                executedIf.get(tmp).put(testCaseName, IfBranch.THEN);

            }
        }
        for (IfPosition tmp : elseStatementsExecuted) {
            if (!thenStatementsExecuted.contains(tmp)) {
                elsePur.add(tmp);
                executedIf.get(tmp).put(testCaseName, IfBranch.ELSE);

            }
        }

        writeOutPut((testCaseName + "\t" + inpur.size() + "\t" + (thenPur
                .size() + elsePur.size())));
    }

    public static void computeSecondIfMetric() {
        writeOutPut("If_Position\t\t\tNo_Execution\tOne_Branch\tBoth_Branch\tBoth_Branch_Only_On_Two_TestCases");
        for (IfPosition tmp : executedIf.keySet()) {
            String display = tmp + "\t\t\t";
            if (executedIf.get(tmp).isEmpty()) {
				/*
				 * No_Execution
				 */
                display += "1\t\t0\t\t0\t\t0";
            } else {
                if (!executedIf.get(tmp).containsValue(IfBranch.BOTH) &&
                        (!executedIf.get(tmp).containsValue(IfBranch.THEN) || !executedIf.get(tmp).containsValue(IfBranch.ELSE))) {
					/*
					 * One_Branch
					 */
                    display += "0\t\t1\t\t0\t\t0";
                } else {
					/*
					 * Both_Branch	
					 */
                    display += "0\t\t0\t\t1\t\t";
                    boolean thenInOnTest = false;
                    boolean elseInOnTest = false;
                    for (String testCase : executedIf.get(tmp).keySet()) {
                        if (executedIf.get(tmp).get(testCase).equals(IfBranch.THEN)) {
                            thenInOnTest = true;
                        }
                        if (executedIf.get(tmp).get(testCase).equals(IfBranch.ELSE)) {
                            elseInOnTest = true;
                        }
                    }
                    if (thenInOnTest && elseInOnTest) {
						/*
						 * Both_Branch_Only_On_Two_TestCases
						 */
                        display += "1";
                    } else {
                        display += "0";
                    }
                }
            }
            writeOutPut(display);
        }
    }

    public void closeWriter() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<IfPosition, Map<String, IfBranch>> getExecutedIf() {
        return executedIf;
    }


}
