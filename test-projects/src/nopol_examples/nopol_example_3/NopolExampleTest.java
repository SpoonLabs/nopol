package nopol_examples.nopol_example_3;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NopolExampleTest {

	@Test
	public void test1() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isOddNumber(3));
	}
	@Test
	public void test2() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isOddNumber(5));
	}
	@Test
	public void test3() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isOddNumber(-1));
	}
	@Test
	public void test4() {
		NopolExample ex = new NopolExample();
		assertTrue(!ex.isOddNumber(2));
	}
	@Test
	public void test5() {
		NopolExample ex = new NopolExample();
		assertTrue(!ex.isOddNumber(-8));
	}
	@Test
	public void test6() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isOddNumber((int)(Math.pow(2,6)+1)));
	}
	@Test
	public void test7() {
		NopolExample ex = new NopolExample();
		assertTrue(!ex.isOddNumber((int)(Math.pow(2, 3))));
	}
	@Test
	public void test8() {
		NopolExample ex = new NopolExample();
		assertTrue(!ex.isOddNumber(100/2));
	}
	@Test
	public void test9() {
		NopolExample ex = new NopolExample();
		assertTrue(!ex.isOddNumber(0));
	}
}
