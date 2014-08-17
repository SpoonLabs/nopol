package xxl.java.container.classic;

import static java.util.Arrays.asList;
import static xxl.java.library.ClassLibrary.newInstance;
import static xxl.java.library.NumberLibrary.ifNegative;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xxl.java.support.Factory;

public class MetaMap {

/** Factory */
	
	public static <K, V> Factory<Map<K, V>> hashMapFactory() {
		return new Factory<Map<K, V>>() {
			@Override
			public Map<K, V> newInstance() {
				return newHashMap();
			}
		};
	}
	
	public static <K, V> Factory<Map<K, V>> linkedHashMapFactory() {
		return new Factory<Map<K, V>>() {
			@Override
			public Map<K, V> newInstance() {
				return newLinkedHashMap();
			}
		};
	}
	
	
/** HashMap */
	
	public static <K, V> Map<K, V> newHashMap() {
		return newHashMap(1);
	}
	
	public static <K, V> Map<K, V> newHashMap(int initialCapacity) {
		return new HashMap<K, V>(ifNegative(initialCapacity, 1));
	}
	
	public static <K, V> Map<K, V> newHashMap(Map<K, V> baseMap) {
		Map<K, V> newMap = newHashMap(baseMap.size());
		return withAll(newMap, baseMap);
	}
	
	public static <K, V> Map<K, V> newHashMap(K key, V value) {
		return newHashMap(value, asList(key));
	}
	
	public static <K, V> Map<K, V> newHashMap(V value, Collection<K> keys) {
		Map<K, V> newMap = newHashMap(keys.size());
		return withMany(newMap, value, keys);
	}
	
	public static <K, V> Map<K, V> newHashMap(List<K> keys, List<V> values) {
		Map<K, V> newMap = newHashMap(keys.size());
		return withAll(newMap, keys, values);
	}

	
/** LinkedHashMap */
	
	public static <K, V> Map<K, V> newLinkedHashMap() {
		return newLinkedHashMap(1);
	}
	
	public static <K, V> Map<K, V> newLinkedHashMap(int initialCapacity) {
		return new LinkedHashMap<K, V>(ifNegative(initialCapacity, 1));
	}
	
	public static <K, V> Map<K, V> newLinkedHashMap(Map<K, V> baseMap) {
		Map<K, V> newMap = newLinkedHashMap(baseMap.size());
		return withAll(newMap, baseMap);
	}
	
	public static <K, V> Map<K, V> newLinkedHashMap(K key, V value) {
		return newLinkedHashMap(value, asList(key));
	}
	
	public static <K, V> Map<K, V> newLinkedHashMap(V value, Collection<K> keys) {
		Map<K, V> newMap = newLinkedHashMap(keys.size());
		return withMany(newMap, value, keys);
	}
	
	public static <K, V> Map<K, V> newLinkedHashMap(List<K> keys, List<V> values) {
		Map<K, V> newMap = newLinkedHashMap(keys.size());
		return withAll(newMap, keys, values);
	}
	
	
/** IdentityHashMap */
	
	public static <K, V> Map<K, V> newIdentityHashMap(int keyCapacity) {
		return new IdentityHashMap<K, V>(keyCapacity);
	}
	
	public static <K, V> Map<K, V> newIdentityHashMap(int keyCapacity, Map<K, V> baseMap) {
		Map<K, V> newMap = newIdentityHashMap(keyCapacity);
		return withAll(newMap, baseMap);
	}
	
	public static <K, V> Map<K, V> newIdentityHashMap(int keyCapacity, K key, V value) {
		return newIdentityHashMap(keyCapacity, value, asList(key));
	}
	
	public static <K, V> Map<K, V> newIdentityHashMap(int keyCapacity, V value, Collection<K> keys) {
		Map<K, V> newMap = newIdentityHashMap(keyCapacity);
		return withMany(newMap, value, keys);
	}
	
	public static <K, V> Map<K, V> newIdentityHashMap(int keyCapacity, List<K> keys, List<V> values) {
		Map<K, V> newMap = newIdentityHashMap(keyCapacity);
		return withAll(newMap, keys, values);
	}

	
/** Operations */

	public static <K, V> Map<K, V> withMany(Map<K, V> destination, V value, Collection<K> keys) {
		for (K key : keys) {
			destination.put(key, value);
		}
		return destination;
	}
	
	public static <K, V> Map<K, V> withAll(Map<K, V> destination, Map<K, V> sourceMap) {
		destination.putAll(sourceMap);
		return destination;
	}
	
	public static <K, V> Map<K, V> withAll(Map<K, V> destination, List<K> keys, List<V> values) {
		if (keys.size() == values.size()) {
			int index = 0;
			for (K key : keys) {
				destination.put(key, values.get(index));
				index += 1;
			}
		}
		return destination;
	}
	
	public static <K, V> Map<K, V> withAllFlat(Map<K, V> destination, Collection<Map<K, V>> maps) {
		putAllFlat(destination, maps);
		return destination;
	}

	public static <K, V> Map<K, V> putMany(Map<K, V> sourceMap, V value, Collection<K> keys) {
		Map<K, V> previousValues = newHashMap();
		for (K key : keys) {
			V previousValue = sourceMap.put(key, value);
			if (previousValue != null) {
				previousValues.put(key, previousValue);
			}
		}
		return previousValues;
	}
	
	public static <K, V> Map<K, V> putAll(Map<K, V> sourceMap, Map<K, V> destinationMap) {
		Map<K, V> previousValues = newHashMap();
		for (K key : sourceMap.keySet()) {
			V previousValue = destinationMap.put(key, sourceMap.get(key));
			if (previousValue != null) {
				previousValues.put(key, previousValue);
			}
		}
		return previousValues;
	}
	
	public static <K, V> void putAllFlat(Map<K, V> newMap, Collection<Map<K, V>> desintationMaps) {
		for (Map<K, V> map : desintationMaps) {
			putAll(newMap, map);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K,V> copyOf(Map<K, V> map) {
		Map<K, V> copy = newInstance(map.getClass());
		copy.putAll(map);
		return copy;
	}

	public static <K, V> V getPutIfAbsent(Map<K, V> map, K key, V valueIfAbsent) {
		if (! map.containsKey(key)) {
			map.put(key, valueIfAbsent);
		}
		return map.get(key);
	}
	
	public static <K, V> V getPutIfAbsent(Map<K, V> map, K key, Factory<? extends V> factoryIfAbsent) {
		if (! map.containsKey(key)) {
			map.put(key, factoryIfAbsent.newInstance());
		}
		return map.get(key);
	}
	
	public static <K, V> V getIfAbsent(Map<K, V> map, K key, V valueIfAbsent) {
		if (map.containsKey(key)) {
			return map.get(key);
		}
		return valueIfAbsent;
	}
	
	public static <K, V> V getIfAbsent(Map<K, V> map, K key, Factory<? extends V> factoryIfAbsent) {
		if (map.containsKey(key)) {
			return map.get(key);
		}
		return factoryIfAbsent.newInstance();
	}
	
	public static <K> Map<K, Integer> valuesParsedAsInteger(Map<K, String> sourceMap) {
		Map<K, Integer> parsedMap = newHashMap();
		for (K key : sourceMap.keySet()) {
			Integer parsedValue = Integer.valueOf(sourceMap.get(key));
			parsedMap.put(key, parsedValue);
		}
		return parsedMap;
	}
	
	public static <K, V> Collection<K> keysWithValue(V value, Map<K, V> map) {
		return keysWithValuesIn(asList(value), map);
	}
	
	public static <K, V> Collection<K> keysWithValuesIn(Collection<V> values, Map<K, V> map) {
		Collection<K> keys = new HashSet<K>();
		for (K key : map.keySet()) {
			if (values.contains(map.get(key))) {
				keys.add(key);
			}
		}
		return keys;
	}
	
	public static <K, V> Set<K> keySetUnion(Collection<Map<K, V>> maps) {
		Set<K> keys = new HashSet<K>();
		for (Map<K, V> map : maps) {
			keys.addAll(map.keySet());
		}
		return keys;
	}
	
	public static <K, V> Set<K> keySetIntersection(Collection<Map<K, V>> maps) {
		Set<K> keys = new HashSet<K>();
		if (maps.size() > 0) {
			Iterator<Map<K, V>> mapIterator = maps.iterator();
			keys.addAll(mapIterator.next().keySet());
			while (mapIterator.hasNext()) {
				keys.retainAll(mapIterator.next().keySet());
			}
		}
		return keys;
	}
	
	public static <K, V> boolean containsKeys(Collection<K> keys, Map<K, V> map) {
		for (K key : keys) {
			if (! map.containsKey(key)) {
				return false;
			}
		}
		return true;
	}
	
	public static <K, V> boolean sameContent(Map<K, V> map, List<K> keys, List<V> values) {
		boolean sameSize = map.size() == keys.size() && map.size() == values.size();
		if (sameSize) {
			int index = 0;
			for (K key : keys) {
				if (! (map.containsKey(key) && values.get(index).equals(map.get(key)))) {
					return false;
				}
				index += 1;
			}
		}
		return sameSize;
	}
	
	public static <K, V> boolean onlyValueIs(V value, Map<K, V> map) {
		return valuesAreIn(asList(value), map);
	}
	
	public static <K, V> boolean valuesAreIn(Collection<V> restrictedValues, Map<K, V> map) {
		for (K key : map.keySet()) {
			if (! restrictedValues.contains(map.get(key))) {
				return false;
			}
		}
		return ! map.isEmpty();
	}
	
	public static <K> Map<K, Integer> frequencies(Collection<K> collection) {
		Map<K, Integer> frequencies = newHashMap();
		for (K element : collection) {
			int count = getPutIfAbsent(frequencies, element, 0);
			frequencies.put(element, count + 1);
		}
		return frequencies;
	}
	
	public static <K, V> Map<K, V> extractedWithKeys(Collection<K> keys, Map<K, V> map) {
		Map<K, V> extracted = newHashMap();
		for (K key : keys) {
			if (map.containsKey(key)) {
				extracted.put(key, map.get(key));
			}
		}
		return extracted;
	}
}
