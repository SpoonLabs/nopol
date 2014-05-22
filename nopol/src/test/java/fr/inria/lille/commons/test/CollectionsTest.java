package fr.inria.lille.commons.test;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import fr.inria.lille.commons.collections.ArrayLibrary;

public class CollectionsTest {

	@Test
	public void subarray() {
		String[] array = new String[] {"a", "s", "r"};
		String[] subarray;
		
		subarray = ArrayLibrary.subarray(array, 0, 0);
		Assert.assertEquals(0, subarray.length);
		
		subarray = ArrayLibrary.subarray(array, 0, 1);
		Assert.assertEquals(1, subarray.length);
		Assert.assertEquals("a", subarray[0]);
		
		subarray = ArrayLibrary.subarray(array, 0, 2);
		Assert.assertEquals(2, subarray.length);
		Assert.assertEquals("a", subarray[0]);
		Assert.assertEquals("s", subarray[1]);
		
		subarray = ArrayLibrary.subarray(array, 1, 2);
		Assert.assertEquals(1, subarray.length);
		Assert.assertEquals("s", subarray[0]);
		
		subarray = ArrayLibrary.subarray(array, 0, 3);
		Assert.assertEquals(3, subarray.length);
		Assert.assertTrue(Arrays.equals(array, subarray));
		
		subarray = ArrayLibrary.subarray(array, 0, 4);
		Assert.assertEquals(3, subarray.length);
		Assert.assertTrue(Arrays.equals(array, subarray));
	}
	
}
