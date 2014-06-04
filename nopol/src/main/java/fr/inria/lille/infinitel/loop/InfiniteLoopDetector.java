package fr.inria.lille.infinitel.loop;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.runner.notification.RunListener;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtWhile;
import spoon.reflect.cu.SourcePosition;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.io.ProjectReference;
import fr.inria.lille.commons.spoon.SpoonClassLoader;
import fr.inria.lille.commons.suite.TestSuiteExecution;

public class InfiniteLoopDetector extends AbstractProcessor<CtWhile> {

	public InfiniteLoopDetector(ProjectReference project, Number threshold) {
		this.project = project;
		this.threshold = threshold.intValue();
		auditors = ListLibrary.newArrayList();
	}

	public ProjectReference project() {
		return project;
	}
	
	public Collection<SourcePosition> detectedLoopsRunning(Collection<String> testClasses, RunListener listener) {
		Map<String, Class<?>> processedClasses = processedClasses();
		TestSuiteExecution.runCasesIn(testClasses, project().classpath(), processedClasses, listener);
		return infiniteLoopLocations();
	}

	private Collection<SourcePosition> infiniteLoopLocations() {
		Collection<SourcePosition> locations = SetLibrary.newHashSet();
		for (IterationsAuditor auditor : auditors()) {
			if (auditor.loopReachedThreshold()) {
				locations.add(auditor.auditedLoopPosition());
			}
		}
		return locations;
	}

	private Map<String, Class<?>> processedClasses() {
		return SpoonClassLoader.allClassesTranformedWith(this, project().sourceFolder());
	}
	
	@Override
	public void process(CtWhile loopStatement) {
		IterationsAuditor auditor = IterationsAuditor.newInstance(loopStatement.getPosition(), threshold());
		auditor.process(loopStatement);
		auditors().add(auditor);
	}
	
	private List<IterationsAuditor> auditors() {
		return auditors;
	}
	
	public int threshold() {
		return threshold;
	}
	
	private int threshold;
	private ProjectReference project;
	private List<IterationsAuditor> auditors;
}