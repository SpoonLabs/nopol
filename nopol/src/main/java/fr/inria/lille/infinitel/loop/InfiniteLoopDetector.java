package fr.inria.lille.infinitel.loop;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import spoon.processing.AbstractProcessor;
import spoon.processing.Processor;
import spoon.reflect.code.CtWhile;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.spoon.SpoonClassLoader;
import fr.inria.lille.commons.suite.TestSuiteExecution;

public class InfiniteLoopDetector extends AbstractProcessor<CtWhile> {

	public InfiniteLoopDetector(File sourceFolder, Collection<URL> classpath) {
		this.sourceFolder = sourceFolder;
		this.classpath = classpath;
		loopID = 0;
	}

	public File sourceFolder() {
		return sourceFolder;
	}
	
	public Collection<URL> classpath() {
		return classpath;
	}
	
	public Collection<CtWhile> runtimeDetectedLoops(Collection<String> testClasses) {
		Collection<Processor<?>> processors = ListLibrary.newArrayList();
		processors.add(this);
		Map<String, Class<?>> loadedClases = SpoonClassLoader.allClassesTranformedWith(processors, sourceFolder());
		TestSuiteExecution.runCasesIn(testClasses, classpath(), loadedClases);
		Collection<Integer> ids = IterationsAuditor.infiniteLoopIDs();
		return null;
	}
	
	@Override
	public void process(CtWhile loopStatement) {
		IterationsAuditor.attachTo(loopStatement, loopID());
		increaseLoopID();
	}
	
	private int loopID() {
		return loopID;
	}
	
	private int increaseLoopID() {
		return loopID += 1;
	}
	
	private int loopID;
	private File sourceFolder;
	private Collection<URL> classpath;
}