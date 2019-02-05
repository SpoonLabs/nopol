package nopol_examples.nopol_example_6;

import junit.framework.TestCase;

public class NopolExampleTest extends TestCase {
	
	public void test1()
	{
		NopolExample program = new NopolExample();
		assertEquals(program.abs(3, 5), 2);
	}
	
	public void test2()
	{
		NopolExample program = new NopolExample();
		assertEquals(program.abs(5, 3), 2);
	}
	
	public void test3()
	{
		NopolExample program = new NopolExample();
		assertEquals(program.abs(4, 0), 4);
	}
	
	public void test4()
	{
		NopolExample program = new NopolExample();
		assertEquals(program.abs(0, 4), 4);
	}
	
	public void test5()
	{
		NopolExample program = new NopolExample();
		assertEquals(program.abs(1, 1), 0);
	}
	
	public void test6()
	{
		NopolExample program = new NopolExample();
		assertEquals(program.abs(0, -3), 3);
	}
}
