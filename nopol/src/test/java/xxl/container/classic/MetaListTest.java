package xxl.container.classic;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static xxl.java.container.classic.MetaList.flatArrayList;
import static xxl.java.container.classic.MetaList.flatLinkedList;
import static xxl.java.container.classic.MetaList.head;
import static xxl.java.container.classic.MetaList.isPartitionOf;
import static xxl.java.container.classic.MetaList.last;
import static xxl.java.container.classic.MetaList.newArrayList;
import static xxl.java.container.classic.MetaList.newLinkedList;
import static xxl.java.container.classic.MetaList.tail;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class MetaListTest {

	@Test
	public void headOfList() {
		List<String> list = newArrayList(".", "..", "...");
		String head = head(list);
		assertTrue(list.contains(head));
		assertEquals(".", head);
	}
	
	@Test
	public void lastOfList() {
		List<String> list = newArrayList(".", "..", "...");
		String last = last(list);
		assertTrue(list.contains(last));
		assertEquals("...", last);
	}
	
	@Test
	public void listAddAllInOne() {
		List<String> firstList = newLinkedList("a", "b", "c");
		List<String> secondList = newLinkedList("d", "e", "f");
		List<String> combined = flatArrayList(firstList, secondList);
		List<String> linkedCombined = flatLinkedList(firstList, secondList);
		assertEquals(firstList.size() + secondList.size(), combined.size());
		assertTrue(combined.containsAll(firstList));
		assertTrue(combined.containsAll(secondList));
		assertEquals(combined.subList(0, 3), firstList);
		assertEquals(combined.subList(3, 6), secondList);
		assertEquals(combined, linkedCombined);
	}
	
	@Test
	public void listAddAllInOneForSuperClass() {
		List<List<Integer>> integers = asList(asList(1,2), asList(3, 4));
		LinkedList<Integer> linkedList = new LinkedList<Integer>(asList(5, 6));
		List<LinkedList<Integer>> otherIntegers = asList(linkedList);
		List<List<Integer>> numbers = flatLinkedList(integers, otherIntegers);
		assertEquals(3, numbers.size());
		assertTrue(numbers.contains(asList(1, 2)));
		assertTrue(numbers.contains(asList(3, 4)));
		assertTrue(numbers.contains(asList(5, 6)));
	}
	
	@Test
	public void isPartitionOfList() {
		List<Character> list = asList('a', 'b', 'c', 'd', 'e');
		List<Character> empty = asList();
		List<Character> copy = asList('a', 'b', 'c', 'd', 'e');
		List<Character> subpartition1 = asList('a', 'b');
		List<Character> subpartition2 = asList('c', 'd');
		List<Character> subpartition3 = asList('e');
		List<Character> subpartition4 = asList('d', 'e');
		List<Character> subpartition5 = asList('b', 'e');
		assertTrue(isPartitionOf(empty));
		assertTrue(isPartitionOf(list, subpartition1, subpartition2, subpartition3));
		assertTrue(isPartitionOf(list, subpartition1, empty, subpartition2, subpartition3));
		assertTrue(isPartitionOf(list, subpartition1, empty, subpartition2, empty, subpartition3));
		assertTrue(isPartitionOf(list, copy));
		assertFalse(isPartitionOf(list));
		assertFalse(isPartitionOf(list, empty));
		assertFalse(isPartitionOf(list, subpartition1, subpartition2, subpartition4));
		assertFalse(isPartitionOf(list, subpartition1, subpartition5, subpartition3));
		assertFalse(isPartitionOf(list, subpartition1, subpartition3, subpartition2));
	}
	
	@Test
	public void arrayListConstructorWithEnumeration() {
		Enumeration<String> enumeration = Collections.enumeration(asList("a", "b", "b", "c"));
		List<String> arrayList = newArrayList(enumeration);
		assertEquals(4, arrayList.size());
		assertEquals("a", arrayList.get(0));
		assertEquals("b", arrayList.get(1));
		assertEquals("b", arrayList.get(2));
		assertEquals("c", arrayList.get(3));
	}
	
	@Test
	public void linkedListConstructorWithEnumeration() {
		Enumeration<String> enumeration = Collections.enumeration(asList("a", "b", "b", "c"));
		List<String> linkedList = newLinkedList(enumeration);
		assertEquals(4, linkedList.size());
		assertEquals("a", linkedList.get(0));
		assertEquals("b", linkedList.get(1));
		assertEquals("b", linkedList.get(2));
		assertEquals("c", linkedList.get(3));
	}
	
	@Test
	public void firstElementsOfAList() {
		assertEquals(asList(), head(0, asList()));
		assertEquals(asList(), head(1, asList()));
		List<String> list = asList("a", "b", "c", "d");
		assertEquals(asList(), head(0, list));
		assertEquals(asList("a"), head(1, list));
		assertEquals(list, head(4, list));
		assertEquals(list, head(5, list));
	}
	
	@Test
	public void lastElementsOfAList() {
		assertEquals(asList(), tail(0, asList()));
		assertEquals(asList(), tail(1, asList()));
		List<String> list = asList("a", "b", "c", "d");
		assertEquals(asList(), tail(0, list));
		assertEquals(asList("d"), tail(1, list));
		assertEquals(list, tail(4, list));
		assertEquals(list, tail(5, list));
		assertEquals(asList("b", "c", "d"), tail(list));
	}
}
