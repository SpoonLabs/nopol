package xxl.java.library;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static xxl.java.library.NumberLibrary.bounded;
import static xxl.java.library.NumberLibrary.ifNegative;
import static xxl.java.library.NumberLibrary.mean;
import static xxl.java.library.NumberLibrary.median;
import static xxl.java.library.NumberLibrary.sumInts;
import static xxl.java.library.NumberLibrary.sumLongs;

import java.util.Collection;

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
	
	@Test
	public void sumOfIntegersOfACollection() {
		Collection<Integer> empty = asList();
		assertTrue(0 == sumInts(empty));
		assertTrue(10 == sumInts(asList(1,2,3,4)));
		assertTrue(-10 == sumInts(asList(-1,-2,-3,-4)));
	}
	
	@Test
	public void sumOfLongsOfACollection() {
		Collection<Long> empty = asList();
		assertTrue(0 == sumLongs(empty));
		assertTrue(10 == sumLongs(asList(1L,2L,3L,4L)));
		assertTrue(-10 == sumLongs(asList(-1L,-2L,-3L,-4L)));
	}
	
	@Test
	public void meanValueOfACollection() {
		Collection<Integer> empty = asList();
		assertTrue(0.0 == mean(empty));
		assertTrue(0.0 == mean(asList(-1, 0, 1)));
		assertTrue(0.0 == mean(asList(-2.2, 2.2)));
	}
	
	@Test
	public void medianValueOfACollection() {
		Collection<Integer> empty = asList();
		assertTrue(0.0 == median(empty));
		assertTrue(92.0 == median(asList(92)));
		assertTrue(96.0 == median(asList(92, 100)));
		assertTrue(3.0 == median(asList(1, 2, 3, 4, 5)));
		assertTrue(3.5 == median(asList(1, 2, 3, 4, 5, 6)));
		assertTrue(21.0 == median(asList(400, 500, 19, 21, 3)));
		assertTrue(20.0 == median(asList(400, 500, 19, 21, 3, 0)));
	}
}
