package fr.inria.lille.jefix.synth.collector;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ValuesCollectorTest {

	@Before
	public void setUp() {
		ValuesCollector.clear();
	}

	@After
	public void tearDown() {
		ValuesCollector.clear();
	}

	@Test
	public final void adding_a_Collection_should_add_the_size_and_if_it_is_empty() {
		// GIVEN
		String name = "collection";
		Collection<?> value = asList(1, 2, 3);

		// WHEN
		ValuesCollector.add(name, value);

		// THEN
		Iterator<Entry<String, Object>> iterator = ValuesCollector.getValues().iterator();
		Entry<String, Object> isEmpty = iterator.next();
		assertEquals(name + ".isEmpty()", isEmpty.getKey());
		assertEquals(value.isEmpty(), isEmpty.getValue());

		Entry<String, Object> size = iterator.next();
		assertEquals(name + ".size()", size.getKey());
		assertEquals(value.size(), size.getValue());

		Entry<String, Object> entry = iterator.next();
		assertSame(name, entry.getKey());
		assertSame(value, entry.getValue());

		assertFalse(iterator.hasNext());
	}

	@Test
	public final void adding_a_String_should_add_the_length_and_if_it_is_empty() {
		// GIVEN
		String name = "string";
		String value = "Take nothing on its looks; take everything on evidence. There's no better rule.";

		// WHEN
		ValuesCollector.add(name, value);

		// THEN
		Iterator<Entry<String, Object>> iterator = ValuesCollector.getValues().iterator();
		Entry<String, Object> isEmpty = iterator.next();
		assertEquals(name + ".isEmpty()", isEmpty.getKey());
		assertEquals(value.isEmpty(), isEmpty.getValue());

		Entry<String, Object> length = iterator.next();
		assertEquals(name + ".length()", length.getKey());
		assertEquals(value.length(), length.getValue());

		Entry<String, Object> entry = iterator.next();
		assertSame(name, entry.getKey());
		assertSame(value, entry.getValue());

		assertFalse(iterator.hasNext());
	}

	@Test
	public final void adding_an_array_should_add_the_length_also() {
		// GIVEN
		String name = "array";
		int[] value = { 1, 2, 3 };

		// WHEN
		ValuesCollector.add(name, value);

		// THEN
		Iterator<Entry<String, Object>> iterator = ValuesCollector.getValues().iterator();
		Entry<String, Object> entry = iterator.next();
		assertEquals(name + ".length", entry.getKey());
		assertEquals(value.length, entry.getValue());

		entry = iterator.next();
		assertSame(name, entry.getKey());
		assertSame(value, entry.getValue());

		assertFalse(iterator.hasNext());
	}
}
