package fr.inria.lille.commons.collections;

import static fr.inria.lille.commons.collections.Multimap.newIdentityHashListMultimap;
import static fr.inria.lille.commons.collections.Multimap.newIdentityLinkedHashSetMultimap;
import static fr.inria.lille.commons.collections.Multimap.newLinkedHashSetMultimap;
import static fr.inria.lille.commons.collections.Multimap.newListMultimap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import fr.inria.lille.commons.collections.Multimap;

public class MultimapTest {

	@Test
	public void listMultimap() {
		Multimap<Integer, String> listMultimap = newListMultimap();
		listMultimap.addAll(1, "a", "b", "c", "a", "b", "c");
		assertEquals(1, listMultimap.size());
		assertTrue(listMultimap.containsKey(1));
		Collection<String> values = listMultimap.get(1);
		assertEquals(6, values.size());
		assertEquals(Arrays.asList("a", "b", "c", "a", "b", "c"),  values);
	}
	
	@Test
	public void setMultimap() {
		Multimap<Integer, String> setMultimap = newLinkedHashSetMultimap();
		setMultimap.addAll(1, "a", "b", "c", "a", "b", "c");
		assertEquals(1, setMultimap.size());
		assertTrue(setMultimap.containsKey(1));
		Collection<String> values = setMultimap.get(1);
		assertEquals(3, values.size());
		assertTrue(values.contains("a"));
		assertTrue(values.contains("b"));
		assertTrue(values.contains("c"));
	}
	
	@Test
	public void identityHashListMultimap() {
		Multimap<String, Integer> identityListMultimap = newIdentityHashListMultimap(3);
		Collection<Integer> integerValues;
		String firstA = new String("a");
		String secondA = new String("a");
		String thirdA = new String("a");
		identityListMultimap.addAll(firstA, 1, 2, 3);
		identityListMultimap.addAll(secondA, 2, 3, 4, 5);
		identityListMultimap.addAll(thirdA, 1, 1, 1);
		assertEquals(3, identityListMultimap.size());
		assertTrue(identityListMultimap.containsKey(firstA));
		assertTrue(identityListMultimap.containsKey(secondA));
		assertTrue(identityListMultimap.containsKey(thirdA));
		integerValues = identityListMultimap.get(firstA);
		assertEquals(3, integerValues.size());
		assertTrue(integerValues.contains(1));
		assertTrue(integerValues.contains(2));
		assertTrue(integerValues.contains(3));
		integerValues = identityListMultimap.get(secondA);
		assertEquals(4, integerValues.size());
		assertTrue(integerValues.contains(2));
		assertTrue(integerValues.contains(3));
		assertTrue(integerValues.contains(4));
		assertTrue(integerValues.contains(5));
		integerValues = identityListMultimap.get(thirdA);
		assertEquals(3, integerValues.size());
		assertTrue(integerValues.contains(1));
		assertTrue(integerValues.remove(1));
		assertTrue(integerValues.remove(1));
		assertTrue(integerValues.remove(1));
		assertTrue(integerValues.isEmpty());
	}
	
	@Test
	public void identityHashSetMultimap() {
		Multimap<String, Integer> identitySetMultimap = newIdentityLinkedHashSetMultimap(3);
		Collection<Integer> integerValues;
		String firstA = new String("a");
		String secondA = new String("a");
		String thirdA = new String("a");
		identitySetMultimap.addAll(firstA, 1, 2, 3);
		identitySetMultimap.addAll(secondA, 2, 3, 4, 5);
		identitySetMultimap.addAll(thirdA, 1, 1, 1);
		assertEquals(3, identitySetMultimap.size());
		assertTrue(identitySetMultimap.containsKey(firstA));
		assertTrue(identitySetMultimap.containsKey(secondA));
		assertTrue(identitySetMultimap.containsKey(thirdA));
		integerValues = identitySetMultimap.get(firstA);
		assertEquals(3, integerValues.size());
		assertTrue(integerValues.contains(1));
		assertTrue(integerValues.contains(2));
		assertTrue(integerValues.contains(3));
		integerValues = identitySetMultimap.get(secondA);
		assertEquals(4, integerValues.size());
		assertTrue(integerValues.contains(2));
		assertTrue(integerValues.contains(3));
		assertTrue(integerValues.contains(4));
		assertTrue(integerValues.contains(5));
		integerValues = identitySetMultimap.get(thirdA);
		assertEquals(1, integerValues.size());
		assertTrue(integerValues.contains(1));
		assertTrue(integerValues.remove(1));
		assertTrue(integerValues.isEmpty());
	}
	
	@Test
	public void capacityOfIdentityHashMultimap() {
		Multimap<String, Integer> identityListMultimap = newIdentityHashListMultimap(2);
		identityListMultimap.addAll("qjjw", 1, 2, 4);
		identityListMultimap.addAll("xzcv", 9, 3, 1, 2);
		assertEquals(2, identityListMultimap.size());
		identityListMultimap.add("extra", 0);
		assertEquals(3, identityListMultimap.size());
	}
}
