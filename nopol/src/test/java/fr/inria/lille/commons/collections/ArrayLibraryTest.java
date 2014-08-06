package fr.inria.lille.commons.collections;

import static fr.inria.lille.commons.collections.ArrayLibrary.subarray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class ArrayLibraryTest {

	@Test
	public void subarrayMethod() {
		String[] array = new String[] {"a", "s", "r"};
		String[] subarray;
		
		subarray = subarray(array, 0, 0);
		assertEquals(0, subarray.length);
		
		subarray = subarray(array, 0, 1);
		assertEquals(1, subarray.length);
		assertEquals("a", subarray[0]);
		
		subarray = subarray(array, 0, 2);
		assertEquals(2, subarray.length);
		assertEquals("a", subarray[0]);
		assertEquals("s", subarray[1]);
		
		subarray = subarray(array, 1, 2);
		assertEquals(1, subarray.length);
		assertEquals("s", subarray[0]);
		
		subarray = subarray(array, 0, 3);
		assertEquals(3, subarray.length);
		assertTrue(Arrays.equals(array, subarray));
		
		subarray = subarray(array, 0, 4);
		assertEquals(3, subarray.length);
		assertTrue(Arrays.equals(array, subarray));
	}
}
