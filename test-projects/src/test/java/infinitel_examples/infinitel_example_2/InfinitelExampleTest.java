package infinitel_examples.infinitel_example_2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InfinitelExampleTest {

	@Test
	public void oneIteration() {
		assertEquals(10, new InfinitelExample().oneIterationOrZero(false));
	}
	
	@Test
	public void infiniteLoop() {
		assertEquals(10, new InfinitelExample().oneIterationOrZero(true));
	}
	
}
