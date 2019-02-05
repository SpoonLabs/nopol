package xxl.java.library;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static xxl.java.library.StringLibrary.asStringMap;
import static xxl.java.library.StringLibrary.firstAfterSplit;
import static xxl.java.library.StringLibrary.join;
import static xxl.java.library.StringLibrary.lastAfterSplit;
import static xxl.java.library.StringLibrary.leftFilled;
import static xxl.java.library.StringLibrary.mapWithStringKeys;
import static xxl.java.library.StringLibrary.mapWithStringValues;
import static xxl.java.library.StringLibrary.maximumToStringLength;
import static xxl.java.library.StringLibrary.plainDecimalRepresentation;
import static xxl.java.library.StringLibrary.quoted;
import static xxl.java.library.StringLibrary.repeated;
import static xxl.java.library.StringLibrary.reversed;
import static xxl.java.library.StringLibrary.rightFilled;
import static xxl.java.library.StringLibrary.split;
import static xxl.java.library.StringLibrary.stripEnd;
import static xxl.java.library.StringLibrary.toStringList;
import static xxl.java.library.StringLibrary.unique;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
		splitted = split(chained, "ad.a");
		assertEquals(2, splitted.size());
		assertEquals("aja~", splitted.get(0));
		assertEquals("!~ao", splitted.get(1));
	}
	
	@Test
	public void stringSplitWithCharacter() {
		String chained = "a.b.b.b,cc-dd(aa";
		List<String> splitted;
		splitted = split(chained, '.');
		assertEquals(4, splitted.size());
		assertEquals("a", splitted.get(0));
		assertEquals("b", splitted.get(1));
		assertEquals("b", splitted.get(2));
		assertEquals("b,cc-dd(aa", splitted.get(3));
		splitted = split(chained, ',');
		assertEquals(2, splitted.size());
		assertEquals("a.b.b.b", splitted.get(0));
		assertEquals("cc-dd(aa", splitted.get(1));
		splitted = split(chained, '(');
		assertEquals(2, splitted.size());
		assertEquals("a.b.b.b,cc-dd", splitted.get(0));
		assertEquals("aa", splitted.get(1));
		splitted = split(chained, '+');
		assertEquals(1, splitted.size());
		assertEquals(chained, splitted.get(0));
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
		Map<String, Boolean> stringMap = mapWithStringKeys(map);
		assertEquals(2, stringMap.size());
		assertTrue(stringMap.containsKey("0"));
		assertEquals(false, stringMap.get("0"));
		assertTrue(stringMap.containsKey("1"));
		assertEquals(true, stringMap.get("1"));
	}
	
	@Test
	public void convertValuesToStringInMap() {
		Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
		map.put(0, false);
		map.put(1, true);
		Map<Integer, String> stringMap = mapWithStringValues(map);
		assertEquals(2, stringMap.size());
		assertTrue(stringMap.containsKey(0));
		assertEquals("false", stringMap.get(0));
		assertTrue(stringMap.containsKey(1));
		assertEquals("true", stringMap.get(1));
	}
	
	@Test
	public void convertMapToStringMap() {
		Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
		map.put(0, false);
		map.put(1, true);
		Map<String, String> stringMap = asStringMap(map);
		assertEquals(2, stringMap.size());
		assertTrue(stringMap.containsKey("0"));
		assertEquals("false", stringMap.get("0"));
		assertTrue(stringMap.containsKey("1"));
		assertEquals("true", stringMap.get("1"));
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
	
	@Test
	public void plainDecimalRepresentationOfNumbers() {
		assertEquals("0.0", plainDecimalRepresentation(0));
		assertEquals("0.0", plainDecimalRepresentation(-0));
		assertEquals("0.0", plainDecimalRepresentation(0.0));
		assertEquals("0.0", plainDecimalRepresentation(-0.0));
		assertEquals("1.0", plainDecimalRepresentation(1));
		assertEquals("-1.0", plainDecimalRepresentation(-1));
		assertEquals("1.0", plainDecimalRepresentation(1.0));
		assertEquals("-1.0", plainDecimalRepresentation(-1.0));
		assertEquals("2.0", plainDecimalRepresentation(2));
		assertEquals("-2.0", plainDecimalRepresentation(-2));
		assertEquals("2.0", plainDecimalRepresentation(2.0));
		assertEquals("-2.0", plainDecimalRepresentation(-2.0));
		assertEquals("0.05", plainDecimalRepresentation(5.0/100));
		assertTrue(plainDecimalRepresentation(1.0/3).startsWith("0.33"));
		assertEquals("999999999.0", plainDecimalRepresentation(999999999));
		assertEquals("-999999999.0", plainDecimalRepresentation(-999999999));
		assertEquals("413845470000000000.0", plainDecimalRepresentation(4.1384547E17));
		assertEquals("-413845470000000000.0", plainDecimalRepresentation(-4.1384547E17));
		assertEquals("0.000000000000000041384547", plainDecimalRepresentation(4.1384547E-17));
		assertEquals("-0.000000000000000041384547", plainDecimalRepresentation(-4.1384547E-17));
	}
	
	@Test
	public void decimalRepresentationUsingDot() throws Exception {
		Locale french = Locale.FRANCE;
		Double half = NumberFormat.getInstance(french).parse("0,5").doubleValue();
		DecimalFormat decimalFormat = new DecimalFormat("#0.0", new DecimalFormatSymbols(french));
		assertEquals("0,5", decimalFormat.format(half));
		assertEquals("0.5", plainDecimalRepresentation(half));
		
		Double twoAndHalf = NumberFormat.getInstance(french).parse("2,5").doubleValue();
		assertEquals("2,5", decimalFormat.format(twoAndHalf));
		assertEquals("2.5", plainDecimalRepresentation(twoAndHalf));
	}
	
	@Test
	public void internStringReturnUniqueStrings() {
		String a = "abcdjjq";
		String b = a + "";
		String c = a + "";
		assertTrue(a.equals(b));
		assertTrue(b.equals(c));
		assertTrue(a.equals(c));
		assertFalse(a == b);
		assertFalse(b == c);
		assertFalse(a == c);
		assertTrue(a == unique(a));
		assertTrue(a == unique(b));
		assertTrue(a == unique(c));
	}
	
	@Test
	public void quotationOfString() {
		assertEquals("\"\"", quoted(""));
		assertEquals("\"a\"", quoted("a"));
	}
}
