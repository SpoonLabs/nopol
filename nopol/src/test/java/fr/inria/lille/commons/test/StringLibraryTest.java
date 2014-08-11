package fr.inria.lille.commons.test;

import static fr.inria.lille.commons.string.StringLibrary.asClasspath;
import static fr.inria.lille.commons.string.StringLibrary.firstAfterSplit;
import static fr.inria.lille.commons.string.StringLibrary.join;
import static fr.inria.lille.commons.string.StringLibrary.lastAfterSplit;
import static fr.inria.lille.commons.string.StringLibrary.leftFilled;
import static fr.inria.lille.commons.string.StringLibrary.maximumToStringLength;
import static fr.inria.lille.commons.string.StringLibrary.repeated;
import static fr.inria.lille.commons.string.StringLibrary.reversed;
import static fr.inria.lille.commons.string.StringLibrary.rightFilled;
import static fr.inria.lille.commons.string.StringLibrary.split;
import static fr.inria.lille.commons.string.StringLibrary.stripEnd;
import static fr.inria.lille.commons.string.StringLibrary.toStringList;
import static fr.inria.lille.commons.string.StringLibrary.toStringMap;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class StringLibraryTest {

	@Test
	public void stringSplit() {
		String chained = "aja~adja!~ao";
		List<String> splitted;
		splitted = split(chained, "z");
		assertEquals(1, splitted.size());
		assertEquals(chained, splitted.get(0));
		splitted = split(chained, "~");
		assertEquals(3, splitted.size());
		assertEquals("aja", splitted.get(0));
		assertEquals("adja!", splitted.get(1));
		assertEquals("ao", splitted.get(2));
	}
	
	@SuppressWarnings({"rawtypes","unchecked"})
	@Test
	public void stringJoin() {
		assertEquals("", join((List) asList(), ".."));
		assertEquals("a", join(asList("a"), ".."));
		assertEquals("a..b", join(asList("a", "b"), ".."));
	}
	
	@Test
	public void stripEndFromString() {
		String suffix = "ly";
		assertEquals("", stripEnd("", suffix));
		assertEquals("l", stripEnd("l", suffix));
		assertEquals("window", stripEnd("window", suffix));
		assertEquals("dramatical", stripEnd("dramatically", suffix));
		assertEquals(suffix, stripEnd(suffix + suffix, suffix));
	}
	
	@Test
	public void firstSubstringOfSplit() {
		String chained = "c.a.q.f.q.mq.e";
		assertEquals(chained, firstAfterSplit(chained, "<"));
		assertEquals("c", firstAfterSplit(chained, "[.]"));
		assertEquals("c.a.", firstAfterSplit(chained, "q"));
		assertEquals("some.package.OuterClass", firstAfterSplit("some.package.OuterClass", "[$]"));
		assertEquals("some.package.OuterClass", firstAfterSplit("some.package.OuterClass$InnerClass", "[$]"));
		assertEquals("some.package.OuterClass", firstAfterSplit("some.package.OuterClass$InnerClass$InnerInnerClass", "[$]"));
	}
	
	@Test
	public void lastSubstringOfSplit() {
		String chained = "c.a.q.f.q.mq.e";
		assertEquals(chained, lastAfterSplit(chained, "<"));
		assertEquals("e", lastAfterSplit(chained, "[.]"));
		assertEquals(".e", lastAfterSplit(chained, "q"));
	}
	
	@Test
	public void reversedString() {
		assertEquals("", reversed(""));
		assertEquals("a", reversed("a"));
		assertEquals("noemoc", reversed("comeon"));
	}
	
	@Test
	public void listOfStringsFromList() {
		assertEquals("[a, B, null]", toStringList(asList('a', "B", null)).toString());
		assertEquals("[a, B, ?]", toStringList(asList('a', "B", null), "?").toString());
		assertEquals("[a, B, 1, 2.0]", toStringList(asList('a', "B", 1, 2.0)).toString());
	}
	
	@Test
	public void convertKeysToStringInMap() {
		Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
		map.put(0, false);
		map.put(1, true);
		Map<String, Boolean> stringMap = toStringMap(map);
		assertEquals(2, stringMap.keySet().size());
		assertTrue(stringMap.containsKey("0"));
		assertEquals(false, stringMap.get("0"));
		assertTrue(stringMap.containsKey("1"));
		assertEquals(true, stringMap.get("1"));
	}
	
	@Test
	public void joinClasspaths() throws MalformedURLException {
		URL url = new URL("file:///imaginary/project/folder/src");
		URL url2 = new URL("file:///imaginary/dependency/lib.jar");
		String classpath = asClasspath(new URL[] {url, url2});
		Character classPathSeparator = File.pathSeparatorChar;
		assertEquals("/imaginary/project/folder/src" + classPathSeparator + "/imaginary/dependency/lib.jar", classpath);
	}
	
	@Test
	public void maximumLengthOfElementToString() {
		assertEquals(0, maximumToStringLength(asList(), 0));
		assertEquals(1, maximumToStringLength(asList("a"), 0));
		assertEquals(1, maximumToStringLength(asList("a"), 10));
		assertEquals(1, maximumToStringLength(asList("a", null), 0));
		assertEquals(4, maximumToStringLength(asList("a", null), 4));
	}
	
	@Test
	public void patternRepeated() {
		assertEquals("", repeated('a', 0));
		assertEquals("", repeated('a', -1));
		assertEquals("a", repeated('a', 1));
		assertEquals("aaa", repeated('a', 3));
		assertEquals("", repeated("", 0));
		assertEquals("", repeated("", 3));
		assertEquals("", repeated("ab", 0));
		assertEquals("", repeated("ab", -1));
		assertEquals("ab", repeated("ab", 1));
		assertEquals("ababab", repeated("ab", 3));
	}
	
	@SuppressWarnings({"rawtypes","unchecked"})
	@Test
	public void fillingStringToRight() {
		assertEquals("", leftFilled("", 0, '+'));
		assertEquals("+", leftFilled("", 1, '+'));
		assertEquals("+++", leftFilled("", 3, '+'));
		assertEquals("aaaa", rightFilled("aaaa", 0, '+'));
		assertEquals("aaaa", rightFilled("aaaa", 1, '+'));
		assertEquals("aaaa", rightFilled("aaaa", -1, '+'));
		assertEquals("aaaa", rightFilled("aaaa", 4, '+'));
		assertEquals("aaaa++", rightFilled("aaaa", 6, '+'));
		assertEquals(asList(), rightFilled((List) asList(), 0, '-'));
		assertEquals(asList("a", "bc"), rightFilled(asList("a", "bc"), 0, '+'));
		assertEquals(asList("a", "bc"), rightFilled(asList("a", "bc"), -1, '+'));
		assertEquals(asList("a", "bc"), rightFilled(asList("a", "bc"), 1, '+'));
		assertEquals(asList("a+", "bc"), rightFilled(asList("a", "bc"), 2, '+'));
		assertEquals(asList("a+++", "bc++"), rightFilled(asList("a", "bc"), 4, '+'));
	}

	@SuppressWarnings({"rawtypes","unchecked"})
	@Test
	public void fillingStringToLeft() {
		assertEquals("", leftFilled("", 0, '-'));
		assertEquals("-", leftFilled("", 1, '-'));
		assertEquals("---", leftFilled("", 3, '-'));
		assertEquals("aaaa", leftFilled("aaaa", 0, '-'));
		assertEquals("aaaa", leftFilled("aaaa", -1, '-'));
		assertEquals("aaaa", leftFilled("aaaa", 1, '-'));
		assertEquals("aaaa", leftFilled("aaaa", 4, '-'));
		assertEquals("--aaaa", leftFilled("aaaa", 6, '-'));
		assertEquals(asList(), leftFilled((List) asList(), 0, '-'));
		assertEquals(asList("a", "bc"), leftFilled(asList("a", "bc"), 0, '-'));
		assertEquals(asList("a", "bc"), leftFilled(asList("a", "bc"), -1, '-'));
		assertEquals(asList("a", "bc"), leftFilled(asList("a", "bc"), 1, '-'));
		assertEquals(asList("-a", "bc"), leftFilled(asList("a", "bc"), 2, '-'));
		assertEquals(asList("---a", "--bc"), leftFilled(asList("a", "bc"), 4, '-'));
	}
}
