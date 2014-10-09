package fr.inria.lille.commons.trace;

import static xxl.java.library.LoggerLibrary.logDebug;
import static xxl.java.library.LoggerLibrary.logWarning;

import java.util.Collection;
import java.util.Map;

import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaMap;
import xxl.java.container.classic.MetaSet;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;

public class SpecificationTestCasesListener<T> extends TestCasesListener {

	public SpecificationTestCasesListener(RuntimeValues<T> runtimeValues) {
		this.runtimeValues = runtimeValues;
		consistentInputs = MetaMap.newHashMap();
		inconsistentInputs = MetaSet.newHashSet();
	}
	
	@Override
	protected void processBeforeRun() {
    	runtimeValues().enable();
    }
    
	@Override
	protected void processTestStarted(TestCase testCase) {
		runtimeValues().reset();
	}
	
	@Override
	protected void processSuccessfulRun(TestCase testCase) {
		if (! runtimeValues().isEmpty()) {
			logDebug(logger(), "Collecting specifications from " + testCase);
			for (Specification<T> specification : runtimeValues().specifications()) {
				T output = specification.output();
				Map<String, Object> inputs = specification.inputs();
				if (consistencyCheck(inputs, output)) {
					consistentInputs().put(inputs, output);
				}
			}
		}
	}
	
	private boolean consistencyCheck(Map<String, Object> inputs, T output) {
		if (! inconsistentInputs().contains(inputs)) {
			T reference = consistentInputs().get(inputs);
			if (reference == null || output.equals(reference)) {
				return true;
			} else {
				consistentInputs().remove(inputs);
				inconsistentInputs().add(inputs);
				logWarning(logger(), "Inconsistent input found when collecting specifications");
			}
		}
		return false;
	}
	
	@Override
    protected void processAfterRun() {
    	runtimeValues().disable();
    }
    
    public Collection<Specification<T>> specifications() {
    	Collection<Specification<T>> specifications = MetaList.newLinkedList();
    	for (Map<String, Object> input : consistentInputs().keySet()) {
			specifications.add(new Specification<T>(input, consistentInputs().get(input)));
    	}
    	return specifications;
    }
    
	protected RuntimeValues<T> runtimeValues() {
    	return runtimeValues;
    }
	
    protected Map<Map<String, Object>, T> consistentInputs() {
    	return consistentInputs;
    }
    
    protected Collection<Map<String, Object>> inconsistentInputs() {
    	return inconsistentInputs;
    }

    private RuntimeValues<T> runtimeValues;
    private Map<Map<String, Object>, T> consistentInputs;
    private Collection<Map<String, Object>> inconsistentInputs;
}
