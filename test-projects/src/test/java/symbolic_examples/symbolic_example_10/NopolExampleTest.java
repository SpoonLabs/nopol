package symbolic_examples.symbolic_example_10;

import org.junit.Assert;
import org.junit.Test;

public class NopolExampleTest {

	// The test driver: added by corina
	public static void main(String[] args) throws Exception{
		String methode;
		if(args.length > 0 ) {
			methode = args[0];
		} else {
			methode = "test_f";
		}
		
		NopolExampleTest.class.getMethod(methode, null).invoke(new NopolExampleTest());
	}

	@Test
	public void test_g() {
		// example where there are multiple possible runtime values for
		// intermediate variable resg (below)

		// I expect JPF to tell me: 'for test_g to pass, resg in method g should
		// be assigned something < 7'

		NopolExample p = new NopolExample();
		// assertTrue
		Assert.assertTrue(p.g(3) < 7); // failing assertion
	}

	@Test
	public void test_g_1() {
		// example where there are multiple possible runtime values for
		// intermediate variable resg (below)

		// I expect JPF to tell me: 'for test_g to pass, resg in method g should
		// be assigned something < 7'

		NopolExample p = new NopolExample();
		// assertTrue
		Assert.assertTrue(p.g(1) < 3); // passing assertion
	}

	@Test
	public void test_g_2() {
		// example where there are multiple possible runtime values for
		// intermediate variable resg (below)

		// I expect JPF to tell me: 'for test_g to pass, resg in method g should
		// be assigned something < 7'

		NopolExample p = new NopolExample();
		// assertTrue
		Assert.assertTrue(p.g(3) > 0); // passing assertion
	}

	@Test
	public void test_g_3() {
		// example where there are multiple possible runtime values for
		// intermediate variable resg (below)

		// I expect JPF to tell me: 'for test_g to pass, resg in method g should
		// be assigned something < 7'

		NopolExample p = new NopolExample();

		Assert.assertTrue(p.g(4) == 8); // passing assertion
	}

	@Test
	public void test_g_4() {
		// example where there are multiple possible runtime values for
		// intermediate variable resg (below)

		// I expect JPF to tell me: 'for test_g to pass, resg in method g should
		// be assigned something < 7'

		NopolExample p = new NopolExample();
		// assertTrue
		Assert.assertTrue(p.g(8) == 16); // failing assertion
	}

	@Test
	public void test_g_passing() {
		NopolExample p = new NopolExample();
		// assertTrue
		Assert.assertTrue(p.g(1) == 2); // passing assertion
	}
}
