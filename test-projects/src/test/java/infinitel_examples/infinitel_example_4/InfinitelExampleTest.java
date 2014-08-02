package infinitel_examples.infinitel_example_4;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InfinitelExampleTest {

	@Test
	public void returnExitIn1() {
		assertEquals(18, InfinitelExample.loopWithBreakAndReturn(18));
	}
	
	@Test
	public void returnExitIn4() {
		assertEquals(21, InfinitelExample.loopWithBreakAndReturn(21));
	}
	
	@Test
	public void breakExitIn1() {
		assertEquals(9, InfinitelExample.loopWithBreakAndReturn(9));
	}
	
	@Test
	public void breakExitIn3() {
		assertEquals(9, InfinitelExample.loopWithBreakAndReturn(11));
	}
	
	@Test
	public void normalExitIn0() {
		assertEquals(0, InfinitelExample.loopWithBreakAndReturn(0));
	}
	
	@Test
	public void normalExitIn6() {
		assertEquals(0, InfinitelExample.loopWithBreakAndReturn(6));
	}
}
