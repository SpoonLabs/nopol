package nopol_examples.nopol_example_5;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NopolExampleTest {

	@Test
	public void test1(){
		NopolExample ex = new NopolExample();
		assertEquals(-2, ex.negate(2));
	}
	@Test
	public void test2(){
		NopolExample ex = new NopolExample();
		assertEquals(-10, ex.negate(10));
	}
	@Test
	public void test3(){
		NopolExample ex = new NopolExample();
		assertEquals(0, ex.negate(0));
	}
	@Test
	public void test4(){
		NopolExample ex = new NopolExample();
		assertEquals(-2, ex.negate(-2));
	}
	@Test
	public void test5(){
		NopolExample ex = new NopolExample();
		assertEquals(-5, ex.negate(-5));
	}

}
