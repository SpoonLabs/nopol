package infinitel_examples.infinitel_example_1;


import infinitel_examples.infinitel_example_1.InfinitelExample;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InfinitelExampleTest {
	
	@Before
	public void initialize() {
		instance = new InfinitelExample();
	}
	
	@Test
	public void test1() {
		Assert.assertEquals(0, instance().loopResult(0));
	}
	
	@Test
	public void test2() {
		Assert.assertEquals(1, instance().loopResult(1));
	}
	
	@Test
	public void test3() {
		Assert.assertEquals(2, instance().loopResult(2));
	}
	
	@Test
	public void test4() {
		Assert.assertEquals(3, instance().loopResult(3));
	}
	
	@Test
	public void testNegative() {
		Assert.assertEquals(4, instance().loopResult(-1));
	}
	
	private static InfinitelExample instance() {
		return instance;
	}
	
	private static InfinitelExample instance;
}
