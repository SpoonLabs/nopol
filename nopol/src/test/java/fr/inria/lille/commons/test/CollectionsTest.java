package fr.inria.lille.commons.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import fr.inria.lille.commons.collections.ArrayLibrary;
import fr.inria.lille.commons.collections.CollectionLibrary;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.Multimap;
import fr.inria.lille.commons.collections.SetLibrary;

public class CollectionsTest {

	@Test
	public void subarray() {
		String[] array = new String[] {"a", "s", "r"};
		String[] subarray;
		
		subarray = ArrayLibrary.subarray(array, 0, 0);
		assertEquals(0, subarray.length);
		
		subarray = ArrayLibrary.subarray(array, 0, 1);
		assertEquals(1, subarray.length);
		assertEquals("a", subarray[0]);
		
		subarray = ArrayLibrary.subarray(array, 0, 2);
		assertEquals(2, subarray.length);
		assertEquals("a", subarray[0]);
		assertEquals("s", subarray[1]);
		
		subarray = ArrayLibrary.subarray(array, 1, 2);
		assertEquals(1, subarray.length);
		assertEquals("s", subarray[0]);
		
		subarray = ArrayLibrary.subarray(array, 0, 3);
		assertEquals(3, subarray.length);
		assertTrue(Arrays.equals(array, subarray));
		
		subarray = ArrayLibrary.subarray(array, 0, 4);
		assertEquals(3, subarray.length);
		assertTrue(Arrays.equals(array, subarray));
	}
	
	@Test
	public void copyOfList() {
		List<String> listSymbols = ListLibrary.newArrayList(",", ".", "<", ":", "<", ":");
		Collection<String> copy = CollectionLibrary.copyOf(listSymbols);
		assertEquals(6, listSymbols.size());
		assertEquals(6, copy.size());
		assertEquals(listSymbols.getClass(), copy.getClass());
		List<String> copiedList = (List) copy;
		for (int i = 0; i < listSymbols.size(); i += 1) {
			assertEquals(listSymbols.get(i), copiedList.get(i));
		}
	}
	
	@Test
	public void copyOfSet() {
		Set<String> setSymbols = SetLibrary.newHashSet(",", ".", "<", ":", "<", ":");
		Collection<String> copy = CollectionLibrary.copyOf(setSymbols);
		assertEquals(4, setSymbols.size());
		assertEquals(4, copy.size());
		assertEquals(setSymbols.getClass(), copy.getClass());
		Set<String> copiedSet = (Set) copy;
		assertTrue(copiedSet.containsAll(setSymbols));
	}
	
	@Test
	public void anyOfList() {
		List<String> list = ListLibrary.newArrayList(".", "..", "...");
		assertTrue(list.contains(CollectionLibrary.any(list)));
	}
	
	@Test
	public void anyOfSet() {
		Set<String> set = SetLibrary.newHashSet("-", "--", "---");
		assertTrue(set.contains(CollectionLibrary.any(set)));
	}
	
	@Test
	public void headOfList() {
		List<String> list = ListLibrary.newArrayList(".", "..", "...");
		String head = ListLibrary.head(list);
		assertTrue(list.contains(head));
		assertEquals(".", head);
	}
	
	@Test
	public void lastOfList() {
		List<String> list = ListLibrary.newArrayList(".", "..", "...");
		String last = ListLibrary.last(list);
		assertTrue(list.contains(last));
		assertEquals("...", last);
	}
	
	@Test
	public void repetitionsInList() {
		List<String> list = ListLibrary.newArrayList();
		assertEquals(0, CollectionLibrary.repetitions(list, "a"));
		list.add("a");
		assertEquals(1, CollectionLibrary.repetitions(list, "a"));
		list.add("a");
		assertEquals(2, CollectionLibrary.repetitions(list, "a"));
		list.remove("a");
		assertEquals(1, CollectionLibrary.repetitions(list, "a"));
	}
	
	@Test
	public void repetitionsInSet() {
		Set<String> set = SetLibrary.newHashSet();
		assertEquals(0, CollectionLibrary.repetitions(set, "a"));
		set.add("a");
		assertEquals(1, CollectionLibrary.repetitions(set, "a"));
		set.add("a");
		assertEquals(1, CollectionLibrary.repetitions(set, "a"));
		set.remove("a");
		assertEquals(0, CollectionLibrary.repetitions(set, "a"));
	}
	
	@Test
	public void addManyToList() {
		List<String> list = ListLibrary.newArrayList("a");
		CollectionLibrary.addMany(list, 0, "a");
		assertEquals(1, list.size());
		CollectionLibrary.addMany(list, 1, "a");
		assertEquals(2, list.size());
		CollectionLibrary.addMany(list, 5, "a");
		assertEquals(7, list.size());
		assertEquals(7, CollectionLibrary.repetitions(list, "a"));
	}
	
	@Test
	public void addManyToSet() {
		Set<String> set = SetLibrary.newHashSet();
		CollectionLibrary.addMany(set, 0, "a");
		assertEquals(0, set.size());
		CollectionLibrary.addMany(set, 1, "a");
		assertEquals(1, set.size());
		CollectionLibrary.addMany(set, 5, "a");
		assertEquals(1, set.size());
		assertEquals(1, CollectionLibrary.repetitions(set, "a"));
	}
	
	@Test
	public void toStringMap() {
		Map<Integer, Boolean> map = MapLibrary.newHashMap();
		map.put(0, false);
		map.put(1, true);
		Map<String, Boolean> stringMap = MapLibrary.toStringMap(map);
		assertEquals(2, stringMap.keySet().size());
		assertTrue(stringMap.containsKey("0"));
		assertEquals(false, stringMap.get("0"));
		assertTrue(stringMap.containsKey("1"));
		assertEquals(true, stringMap.get("1"));
	}
	
	@Test
	public void listMultimap() {
		Multimap<Integer, String> listMultimap = Multimap.newListMultimap();
		listMultimap.addAll(1, "a", "b", "c", "a", "b", "c");
		assertEquals(1, listMultimap.size());
		assertTrue(listMultimap.containsKey(1));
		Collection<String> values = listMultimap.get(1);
		assertEquals(6, values.size());
		assertEquals(Arrays.asList("a", "b", "c", "a", "b", "c"),  values);
	}
	
	@Test
	public void setMultimap() {
		Multimap<Integer, String> setMultimap = Multimap.newSetMultimap();
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
		Multimap<String, Integer> identityListMultimap = Multimap.newIdentityHashListMultimap(3);
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
		Multimap<String, Integer> identitySetMultimap = Multimap.newIdentityHashSetMultimap(3);
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
		Multimap<String, Integer> identityListMultimap = Multimap.newIdentityHashListMultimap(2);
		identityListMultimap.addAll("qjjw", 1, 2, 4);
		identityListMultimap.addAll("xzcv", 9, 3, 1, 2);
		assertEquals(2, identityListMultimap.size());
		identityListMultimap.add("extra", 0);
		assertEquals(3, identityListMultimap.size());
	}
	
	@Test
	public void putValueInManyKeys() {
		Map<String, String> map = MapLibrary.newHashMap();
		assertEquals(0, map.size());
		List<String> keys = Arrays.asList("a", "b", "c", "d");
		MapLibrary.putMany(map, "C", keys);
		assertEquals(4, map.size());
		for (String key : keys) {
			assertTrue(map.containsKey(key));
			assertEquals("C", map.get(key));
		}
	}

}
