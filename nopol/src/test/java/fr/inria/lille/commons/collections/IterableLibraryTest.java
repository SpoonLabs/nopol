package fr.inria.lille.commons.collections;

import static fr.inria.lille.commons.collections.IterableLibrary.addTo;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.inria.lille.commons.collections.IterableLibrary;

public class IterableLibraryTest {

	@Test
	public void addIterableToCollection() {
		Iterable<String> iterable = asList("a", "b", "c");
		List<String> list = new ArrayList<String>();
		assertTrue(list.isEmpty());
		addTo(list, iterable);
		assertEquals(3, list.size());
		assertTrue(list.containsAll(asList("a", "b", "c")));
	}
	
	@Test
	public void iterableAsList() {
		Iterable<String> iterable = asList("a", "b", "c");
		List<String> list = IterableLibrary.asList(iterable);
		assertEquals(3, list.size());
		assertTrue(list.containsAll(asList("a", "b", "c")));
		list.add("4");
		assertEquals(4, list.size());
	}
}
