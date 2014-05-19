package fr.inria.lille.nopol;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.runner.Result;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.processing.AbstractProcessor;
import spoon.processing.ProcessingManager;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;
import spoon.support.QueueProcessingManager;
import spoon.support.reflect.code.CtStatementListImpl;
import fr.inria.lille.nopol.test.junit.JUnitRunner;
import fr.inria.lille.nopol.test.junit.TestClassesFinder;
import fr.inria.lille.nopol.threads.ProvidedClassLoaderThreadFactory;

public class IfMetric {

	private final String[] classpath;
	private static List<IfPosition> thenStatementsExecuted = new ArrayList<>();
	private static List<IfPosition> elseStatementsExecuted = new ArrayList<>();

	private final File sourceFolder;

	private static File output;
	private static FileWriter writer;
	private List<URL> urls;

	private static final String THEN_EXECUTED_CALL = IfMetric.class.getName()
			+ ".thenStatementExecuted(";
	private static final String ELSE_EXECUTED_CALL = IfMetric.class.getName()
			+ ".elseStatementExecuted(";
	private static final String RESET_METRIC_CALL = IfMetric.class.getName()
			+ ".resetIfMetric()";
	private static final String COMPUTE_METRIC_CALL = IfMetric.class.getName()
			+ ".computeIfMetric(";

	public IfMetric(File sourceFolder, String[] paths) {
		this.sourceFolder = sourceFolder;
		this.classpath = paths;
		output = new File(sourceFolder + "/../IfMetric");
		FileWriter writer = null;
		try {
			writer = new FileWriter(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.writer = writer;
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
			writer.write(s + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void compute(String[] testClasses) {

		SpoonClassLoader scl = new SpoonClassLoader();
		ProcessingManager processing = new QueueProcessingManager(
				scl.getFactory());
		IfCountingInstrumentingProcessor processor = new IfCountingInstrumentingProcessor(
				scl.getFactory());
		processing.addProcessor(processor);
		scl.setSourcePath(sourceFolder);
		SpoonCompiler builder;
		try {
			builder = new Launcher().createCompiler(scl.getFactory());
			builder.addInputSource(sourceFolder);
			builder.build();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		writeOutPut("ClassName.TestCaseName\t\t\tNbInpurIf\tNbPurIf");

		processing.process();
		try {

			builder.compileInputSources();

			ExecutorService executor = Executors
					.newSingleThreadExecutor(new ProvidedClassLoaderThreadFactory(
							scl));
			Result result;
			result = executor.submit(new JUnitRunner(testClasses)).get();
			executor.shutdown();

			writer.close();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private static void printUsage() {
		System.out.println("java " + IfMetric.class.getName()
				+ " <source folder> <classpath>");
	}

	public static void thenStatementExecuted(Class clazz, int ifLine) {
		IfPosition ifPos = IfPosition.create(clazz, ifLine);
		thenStatementsExecuted.add(ifPos);
	}

	public static void elseStatementExecuted(Class clazz, int ifLine) {
		IfPosition ifPos = IfPosition.create(clazz, ifLine);
		elseStatementsExecuted.add(ifPos);
	}

	public static void resetIfMetric() {
		thenStatementsExecuted.clear();
		elseStatementsExecuted.clear();
	}

	public static void computeIfMetric(String testCaseName) {
		List<IfPosition> inpur = new ArrayList<>();
		List<IfPosition> thenPur = new ArrayList<>();
		List<IfPosition> elsePur = new ArrayList<>();

		for (IfPosition tmp : thenStatementsExecuted) {
			if (elseStatementsExecuted.contains(tmp) && !inpur.contains(tmp)) {
				inpur.add(tmp);
			}
		}

		for (IfPosition tmp : thenStatementsExecuted) {
			if (!elseStatementsExecuted.contains(tmp)) {
				if (!thenPur.contains(tmp)) {
					thenPur.add(tmp);
				}
			}
		}

		for (IfPosition tmp : elseStatementsExecuted) {
			if (!thenStatementsExecuted.contains(tmp)) {
				if (!elsePur.contains(tmp)) {
					elsePur.add(tmp);
				}
			}
		}

		writeOutPut((testCaseName + "\t" + inpur.size() + "\t" + (thenPur
				.size() + elsePur.size())));
	}

	private class IfCountingInstrumentingProcessor extends
			AbstractProcessor<CtMethod> {
		private Factory factory;

		public IfCountingInstrumentingProcessor(Factory factory) {
			this.factory = factory;
		}

		private void instrumentIfInsideMethod(CtIf elem) {

			System.out.println("###### Before ###### \n" + elem);
			
			if (elem.getThenStatement() != null) {
				StringBuilder snippet = new StringBuilder();
				snippet.append(THEN_EXECUTED_CALL).append("this.getClass()")
						.append(",").append(elem.getPosition().getLine() + ")");
				CtStatement call = factory.Code().createCodeSnippetStatement(
						snippet.toString());
				if (!(elem.getThenStatement() instanceof CtBlock)) {
					CtBlock block = factory.Core().createBlock();	
					call.setParent(block);
					elem.getThenStatement().setParent(block);			
					block.addStatement(call);
					block.addStatement(elem.getThenStatement());
					block.setParent(elem);
					elem.setThenStatement(block);
				} else {
					CtBlock block = (CtBlock) elem.getThenStatement();
					block.insertBegin(call);
				}
			}
			if (elem.getElseStatement() != null) {
				StringBuilder snippet = new StringBuilder();
				snippet.append(ELSE_EXECUTED_CALL).append("this.getClass()")
						.append(",").append(elem.getPosition().getLine() + ")");
				CtStatement call = factory.Code().createCodeSnippetStatement(
						snippet.toString());
				if (!(elem.getElseStatement() instanceof CtBlock)) {
					CtBlock block = factory.Core().createBlock();	
					call.setParent(block);
					elem.getElseStatement().setParent(block);			
					block.addStatement(call);
					block.addStatement(elem.getElseStatement());
					block.setParent(elem);
					elem.setElseStatement(block);
				} else {
					CtBlock block = (CtBlock) elem.getElseStatement();
					block.insertBegin(call);
				}
			} else { // generate else block if the if doesn't have one
				StringBuilder snippet = new StringBuilder();
				snippet.append(ELSE_EXECUTED_CALL).append("this.getClass()")
						.append(",").append(elem.getPosition().getLine() + ")");
				CtStatement call = factory.Code().createCodeSnippetStatement(
						snippet.toString());
				CtBlock block = factory.Core().createBlock();
				block.addStatement(call);
				elem.setElseStatement(block);
			}

			System.out.println("###### After ###### \n" + elem);
		}

		private void instrumentMethod(CtMethod method) {

			System.out.println("###### Before ###### \n" + method);

			if (method.getBody() != null) {
				StringBuilder snippet_compute = new StringBuilder();
				snippet_compute.append(COMPUTE_METRIC_CALL)
						.append("this.getClass().toString()").append("+")
						.append("\"").append(".")
						.append(method.getSimpleName()).append("\")");
				CtStatement call_compute = factory.Code()
						.createCodeSnippetStatement(snippet_compute.toString());

				StringBuilder snippet_reset = new StringBuilder();
				snippet_reset.append(RESET_METRIC_CALL);
				CtStatement call_reset = factory.Code()
						.createCodeSnippetStatement(snippet_reset.toString());

				CtStatementList<CtStatement> list_call = new CtStatementListImpl<>();
				list_call.addStatement(call_compute);
				list_call.addStatement(call_reset);
				/*
				 * Workaround, getLastStatement throw
				 * ArrayIndexOutOfBoundException when the method is empty
				 */
				try {
					CtStatement lastStatement = method.getBody()
							.getLastStatement();
					if (lastStatement instanceof CtReturn) {
						lastStatement.insertBefore(list_call);
					} else {
						lastStatement.insertAfter(list_call);
					}
				} catch (ArrayIndexOutOfBoundsException aeoob) {
					/*
					 * Do nothing because of empty method
					 */
				}
			}

			System.out.println("###### After ###### \n" + method);
		}

		private boolean isTestCase(CtMethod method) {
			boolean isNamedTest = method.getSimpleName().toLowerCase()
					.contains("test"); // to detect TestCase under JUnit 3.x
			boolean hasTestAnnotation = false; // to detect TestCase under JUnit
												// 4.x
			List<CtAnnotation<?>> listAnnotation = method.getAnnotations();
			for (CtAnnotation tmp : listAnnotation) {
				if (tmp.getSignature().equals("@org.junit.Test")) {
					hasTestAnnotation = true;
				}
			}

			return isNamedTest || hasTestAnnotation;
		}

		@Override
		public void process(final CtMethod method) {
			if (method != null) {
				if (isTestCase(method)) {
					instrumentMethod(method);
				} else {
					if (method.getBody() != null) {
						List<CtIf> ifList = method.getBody().getElements(
								new Filter<CtIf>() {

									@Override
									public Class<?> getType() {
										return CtIf.class;
									}

									@Override
									public boolean matches(CtIf arg0) {
										if (!(arg0 instanceof CtIf)) {
											return false;
										}

										return true;
									}
								});
						for (CtIf tmp : ifList) {
							instrumentIfInsideMethod(tmp);
						}
					}

				}

			}

		}

	}

	private static class IfPosition {
		private Class<?> clazz;
		private int position;

		private IfPosition(Class<?> clazz, int position) {
			this.clazz = clazz;
			this.position = position;
		}

		public static IfPosition create(Class clazz, int ifLine) {
			return new IfPosition(clazz, ifLine);
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public int getPosition() {
			return position;
		}

		public String toString() {
			return clazz.getName() + ":" + position;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof IfPosition)) {
				return false;
			}
			return clazz.equals(((IfPosition) (obj)).clazz)
					&& position == ((IfPosition) (obj)).position;
		}

	}
}
