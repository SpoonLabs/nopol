package symbolic_examples.symbolic_example_9;

import java.io.ObjectInputStream.GetField;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
		Assert.assertTrue(5==p.f(3));
	}

	@Test
	public void test_g() {
		// example where there are multiple possible runtime values for
		// intermediate variable resg (below)

		// I expect JPF to tell me: 'for test_g to pass, resg in method g should
		// be assigned something < 7'

		NopolExample p = new NopolExample();
		// assertTrue
		Assert.assertTrue(p.g(1) <= 3); // passing assertion
		// assertTrue
		Assert.assertTrue(p.g(3) > 0); // passing assertion
		
		Assert.assertTrue(p.g(3) > 3); // passing assertion
		// assertTrue
		Assert.assertTrue(p.g(3) < 7); // failing assertion
	}

	@Test
	public void test_g_passing() {
		NopolExample p = new NopolExample();
		// assertTrue
		Assert.assertTrue(p.g(1) <= 3); // passing assertion
	}

	@Test
	public void test3_h() {
		// example where the assertions exercise the control flow

		// I expect JPF to tell me: 'for test_h to pass, the second assignment
		// to resg (inside the if) should receive 1

		NopolExample p = new NopolExample();
		// assertEquals
		Assert.assertTrue(2 == p.h(6, -1)); // failing assertion
	}

	@Test
	public void test3_h_passing() {
		NopolExample p = new NopolExample();
		// assertEquals
		Assert.assertTrue(6 == p.h(6, 1)); // passing assertion
	}

	@Test
	public void test3_i() {
		// example where the assertions exercise polymorphism

		// I expect JPF to tell me: 'for test_i to pass, resC2 should receive
		// the value 6

		NopolExample p = new NopolExample();
		// assertEquals
		Assert.assertTrue(12 == p.i(4, new C2())); // failing assertion
	}

	@Test
	public void test3_i_passing() {
		NopolExample p = new NopolExample();
		// assertEquals
		Assert.assertTrue(10 == p.i(4, new C1())); // passing assertion
	}
}
