package fr.inria.lille.commons.collections;

import java.util.HashMap;
import java.util.Map;

public class MapLibrary {

	public static <K, V> Map<K, V> newHashMap() {
		return new HashMap<K, V>();
	}
	
	public static <K, V> Map<K, V> newHashMap(Map<K, V> baseMap) {
		Map<K, V> associativeArray = newHashMap();
		for (K key : baseMap.keySet()) {
			associativeArray.put(key, baseMap.get(key));
		}
		return associativeArray;
	}
	
	public static <K, V> V getPutIfAbsent(Map<K, V> map, K key, V valueIfAbsent) {
		if (! map.containsKey(key)) {
			map.put(key, valueIfAbsent);
		}
		return map.get(key);
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
	
	public static <K, V> Map<String, V> toStringMap(Map<K, V> sourceMap) {
		Map<String, V> toStringMap = newHashMap();
		for (K key : sourceMap.keySet()) {
			toStringMap.put(key.toString(), sourceMap.get(key));
		}
		return toStringMap;
	}
}
