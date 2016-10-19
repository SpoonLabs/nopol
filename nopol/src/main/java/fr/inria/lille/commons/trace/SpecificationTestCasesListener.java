package fr.inria.lille.commons.trace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaMap;
import xxl.java.container.classic.MetaSet;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SpecificationTestCasesListener<T> extends TestCasesListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private RuntimeValues<T> runtimeValues;
	private Map<Map<String, Object>, T> consistentInputs;
	private Collection<Map<String, Object>> inconsistentInputs;
	private Set<String> keys;

	public SpecificationTestCasesListener(RuntimeValues<T> runtimeValues) {
		this.runtimeValues = runtimeValues;
		this.consistentInputs = MetaMap.newHashMap();
		this.inconsistentInputs = MetaSet.newHashSet();
		this.keys = null;
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
		if (!runtimeValues().isEmpty()) {
			// logDebug(logger(), "Collecting specifications from " + testCase);
			for (Specification<T> specification : runtimeValues().specifications()) {
				T output = specification.output();
				Map<String, Object> inputs = specification.inputs();
				addToSpec(inputs, output);
			}
		}
	}


	private void addToSpec(Map<String, Object> inputs, T output) {
		T reference = consistentInputs().get(inputs);

		if (consistentInputs().size() == 0) {
			consistentInputs().put(inputs, output);
			this.keys = inputs.keySet();
			return;
		}

		if (reference == null && this.keys.equals(inputs.keySet())) {
			// no such spec so far
			consistentInputs().put(inputs, output);
			return;
		}

		if (output.equals(reference) && this.keys.equals(inputs.keySet())) {
			this.logger.warn("You have redundant tests: same input and same outcome, only one will be used to speed-up synthesis: discarded inputs={} : output={}.", inputs, output);// case 3
			// already there, we don't duplicate the specification line which would slow SMT afterwards
			return;
		}

		// here we have two different outcomes for the same input value
		// it's a logical contradiction
		// we discard it
		if (!this.keys.equals(inputs.keySet())) {
			this.logger.debug("Ill-formed problem: not the input variables in input={} reference={}", inputs, this.keys);// case 2
			this.inconsistentInputs.add(inputs);
		} else {
			this.logger.debug("Same input with different outcome, logical contradiction, discarding the second one discarded={}, current output={}, reference output={}", inputs, output, consistentInputs.get(inputs));// case 1
			this.inconsistentInputs.add(inputs);
		}
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

}
