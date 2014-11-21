package symbolic_examples.symbolic_example_6;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NopolExampleTest {
	
	@Test
	public void test1()
	{
		NopolExample program = new NopolExample();
		assertEquals(program.abs(3, 5), 2);
	}
	
	@Test
	public void test2()
	{
		NopolExample program = new NopolExample();
		assertEquals(program.abs(5, 3), 2);
	}
	
	@Test
	public void test3()
	{
		NopolExample program = new NopolExample();
		assertEquals(program.abs(4, 0), 4);
	}
	
	@Test
	public void test4()
	{
		NopolExample program = new NopolExample();
		assertEquals(program.abs(0, 4), 4);
	}
	
	@Test
	public void test5()
	{
		NopolExample program = new NopolExample();
		assertEquals(program.abs(1, 1), 0);
	}
	
	@Test
	public void test6()
	{
		NopolExample program = new NopolExample();
		assertEquals(program.abs(0, -3), 3);
	}
}
