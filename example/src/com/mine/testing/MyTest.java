package com.mine.testing;

import com.mine.program.MyProgram;

import junit.framework.TestCase;

public class MyTest extends TestCase{
	
	public void test1()
	{
		MyProgram program = new MyProgram();
		assertEquals(program.abs(3, 5), 2);
	}
	
	public void test2()
	{
		MyProgram program = new MyProgram();
		assertEquals(program.abs(5, 3), 2);
	}
	
	public void test3()
	{
		MyProgram program = new MyProgram();
		assertEquals(program.abs(4, 0), 4);
	}
	
	public void test4()
	{
		MyProgram program = new MyProgram();
		assertEquals(program.abs(0, 4), 4);
	}
	
	public void test5()
	{
		MyProgram program = new MyProgram();
		assertEquals(program.abs(1, 1), 0);
	}
	
	public void test6()
	{
		MyProgram program = new MyProgram();
		assertEquals(program.abs(0, -3), 3);
	}
}
