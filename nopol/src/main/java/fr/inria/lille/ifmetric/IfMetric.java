package fr.inria.lille.ifmetric;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.processing.ProcessingManager;
import spoon.support.QueueProcessingManager;
import fr.inria.lille.nopol.SpoonClassLoader;
import fr.inria.lille.nopol.test.junit.JUnitRunner;
import fr.inria.lille.nopol.test.junit.TestClassesFinder;
import fr.inria.lille.nopol.threads.ProvidedClassLoaderThreadFactory;

public class IfMetric {

	private final String[] classpath;
	private static Set<IfPosition> thenStatementsExecuted = new HashSet<>();
	private static Set<IfPosition> elseStatementsExecuted = new HashSet<>();
	
	/*
	 * IfPosition : line number and class of the if
	 * String : testCase name
	 * IfBranch : number of branches executed
	 */
	private static Map<IfPosition, Map<String, IfBranch>> executedIf = new HashMap<IfPosition, Map<String,IfBranch>>();
	final List<String> modifyClass;
	
	private final File sourceFolder;

	private static File output = new File("/tmp/IfMetric");
	private static FileWriter writer;
	private List<URL> urls;

	static final String THEN_EXECUTED_CALL = IfMetric.class.getName()
			+ ".thenStatementExecuted(";
	static final String ELSE_EXECUTED_CALL = IfMetric.class.getName()
			+ ".elseStatementExecuted(";
	static final String RESET_METRIC_CALL = IfMetric.class.getName()
			+ ".resetIfMetric()";
	static final String COMPUTE_METRIC_CALL = IfMetric.class.getName()
			+ ".computeIfMetric(";

	public IfMetric(File sourceFolder, String[] paths) {
		this.sourceFolder = sourceFolder;
		this.classpath = paths;
		output = new File(sourceFolder + "/../IfMetric");
		FileWriter writer = null;
		modifyClass = new ArrayList<>();
		try {
			writer = new FileWriter(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		IfMetric.writer = writer;
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

		new IfMetric(sourceFolder, paths).run();

	}

	private void run() {

		urls = new ArrayList<URL>();
		for (String path : this.classpath) {
			try {
				urls.add(new File(path).toURI().toURL());
			} catch (MalformedURLException e) {
				printUsage();
				throw new RuntimeException(e);
			}
		}

		String[] testClasses = new TestClassesFinder().findIn(
				urls.toArray(new URL[urls.size()]), false);

		compute(testClasses);

	}

	
	private static void writeOutPut(String s) {
		try {
			System.out.println(s);
			if ( writer == null ){
				writer = new FileWriter(output);
			}
			writer.write(s + "\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void compute(String[] testClasses) {
		SpoonClassLoader scl = new SpoonClassLoader();
		ProcessingManager processing = new QueueProcessingManager(
				scl.getFactory());
		processing.addProcessor(new IfCollectorProcessor());
		processing.addProcessor(new IfCountingInstrumentingProcessor(this, scl.getFactory()));
		scl.setSourcePath(sourceFolder);
		SpoonCompiler builder;
		try {
			builder = new Launcher().createCompiler(scl.getFactory());
			builder.addInputSource(sourceFolder);
			builder.build();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		processing.process();

		
		writeOutPut("ClassName.TestCaseName\t\t\tNbInpurIf\tNbPurIf");
		try {
			for ( String modify : modifyClass ){
				scl.loadClass(modify);
			}
			ExecutorService executor = Executors
					.newSingleThreadExecutor(new ProvidedClassLoaderThreadFactory(
							scl));
			executor.submit(new JUnitRunner(testClasses)).get();
			executor.shutdown();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		
		computeSecondIfMetric();
		System.out.println(executedIf);
		
		
	}

	private static void printUsage() {
		System.out.println("java " + IfMetric.class.getName()
				+ " <source folder> <classpath>");
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
			if (elseStatementsExecuted.contains(tmp) ) {
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

	public static void computeSecondIfMetric(){
		writeOutPut("##################################");
		writeOutPut("If_Position\t\t\tNo_Execution\tOne_Branch\tBoth_Branch\tBoth_Branch_Only_On_Two_TestCases");
		for ( IfPosition tmp : executedIf.keySet() ){
			String display = tmp+"\t\t\t";
			if ( executedIf.get(tmp).isEmpty() ){
				/*
				 * No_Execution
				 */
				display+="x";
			}else {
				display+="\t\t";
				if ( !executedIf.get(tmp).containsValue(IfBranch.BOTH)  &&
					(!executedIf.get(tmp).containsValue(IfBranch.THEN) || !executedIf.get(tmp).containsValue(IfBranch.ELSE))){
					/*
					 * One_Branch
					 */
					display+="x";
				}else{
					/*
					 * Both_Branch	
					 */
					display+="\t\tx";
					boolean thenInOnTest = false;
					boolean elseInOnTest = false;
					for ( String testCase : executedIf.get(tmp).keySet() ){
						if ( executedIf.get(tmp).get(testCase).equals(IfBranch.THEN) ){
							thenInOnTest = true;
						}
						if ( executedIf.get(tmp).get(testCase).equals(IfBranch.ELSE) ){
							elseInOnTest = true;
						}
					}
					if ( thenInOnTest && elseInOnTest ){
						/*
						 * Both_Branch_Only_On_Two_TestCases
						 */
						display+="\t\tx";
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
