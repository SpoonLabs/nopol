package nopol_examples.nopol_example_4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NopolExampleTest {

	@Test
	public void test1(){
	  NopolExample ex = new NopolExample();
	  assertFalse(ex.canBeDividedby3("0"));
	}
	
	@Test
	public void test2(){
	  NopolExample ex = new NopolExample();
	  assertFalse(ex.canBeDividedby3("00000"));
	}
	
	@Test
	public void test3(){
	  NopolExample ex = new NopolExample();
	  assertTrue(ex.canBeDividedby3("33"));
	}
	
	@Test
	public void test4(){
	  NopolExample ex = new NopolExample();
	  assertTrue(ex.canBeDividedby3("333"));
	}
	
	@Test
	public void test5(){
	  NopolExample ex = new NopolExample();
	  assertTrue(ex.canBeDividedby3("8142"));
	}
	
	@Test
	public void test6(){
	  NopolExample ex = new NopolExample();
	  assertTrue(ex.canBeDividedby3("-15339"));
	}
	
	@Test
	public void test7(){
	  NopolExample ex = new NopolExample();
	  assertTrue(ex.canBeDividedby3("-150333333"));
	}
	
	@Test
	public void test8(){
	  NopolExample ex = new NopolExample();
	  assertFalse(ex.canBeDividedby3("-1411111"));
	}
	
	@Test
	public void test9(){
	  NopolExample ex = new NopolExample();
	  assertFalse(ex.canBeDividedby3("-2212"));
	}
	
	@Test
	public void test10(){
	  NopolExample ex = new NopolExample();
	  assertFalse(ex.canBeDividedby3("-"));
	}

	@Test
	public void test11(){
	  NopolExample ex = new NopolExample();
	  assertFalse(ex.canBeDividedby3(""));
	}
}

