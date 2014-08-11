package fr.inria.lille.commons.trace;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;
import fr.inria.lille.commons.utils.Function;

public class SpecificationTestCasesListener<T> extends TestCasesListener {

	public SpecificationTestCasesListener(RuntimeValues runtimeValues, Function<Integer, T> outputForEachTrace) {
		this.runtimeValues = runtimeValues;
		this.outputForEachTrace = outputForEachTrace;
		collectedTraces = MapLibrary.newHashMap();
	}
	
	@Override
	protected void processBeforeRun() {
    	runtimeValues().enable();
    }
    
	@Override
	protected void processSuccessfulRun(TestCase testCase) {
		for (Entry<Map<String, Object>, Integer> uniqueTrace : runtimeValues().uniqueTraceSet()) {
			Map<String, Object> runtimeTrace = uniqueTrace.getKey();
			T output = outputForEachTrace().outputFor(uniqueTrace.getValue());
			if (collectedTraces().containsKey(runtimeTrace) && output != specifiedOutput(runtimeTrace)) {
				collectedTraces().remove(runtimeTrace);
			} else {
				Map<String, Object> traceCopy = MapLibrary.copyOf(runtimeTrace);
				collectedTraces().put(traceCopy, output);
			}
		}
	}
	
	@Override
    protected void processTestFinished(TestCase testCase) {
    	runtimeValues().reset();
	}
    
	@Override
    protected void processAfterRun() {
    	runtimeValues().disable();
    }
    
    public Collection<Specification<T>> specifications() {
    	Collection<Specification<T>> specifications = ListLibrary.newArrayList();
    	for (Map<String, Object> input : collectedTraces().keySet()) {
    		specifications.add(new Specification<T>(input, specifiedOutput(input)));
    	}
    	return specifications;
    }
    
    private T specifiedOutput(Map<String, Object> trace) {
    	return collectedTraces().get(trace);
    }
    
    private RuntimeValues runtimeValues() {
    	return runtimeValues;
    }
    
    private Function<Integer, T> outputForEachTrace() {
    	return outputForEachTrace;
    }
    
    private Map<Map<String, Object>, T> collectedTraces() {
    	return collectedTraces;
    }

    private RuntimeValues runtimeValues;
    private Function<Integer, T> outputForEachTrace;
    private Map<Map<String, Object>, T> collectedTraces;
}
