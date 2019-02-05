package infinitel_examples.infinitel_example_3;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InfinitelExampleTest {

	@Test
	public void reachesZeroInOneIteration() {
		assertEquals(0, InfinitelExample.nestedLoops(1));
	}
	
	@Test
	public void reachesZeroInTenIterations() {
		assertEquals(0, InfinitelExample.nestedLoops(10));
	}
	
	@Test
	public void doesNotReachZeroReturnCopy() {
		int a = -10;
		assertEquals(a, InfinitelExample.nestedLoops(a));
	}
	
}
