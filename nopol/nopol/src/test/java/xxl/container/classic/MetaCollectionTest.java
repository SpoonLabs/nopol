package xxl.container.classic;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static xxl.java.container.classic.MetaCollection.addMany;
import static xxl.java.container.classic.MetaCollection.any;
import static xxl.java.container.classic.MetaCollection.combinedSize;
import static xxl.java.container.classic.MetaCollection.copyOf;
import static xxl.java.container.classic.MetaCollection.maximum;
import static xxl.java.container.classic.MetaCollection.repetitions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class MetaCollectionTest {

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void copyOfList() {
		List<String> arraysArrayList = asList(",", ".", "<", ":", "<", ":");
		List<String> listSymbols = new ArrayList<String>(arraysArrayList);
		Collection<String> copy = copyOf(listSymbols);
		assertEquals(6, listSymbols.size());
		assertEquals(6, copy.size());
		assertEquals(listSymbols.getClass(), copy.getClass());
		List<String> copiedList = (List) copy;
		for (int i = 0; i < listSymbols.size(); i += 1) {
			assertEquals(listSymbols.get(i), copiedList.get(i));
		}
		assertEquals(listSymbols, copyOf(arraysArrayList));
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void copyOfSet() {
		Set<String> setSymbols = new HashSet<String>(asList(",", ".", "<", ":", "<", ":"));
		Collection<String> copy = copyOf(setSymbols);
		assertEquals(4, setSymbols.size());
		assertEquals(4, copy.size());
		assertEquals(setSymbols.getClass(), copy.getClass());
		Set<String> copiedSet = (Set) copy;
		assertTrue(copiedSet.containsAll(setSymbols));
	}
	
	@Test
	public void anyOfList() {
		List<String> list = new ArrayList<String>(asList(".", "..", "..."));
		assertTrue(list.contains(any(list)));
	}
	
	@Test
	public void anyOfSet() {
		Set<String> set = new HashSet<String>(asList("-", "--", "---"));
		assertTrue(set.contains(any(set)));
	}
	
	@Test
	public void repetitionsInList() {
		List<String> list = new ArrayList<String>();
		assertEquals(0, repetitions(list, "a"));
		list.add("a");
		assertEquals(1, repetitions(list, "a"));
		list.add("a");
		assertEquals(2, repetitions(list, "a"));
		list.remove("a");
		assertEquals(1, repetitions(list, "a"));
	}
	
	@Test
	public void repetitionsInSet() {
		Set<String> set = new HashSet<String>();
		assertEquals(0, repetitions(set, "a"));
		set.add("a");
		assertEquals(1, repetitions(set, "a"));
		set.add("a");
		assertEquals(1, repetitions(set, "a"));
		set.remove("a");
		assertEquals(0, repetitions(set, "a"));
	}
	
	@Test
	public void addManyToList() {
		List<String> list = new ArrayList<String>(asList("a"));
		addMany(list, "a", 0);
		assertEquals(1, list.size());
		addMany(list, "a", 1);
		assertEquals(2, list.size());
		addMany(list, "a", 5);
		assertEquals(7, list.size());
		assertEquals(7, repetitions(list, "a"));
	}
	
	@Test
	public void addManyToSet() {
		Set<String> set = new HashSet<String>();
		addMany(set, "a", 0);
		assertEquals(0, set.size());
		addMany(set, "a", 1);
		assertEquals(1, set.size());
		addMany(set, "a", 5);
		assertEquals(1, set.size());
		assertEquals(1, repetitions(set, "a"));
	}
	
	@Test
	public void combinedSizeOfCollections() {
		List<Character> firstList = asList('a', 'b', 'c', 'd');
		List<Integer> secondList = asList(1, 2, 3);
		Set<String> empty = new HashSet<String>();
		Set<Boolean> set = new HashSet<Boolean>(asList(true, true, false, true, false));
		assertEquals(0, combinedSize());
		assertEquals(0, combinedSize(empty));
		assertEquals(4, combinedSize(firstList));
		assertEquals(9, combinedSize(firstList, secondList, empty, set));
	}
	
	@Test
	public void maximumIntegerOfACollection() {
		Collection<Integer> empty = asList();
		assertTrue(null == maximum(empty));
		assertTrue(0 == maximum(empty, 0));
		assertEquals(4, maximum(asList(1, 4, 3)).intValue());
		assertEquals(1, maximum(asList(1, -1, 0)).intValue());
	}
}
