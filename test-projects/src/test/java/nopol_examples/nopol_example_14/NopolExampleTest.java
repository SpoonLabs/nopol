package nopol_examples.nopol_example_14;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NopolExampleTest {

	@Test
	public void test1() {
		NopolExample ex = new NopolExample();
		assertEquals(1, ex.identity(1));
	}

}
