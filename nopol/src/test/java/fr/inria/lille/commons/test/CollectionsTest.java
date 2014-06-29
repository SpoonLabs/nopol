package fr.inria.lille.commons.test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
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
	
	@SuppressWarnings({"unchecked", "rawtypes"})
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
	
	@SuppressWarnings({"unchecked", "rawtypes"})
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
		Multimap<Integer, String> setMultimap = Multimap.newLinkedHashSetMultimap();
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
		Multimap<String, Integer> identitySetMultimap = Multimap.newIdentityLinkedHashSetMultimap(3);
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
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void unionOfkeySets() {
		Map<String, String> stringMap = MapLibrary.newHashMap();
		Map<String, Byte> byteMap = MapLibrary.newHashMap();
		Collection<String> keyUnion = MapLibrary.keySetUnion((Collection) asList(stringMap, byteMap));
		assertTrue(keyUnion.isEmpty());
		stringMap.put("a", "b");
		keyUnion = MapLibrary.keySetUnion((Collection) asList(stringMap, byteMap));
		assertEquals(1, keyUnion.size());
		assertTrue(keyUnion.contains("a"));
		byteMap.put("a", (byte) 0x29);
		keyUnion = MapLibrary.keySetUnion((Collection) asList(stringMap, byteMap));
		assertEquals(1, keyUnion.size());
		assertTrue(keyUnion.contains("a"));
		stringMap.put("b", "c");
		byteMap.put("z", (byte) 0x23);
		keyUnion = MapLibrary.keySetUnion((Collection) asList(stringMap, byteMap));
		assertEquals(3, keyUnion.size());
		assertTrue(keyUnion.containsAll(asList("a", "b", "z")));
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void mapContainsKeys() {
		Map<String, Integer> map = MapLibrary.newHashMap(asList("a", "b", "c"), asList(10, 20, 30));
		assertTrue(MapLibrary.containsKeys(asList("a", "b", "c"), map));
		assertTrue(MapLibrary.containsKeys(asList("a", "b"), map));
		assertTrue(MapLibrary.containsKeys((List) asList(), map));
		assertFalse(MapLibrary.containsKeys(asList("a", "b", "C"), map));
		assertFalse(MapLibrary.containsKeys(asList(""), map));
		assertFalse(MapLibrary.containsKeys(asList("C"), map));
	}
	
	@Test
	public void assertContentOfMap() {
		Map<String, Character> aMap = MapLibrary.newHashMap();
		aMap.put("a", 'a');
		aMap.put("b", 'b');
		aMap.put("c", 'c');
		assertTrue(MapLibrary.sameContent(aMap, asList("a", "b", "c"), asList('a', 'b', 'c')));
		assertFalse(MapLibrary.sameContent(aMap, asList("a", "b", "c"), asList('a', 'b', 'd')));
		assertFalse(MapLibrary.sameContent(aMap, asList("a", "b", "d"), asList('a', 'b', 'c')));
		assertFalse(MapLibrary.sameContent(aMap, asList("a", "b", "c", "d"), asList('a', 'b', 'c', 'd')));
		assertFalse(MapLibrary.sameContent(aMap, asList("a", "b"), asList('a', 'b')));
		assertFalse(MapLibrary.sameContent(aMap, asList("a", "b", "c"), asList('a', 'b')));
		assertFalse(MapLibrary.sameContent(aMap, asList("a", "b", "c"), asList('a', 'b', 'c', 'd')));
	}

	@Test
	public void adHocHashMap() {
		Map<String, Integer> adHocMap = MapLibrary.newHashMap(asList("A", "b", "C"), asList(1, 2, 3));
		assertEquals(3, adHocMap.size());
		assertTrue(adHocMap.containsKey("A"));
		assertTrue(adHocMap.containsKey("b"));
		assertTrue(adHocMap.containsKey("C"));
		assertEquals(Integer.valueOf(1), adHocMap.get("A"));
		assertEquals(Integer.valueOf(2), adHocMap.get("b"));
		assertEquals(Integer.valueOf(3), adHocMap.get("C"));
	}
	
	@Test
	public void parseValuesOfMapAsIntegers() {
		Map<String, String> toBeParsed = MapLibrary.newHashMap(asList("a", "b", "c"), asList("10", "20", "30"));
		Map<String, Integer> parsed = MapLibrary.valuesParsedAsInteger(toBeParsed);
		assertTrue(MapLibrary.sameContent(parsed, asList("a", "b", "c"), asList(10, 20, 30)));
	}
	
	@Test
	public void addAMapToManyMaps() {
		Map<String, String> newMap = MapLibrary.newHashMap(asList("a", "b", "c"), asList("+", "++", "+++"));
		Map<String, String> firstMap = MapLibrary.newHashMap(asList("d", "e", "f"), asList(".", "..", "..."));
		Map<String, String> secondMap = MapLibrary.newHashMap(asList("g", "h", "i"), asList("-", "--", "---"));
		MapLibrary.putMany(newMap, asList(firstMap, secondMap));
		assertTrue(MapLibrary.sameContent(firstMap, asList("a", "b", "c", "d", "e", "f"), asList("+", "++", "+++", ".", "..", "...")));
		assertTrue(MapLibrary.sameContent(secondMap, asList("a", "b", "c", "g", "h", "i"), asList("+", "++", "+++", "-", "--", "---")));
	}
	
	@SuppressWarnings({"unchecked"})
	@Test
	public void listAddAllInOne() {
		List<String> firstList = ListLibrary.newLinkedList("a", "b", "c");
		List<String> secondList = ListLibrary.newLinkedList("d", "e", "f");
		List<String> combined = ListLibrary.flatArrayList(firstList, secondList);
		List<String> linkedCombined = ListLibrary.flatLinkedList(firstList, secondList);
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
		List<List<Integer>> numbers = ListLibrary.flatLinkedList(integers, otherIntegers);
		assertEquals(3, numbers.size());
		assertTrue(numbers.contains(asList(1, 2)));
		assertTrue(numbers.contains(asList(3, 4)));
		assertTrue(numbers.contains(asList(5, 6)));
	}
	
	
	@SuppressWarnings({"unchecked"})
	@Test
	public void setAddAllInOne() {
		Set<String> firstSet = SetLibrary.newLinkedHashSet("a", "b", "c");
		Set<String> secondSet  = SetLibrary.newLinkedHashSet("d", "e", "f");
		Set<String> combined = SetLibrary.flatLinkedHashSet(firstSet, secondSet, firstSet);
		Set<String> hashedCombined = SetLibrary.flatHashSet(firstSet, secondSet, firstSet);
		assertEquals(firstSet.size() + secondSet.size(), combined.size());
		assertTrue(combined.containsAll(firstSet));
		assertTrue(combined.containsAll(secondSet));
		assertEquals(asList(combined.toArray()).subList(0, 3), asList(firstSet.toArray()));
		assertEquals(asList(combined.toArray()).subList(3, 6), asList(secondSet.toArray()));
		assertEquals(combined, hashedCombined);
	}
	
	@Test
	public void combinedSizeOfCollections() {
		List<Character> firstList = asList('a', 'b', 'c', 'd');
		List<Integer> secondList = asList(1, 2, 3);
		Set<String> empty = SetLibrary.newHashSet();
		Set<Boolean> set = SetLibrary.newHashSet(true, true, false, true, false);
		int combinedSize;
		combinedSize = CollectionLibrary.combinedSize();
		assertEquals(0, combinedSize);
		combinedSize = CollectionLibrary.combinedSize(empty);
		assertEquals(0, combinedSize);
		combinedSize = CollectionLibrary.combinedSize(firstList);
		assertEquals(4, combinedSize);
		combinedSize = CollectionLibrary.combinedSize(firstList, secondList, empty, set);
		assertEquals(9, combinedSize);
	}
	
	@SuppressWarnings({"unchecked"})
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
		assertTrue(ListLibrary.isPartitionOf(list, subpartition1, subpartition2, subpartition3));
		assertTrue(ListLibrary.isPartitionOf(list, subpartition1, empty, subpartition2, subpartition3));
		assertTrue(ListLibrary.isPartitionOf(list, subpartition1, empty, subpartition2, empty, subpartition3));
		assertTrue(ListLibrary.isPartitionOf(list, copy));
		assertFalse(ListLibrary.isPartitionOf(list));
		assertFalse(ListLibrary.isPartitionOf(list, empty));
		assertFalse(ListLibrary.isPartitionOf(list, subpartition1, subpartition2, subpartition4));
		assertFalse(ListLibrary.isPartitionOf(list, subpartition1, subpartition5, subpartition3));
		assertFalse(ListLibrary.isPartitionOf(list, subpartition1, subpartition3, subpartition2));
	}
}
