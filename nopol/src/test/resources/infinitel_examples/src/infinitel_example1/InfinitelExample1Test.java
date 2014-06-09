package infinitel_example1;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InfinitelExample1Test {
	@Before
	public void initialize() {
		instance = new InfinitelExample1();
	}
	
	@Test
	public void test1() {
		stderr("Executing test1");
		Assert.assertEquals(0, instance().loopResult(0));
	}
	
	@Test
	public void test2() {
		stderr("Executing test2");
		Assert.assertEquals(1, instance().loopResult(1));
	}
	
	@Test
	public void test3() {
		stderr("Executing test3");
		Assert.assertEquals(2, instance().loopResult(2));
	}
	
	@Test
	public void test4() {
		stderr("Executing test4");
		Assert.assertEquals(3, instance().loopResult(3));
	}
	
	@Test
	public void testNegative() {
		stderr("Executing testNegative");
		Assert.assertEquals(4, instance().loopResult(-1));
	}
	
	private static InfinitelExample1 instance() {
		return instance;
	}
	
	private void stderr(String message) {
		System.err.println(message);
	}
	
	private static InfinitelExample1 instance;
}
