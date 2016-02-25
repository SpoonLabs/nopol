package evo_examples.evo_example_1;

import static org.junit.Assert.*;

import org.junit.Test;

public class EvoExampleTest {

	 @Test
	public void test_evo_example_1() {
		 assertEquals(6,new EvoExample().minZero(6));
	}

	@Test
	public void test_evo_example_2() {
		assertEquals(9,new EvoExample().minZero(9));
	}
	
	//failing test
	@Test
	public void test_evo_example_3() {
		assertEquals(1,new EvoExample().minZero(1));
	}
	
	@Test
	public void test_evo_example_4() {
		assertEquals(0,new EvoExample().minZero(-3));
	}

	@Test
	public void test_evo_example_5() {
		assertEquals(0,new EvoExample().minZero(0));
	}


}
