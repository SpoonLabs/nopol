package fr.inria.lille.commons.trace;

import fr.inria.lille.commons.suite.TestCase;
import fr.inria.lille.commons.suite.TestCasesListener;

public class RuntimeValuesCleanerListener extends TestCasesListener {

	@Override
	protected void processSuccessfulRun(TestCase testCase) {
		IterationRuntimeValues.instance().discardCollectedValues();
	}
	
	@Override
    protected void processFailedRun(TestCase testCase) {
		IterationRuntimeValues.instance().discardCollectedValues();
	}

}
