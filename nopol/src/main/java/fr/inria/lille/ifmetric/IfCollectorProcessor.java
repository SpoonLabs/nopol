package fr.inria.lille.ifmetric;

import java.util.HashMap;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtIf;

public class IfCollectorProcessor extends AbstractProcessor<CtIf>{

	@Override
	public void process(CtIf element) {
		String className = element.getPosition().getCompilationUnit().getMainType().getSimpleName();
		int line = element.getPosition().getLine();
		IfMetric.getExecutedIf().put(IfPosition.create(className, line), new HashMap<String, IfBranch>());
		System.out.println(element);
	}

}
