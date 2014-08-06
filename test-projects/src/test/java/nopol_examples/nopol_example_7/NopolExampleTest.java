package nopol_examples.nopol_example_7;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NopolExampleTest {

	/*
	 * Prime Number :
	 * 2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97
	 */
	
	@Test
	public void test1() {
		NopolExample ex = new NopolExample();		
		assertTrue(ex.isPrime(2));
	}
	
	@Test
	public void test2() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(3));
	}
	
	@Test
	public void test3() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(5));
	}
	
	@Test
	public void test4() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(7));
	}
	@Test
	public void test5() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(11));
	}
	@Test
	public void test6() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(13));
	}
	@Test
	public void test7() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(17));
	}
	@Test
	public void test8() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(19));
	}
	@Test
	public void test9() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(23));
	}
	@Test
	public void test10() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(29));
	}
	@Test
	public void test11() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(31));
	}
	@Test
	public void test12() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(37));
	}
	@Test
	public void test13() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(41));
	}
	@Test
	public void test14() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(43));
	}
	@Test
	public void test15() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(47));
	}
	@Test
	public void test16() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(53));
	}
	@Test
	public void test17() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(59));
	}
	@Test
	public void test18() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(61));
	}
	@Test
	public void test19() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(67));
	}
	@Test
	public void test20() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(71));
	}
	@Test
	public void test21() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(73));
	}
	@Test
	public void test22() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(79));
	}
	@Test
	public void test23() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(83));
	}
	@Test
	public void test24() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(89));
	}
	@Test
	public void test25() {
		NopolExample ex = new NopolExample();
		assertTrue(ex.isPrime(97));
	}
	@Test
	public void test26() {
		NopolExample ex = new NopolExample();
		assertFalse(ex.isPrime(4));
	}
	
	@Test
	public void test27() {
		NopolExample ex = new NopolExample();
		assertFalse(ex.isPrime(42));
	}
	
	@Test
	public void test28() {
		NopolExample ex = new NopolExample();
		assertFalse(ex.isPrime(81));
	}
	
	@Test
	public void test29() {
		NopolExample ex = new NopolExample();
		assertFalse(ex.isPrime(102));
	}
	
	@Test
	public void test30() {
		NopolExample ex = new NopolExample();
		assertFalse(ex.isPrime(225));
	}
	
	@Test
	public void test31() {
		NopolExample ex = new NopolExample();
		assertFalse(ex.isPrime(-231));
	}	
}
