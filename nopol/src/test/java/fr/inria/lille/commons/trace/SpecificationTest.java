package fr.inria.lille.commons.trace;

import static org.junit.Assert.assertEquals;
import java.util.Collection;
import org.junit.Test;
import xxl.java.junit.TestCase;

/**
 * 
 * This test class exposes specifications collection issues
 * Probably most are known issues. But it is good to document them here.
 * 
 */

public class SpecificationTest {
	
	/**
	 * In this test, we run three tests to collect specifications from runtime values.
	 * After the first test run, specifications collected is: [collected data: {a=1, b=2}. outcome: true]
	 * After the second test run,specifications collected are: [collected data: {a=1, b=2}. outcome: true, collected data: {a=4, b=5}. outcome: false]
	 * After the third test run,specifications collected is [collected data: {c=3}. outcome: false]
	 * Even {c=3} is inconsistent inputs. 
	 * NOTE: The final specification is not to keep the first two consistent runtime values.
	 * Due to the strategy is to keep the specifications size as minimal as possible. Shall we improve it?
	 */
	@Test
	public void testContinue() {
		
		TestCase testA = TestCase.from("com.example", "testA");
		TestCase testB = TestCase.from("com.example", "testB");
		TestCase testC = TestCase.from("com.example", "testC");
		RuntimeValues<Boolean> runtimeValues = RuntimeValues.newInstance();

		Collection<Specification<Boolean>> specifications;
		
	    //To run testA
		SpecificationTestCasesListener<Boolean> listener = new SpecificationTestCasesListener<Boolean>(runtimeValues);
		listener.processBeforeRun();
		listener.processTestStarted(testA);
		runtimeValues.collectInput("a", 1);
		runtimeValues.collectInput("b", 2);
		runtimeValues.collectOutput(true);
		runtimeValues.collectionEnds();
		listener.processSuccessfulRun(testA);
		specifications = listener.specifications();
		assertEquals("[collected data: {a=1, b=2}. outcome: true]",specifications.toString() );
		assertEquals(1,specifications.size());

        //To run testB
		listener.processTestStarted(testB);
		runtimeValues.collectInput("a",4);
		runtimeValues.collectInput("b",5);
		runtimeValues.collectOutput(false);
		runtimeValues.collectionEnds();
		listener.processSuccessfulRun(testB);
		specifications = listener.specifications();
		assertEquals(2,specifications.size());
		
		//To run testC
		listener.processTestStarted(testC);
		runtimeValues.collectInput("c",3);
		runtimeValues.collectOutput(false);
		runtimeValues.collectionEnds();
		listener.processSuccessfulRun(testB);
		specifications = listener.specifications();

		// those assertions seem to be incorrect
		//assertEquals(1,specifications.size());
		//assertEquals("[collected data: {c=3}. outcome: false]",specifications.toString() );

		assertEquals(2,specifications.size());
		assertEquals("[collected data: {a=1, b=2}. outcome: true, collected data: {a=4, b=5}. outcome: false]",specifications.toString() );

	}

}
