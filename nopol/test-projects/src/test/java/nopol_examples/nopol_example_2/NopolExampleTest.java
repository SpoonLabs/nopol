package nopol_examples.nopol_example_2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NopolExampleTest {

	@Test
	public void test1() {
		NopolExample ex = new NopolExample();
		assertEquals(4, ex.getMax(2, 4));
	}

	@Test
	public void test2() {
		NopolExample ex = new NopolExample();
		assertEquals(4, ex.getMax(4, 2));
	}

	@Test
	public void test3() {
		NopolExample ex = new NopolExample();
		assertEquals(4, ex.getMax(4, 4));
	}

	@Test
	public void test4() {
		NopolExample ex = new NopolExample();
		assertEquals(4, ex.getMax(4, -2));
	}

	@Test
	public void test5() {
		NopolExample ex = new NopolExample();
		assertEquals(4, ex.getMax(-2, 4));
	}

	@Test
	public void test6() {
		NopolExample ex = new NopolExample();
		assertEquals(-2, ex.getMax(-2, -4));
	}

	@Test
	public void test7() {
		NopolExample ex = new NopolExample();
		assertEquals(-2, ex.getMax(-4, -2));
	}

	@Test
	public void test8() {
		NopolExample ex = new NopolExample();
		assertEquals(-2, ex.getMax(-2, -2));
	}

	@Test
	public void test9() {
		NopolExample ex = new NopolExample();
		assertEquals(2, ex.getMax(2, -8));
	}

}
