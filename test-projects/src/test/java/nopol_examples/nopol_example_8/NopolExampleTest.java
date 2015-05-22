package nopol_examples.nopol_example_8;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NopolExampleTest {

	@Test
	public void test_1() {
		assertTrue(new NopolExample().productLowerThan100(5, 5));
	}
	
	@Test
	public void test_2() {
		assertTrue(new NopolExample().productLowerThan100(2, 50));
	}
	
	@Test
	public void test_3() {
		assertTrue(new NopolExample().productLowerThan100(50, 1));
	}
	
	@Test
	public void test_4() {
		assertTrue(new NopolExample().productLowerThan100(7, 8));
	}
	
	@Test
	public void test_5() {
		assertTrue(new NopolExample().productLowerThan100(9, 9));
	}
	
	@Test
	public void test_6() {
		assertTrue(new NopolExample().productLowerThan100(0, 1));
	}
	
	@Test
	public void test_7() {
		assertFalse(new NopolExample().productLowerThan100(5, 50));
	}
	
	@Test
	public void test_8() {
		assertFalse(new NopolExample().productLowerThan100(50, 50));
	}
	
	@Test
	public void test_9() {
		assertFalse(new NopolExample().productLowerThan100(101, 1));
	}
	
	@Test
	public void test_10() {
		assertFalse(new NopolExample().productLowerThan100(8451, 4897));
	}
	
	public void test_11() {
        assertTrue(new NopolExample().productLowerThan100(50, 1));
    }
    
    public void test_12() {
        assertFalse(new NopolExample().productLowerThan100(50, 3));
    }
}
