package fr.inria.lille.commons.trace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxl.java.container.classic.MetaList;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;

import java.util.List;

public class SpecificationTestCasesListener<T> extends TestCasesListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private RuntimeValues<T> runtimeValues;
	public SpecificationTestCasesListener(RuntimeValues<T> runtimeValues) {
		this.runtimeValues = runtimeValues;
	}

	@Override
	protected void processBeforeRun() {
		runtimeValues().enable();
	}

	@Override
	protected void processTestStarted(TestCase testCase) {
		runtimeValues.specificationsForASingleTest().clear();
	}

	@Override
	protected void processSuccessfulRun(TestCase testCase) {
		for (Specification s :runtimeValues.specificationsForASingleTest()) {
			s.setTestCase(testCase);

			// now if the if in a loop there are many times the same spec
			// and this kills the performance of the solver (for instance TSE_CM7 cannot be solved anymore without it)
			// so we don't duplicate them
			// this optimization depends on the correctness of Specification#equals
			if (!specifications.contains(s)) { 			// contains of ArrayList only use equals, not hashCode
				specifications.add(s);
			}
		}
	}


	@Override
	protected void processAfterRun() {
		runtimeValues().disable();
	}

	private final List<Specification<T>> specifications = MetaList.newLinkedList();

	/** returns the specifications over all passing tests (incl those passing thanks to angelic execution modification */
	public List<Specification<T>> specificationsForAllTests() {
		return specifications;
	}

	protected RuntimeValues<T> runtimeValues() {
		return runtimeValues;
	}
}
