package fr.inria.lille.commons.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.inria.lille.commons.string.StringLibrary;

public class StringLibraryTest {

	@Test
	public void split() {
		String chained = "aja~adja!~ao";
		List<String> splitted;
		splitted = StringLibrary.split(chained, "z");
		assertEquals(1, splitted.size());
		assertEquals(chained, splitted.get(0));
		splitted = StringLibrary.split(chained, "~");
		assertEquals(3, splitted.size());
		assertEquals("aja", splitted.get(0));
		assertEquals("adja!", splitted.get(1));
		assertEquals("ao", splitted.get(2));
	}
	
	@SuppressWarnings({"rawtypes","unchecked"})
	@Test
	public void join() {
		assertEquals("", StringLibrary.join((List) Arrays.asList(), ".."));
		assertEquals("a", StringLibrary.join(Arrays.asList("a"), ".."));
		assertEquals("a..b", StringLibrary.join(Arrays.asList("a", "b"), ".."));
	}
}
