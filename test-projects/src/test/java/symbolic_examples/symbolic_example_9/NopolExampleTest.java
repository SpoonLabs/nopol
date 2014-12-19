package symbolic_examples.symbolic_example_9;

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
	public void test_f() {
		// example where there is one unique possible runtime value for
		// intermediate variable resf (below)

		// I expect JPF to tell me 'for test_f to pass, resf in method f should
		// be assigned to be 5 '
		// i.e. the synthesized fix should be evaluated to 5 (the synthesis part
		// for which we already have a solution

		NopolExample p = new NopolExample();
		Assert.assertEquals(5, p.f(3));
	}
	
}
