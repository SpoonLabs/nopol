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
	
	@Test
	public void stripEnd() {
		String suffix = "ly";
		assertEquals("", StringLibrary.stripEnd("", suffix));
		assertEquals("l", StringLibrary.stripEnd("l", suffix));
		assertEquals("window", StringLibrary.stripEnd("window", suffix));
		assertEquals("dramatical", StringLibrary.stripEnd("dramatically", suffix));
		assertEquals(suffix, StringLibrary.stripEnd(suffix + suffix, suffix));
	}
	
	@Test
	public void firstSubstringOfSplit() {
		String chained = "c.a.q.f.q.mq.e";
		assertEquals(chained, StringLibrary.firstAfterSplit(chained, "<"));
		assertEquals("c", StringLibrary.firstAfterSplit(chained, "[.]"));
		assertEquals("c.a.", StringLibrary.firstAfterSplit(chained, "q"));
	}
	
	@Test
	public void lastSubstringOfSplit() {
		String chained = "c.a.q.f.q.mq.e";
		assertEquals(chained, StringLibrary.lastAfterSplit(chained, "<"));
		assertEquals("e", StringLibrary.lastAfterSplit(chained, "[.]"));
		assertEquals(".e", StringLibrary.lastAfterSplit(chained, "q"));
	}
}
