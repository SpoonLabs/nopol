package symbolic_examples.symbolic_example_11;

import org.junit.Assert;
import org.junit.Test;

public class NopolExampleTest {


	@Test
	public void test3_h() {
		// example where the assertions exercise the control flow

		// I expect JPF to tell me: 'for test_h to pass, the second assignment
		// to resg (inside the if) should receive 1

		NopolExample p = new NopolExample();
		// assertEquals
		Assert.assertEquals(2, p.h(6, -1)); // failing assertion
	}

	@Test
	public void test4_h() {
		// example where the assertions exercise the control flow

		// I expect JPF to tell me: 'for test_h to pass, the second assignment
		// to resg (inside the if) should receive 1

		NopolExample p = new NopolExample();
		// assertEquals
		Assert.assertEquals(1, p.h(10, -1)); // failing assertion
	}

	@Test
	public void test3_h_passing() {
		NopolExample p = new NopolExample();
		// assertEquals
		Assert.assertEquals(6, p.h(6, 1)); // passing assertion
	}

}
