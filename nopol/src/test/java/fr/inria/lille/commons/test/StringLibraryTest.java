package fr.inria.lille.commons.test;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import fr.inria.lille.commons.string.StringLibrary;

public class StringLibraryTest {

	@Test
	public void split() {
		String chained = "aja~adja!~ao";
		List<String> splitted;
		splitted = StringLibrary.split(chained, "z");
		Assert.assertEquals(1, splitted.size());
		Assert.assertEquals(chained, splitted.get(0));
		splitted = StringLibrary.split(chained, "~");
		Assert.assertEquals(3, splitted.size());
		Assert.assertEquals("aja", splitted.get(0));
		Assert.assertEquals("adja!", splitted.get(1));
		Assert.assertEquals("ao", splitted.get(2));
	}
}
