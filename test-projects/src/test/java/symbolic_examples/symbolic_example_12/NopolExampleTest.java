package symbolic_examples.symbolic_example_12;

import org.junit.Assert;
import org.junit.Test;

public class NopolExampleTest {

	@Test
	public void test3_i() {
		// example where the assertions exercise polymorphism

		// I expect JPF to tell me: 'for test_i to pass, resC2 should receive
		// the value 6

		NopolExample p = new NopolExample();
		// assertEquals
		Assert.assertEquals(12, p.i(4, new C2())); // failing assertion
	}

	@Test
	public void test3_i_passing() {
		NopolExample p = new NopolExample();
		// assertEquals
		Assert.assertEquals(10, p.i(4, new C1())); // passing assertion
	}
}
