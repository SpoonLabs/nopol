package fr.inria.lille.localization;

import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtIf;
import spoon.reflect.cu.SourcePosition;
import xxl.java.junit.TestCase;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.inria.lille.localization.FaultLocalizerUtils.getTestMethods;

/**
 * Super dumb! simply says that all if conditions are suspicious, even if they are not executed!
 */
public class DumbFaultLocalizerImpl implements FaultLocalizer {

	private NopolContext nopolContext = null;
	public DumbFaultLocalizerImpl(NopolContext nopolContext) {
		this.nopolContext = nopolContext;
	}

	@Override
	public Map<SourceLocation, List<TestResult>> getTestListPerStatement() {
		SpoonedProject spooner = new SpoonedProject(nopolContext.getProjectSources(), nopolContext);
		final List<SourcePosition> l = new ArrayList<>();
		spooner.process(new AbstractProcessor<CtIf>(){
			@Override
			public void process(CtIf ctIf) {
				l.add(ctIf.getCondition().getPosition());
			}
		});

		Map<SourceLocation, List<TestResult>> countPerSourceLocation = new HashMap<>();

		List<TestResult> res = new ArrayList<>();

		for (String testClass : nopolContext.getProjectTests()) {
			try {
				URLClassLoader urlClassLoader = new URLClassLoader(nopolContext.getProjectClasspath(), this.getClass().getClassLoader());
				Class klass = urlClassLoader.loadClass(testClass);

				// does not work, see https://stackoverflow.com/a/29865611
				//for (FrameworkMethod desc : new BlockJUnit4ClassRunner(klass).getChildren()) {

				// so we get the methods ourselves
				// only support basic Junit4
				for (String m : getTestMethods(klass)) {
					res.add(new TestResultImpl(TestCase.from(m), false));
				}
		} catch (Exception e) {
				System.out.println(testClass);
			}
		}
		for(SourcePosition pos : l) {
			SourceLocation loc = new SourceLocation(pos.getCompilationUnit().getMainType().getQualifiedName(), pos.getLine());
			countPerSourceLocation.put(loc, Collections.unmodifiableList(res));
		}
		return countPerSourceLocation;
	}

	@Override
	public List<? extends StatementSourceLocation> getStatements() {
		throw new UnsupportedOperationException();
	}
}
