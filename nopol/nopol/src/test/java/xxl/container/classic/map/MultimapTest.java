package xxl.container.classic.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static xxl.java.container.map.Multimap.newIdentityHashListMultimap;
import static xxl.java.container.map.Multimap.newIdentityLinkedHashSetMultimap;
import static xxl.java.container.map.Multimap.newLinkedHashSetMultimap;
import static xxl.java.container.map.Multimap.newListMultimap;
import static xxl.java.container.map.Multimap.newSetMultimap;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import xxl.java.container.map.Multimap;

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
	
	@Test
	public void sizeOfValuesForAKey() {
		Multimap<String, String> multimap = newListMultimap();
		multimap.add("a", "A");
		multimap.add("a", "AA");
		assertEquals(0, multimap.totalValuesOf("b"));
		assertEquals(2, multimap.totalValuesOf("a"));
	}
	
	@Test
	public void addAnotherMap() {
		Multimap<String, Integer> multimap = newSetMultimap();
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("a", 1);
		map.put("b", 2);
		multimap.addAll(map);
		assertTrue(multimap.containsKey("a"));
		assertTrue(multimap.containsKey("b"));
		assertTrue(multimap.get("a").size() == 1);
		assertTrue(multimap.get("b").size() == 1);
		assertTrue( multimap.get("a").contains(1));
		assertTrue( multimap.get("b").contains(2));
	}
}
