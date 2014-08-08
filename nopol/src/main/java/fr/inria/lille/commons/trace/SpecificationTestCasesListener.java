package fr.inria.lille.commons.trace;

import static fr.inria.lille.commons.utils.LoggerLibrary.logWarning;
import static java.lang.String.format;

import java.util.Collection;
import java.util.Map;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.commons.utils.Function;

public class SpecificationTestCasesListener<T> extends TestCasesListener {

	public SpecificationTestCasesListener(RuntimeValues runtimeValues, Function<Integer, T> outputForEachTrace) {
		this.runtimeValues = runtimeValues;
		this.outputForEachTrace = outputForEachTrace;
		collectedValues = MapLibrary.newHashMap();
	}
	
	protected void processBeforeRun() {
    	runtimeValues().enable();
    }
    
	protected void processSuccessfulRun(TestCase testCase) {
		for (int trace = 0; trace < runtimeValues().numberOfTraces(); trace += 1) {
			Map<String, Object> values = MapLibrary.copyOf(runtimeValues().valuesFor(trace));
			if (! collectedValues().containsKey(values)) {
				T output = outputForEachTrace().outputFor(trace);
				collectedValues().put(values, output);
			} else {
				logWarning(logger, format("Repeated values during execution of %s: %s", testCase.toString(), values.toString()));
			}
		}
	}
	
    protected void processTestFinished(TestCase testCase) {
    	runtimeValues().reset();
	}
    
    protected void processAfterRun() {
    	runtimeValues().disable();
    }
    
    public Collection<Specification<T>> specifications() {
    	Collection<Specification<T>> specifications = ListLibrary.newArrayList();
    	for (Map<String, Object> input : collectedValues().keySet()) {
    		specifications.add(new Specification<T>(input, collectedValues().get(input)));
    	}
    	return specifications;
    }
    
    private RuntimeValues runtimeValues() {
    	return runtimeValues;
    }
    
    private Function<Integer, T> outputForEachTrace() {
    	return outputForEachTrace;
    }
    
    private Map<Map<String, Object>, T> collectedValues() {
    	return collectedValues;
    }

    private RuntimeValues runtimeValues;
    private Function<Integer, T> outputForEachTrace;
    private Map<Map<String, Object>, T> collectedValues;
}
