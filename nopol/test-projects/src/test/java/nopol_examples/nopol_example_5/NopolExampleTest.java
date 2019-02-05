package nopol_examples.nopol_example_5;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NopolExampleTest {

	@Test
	public void test1(){
		assertEquals(-2, new NopolExample().negate(2));
	}
	@Test
	public void test2(){
		assertEquals(-10, new NopolExample().negate(10));
	}
	@Test
	public void test3(){
		assertEquals(0, new NopolExample().negate(0));
	}
	@Test
	public void test4(){
		assertEquals(-2, new NopolExample().negate(-2));
	}
	@Test
	public void test5(){
		assertEquals(-5, new NopolExample().negate(-5));
	}
	@Test
	public void test6(){
		assertEquals(-1, new NopolExample().negate(1));
	}
}
