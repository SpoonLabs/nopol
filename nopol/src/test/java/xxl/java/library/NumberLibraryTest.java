package xxl.java.library;

import static org.junit.Assert.assertTrue;
import static xxl.java.library.NumberLibrary.bounded;
import static xxl.java.library.NumberLibrary.ifNegative;

import org.junit.Test;

public class NumberLibraryTest {

	@Test
	public void numberWithinBounds() {
		assertTrue(0 == bounded(0, 0, 0));
		assertTrue(1 == bounded(1, 1, 1));
		assertTrue(-1 == bounded(-1, -1, -1));

		assertTrue(-1 == bounded(-1, 1, -1));
		assertTrue(0 == bounded(-1, 1, 0));
		assertTrue(1 == bounded(-1, 1, 1));

		assertTrue(0 == bounded(0, 1, -1));
		assertTrue(-1 == bounded(-1, 1, -2));

		assertTrue(1 == bounded(0, 1, 2));
		assertTrue(-1 == bounded(-3, -1, 0));
	}
	
	@Test
	public void changedIfNegative() {
		assertTrue(2 == ifNegative(2, 0));
		assertTrue(2 == ifNegative(2, 2));
		assertTrue(2 == ifNegative(2, 3));
		assertTrue(2 == ifNegative(2, -1));
		assertTrue(0 == ifNegative(-1, 0));
		assertTrue(0 == ifNegative(0, 2));
	}
}
