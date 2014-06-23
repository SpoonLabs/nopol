package fr.inria.lille.commons.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapLibrary {

	public static <K, V> Map<K, V> newIdentityHashMap(int keyCapacity) {
		return new IdentityHashMap<K, V>(keyCapacity);
	}
	
	public static <K, V> Map<K, V> newIdentityHashMap(int keyCapacity, Map<K, V> baseMap) {
		Map<K, V> newIdentityHashMap = newIdentityHashMap(keyCapacity);
		return mapFilledWith(newIdentityHashMap, baseMap);
	}
	
	public static <K, V> Map<K, V> newHashMap() {
		return new HashMap<K, V>();
	}
	
	public static <K, V> Map<K, V> newHashMap(Map<K, V> baseMap) {
		Map<K, V> newHashMap = newHashMap();
		return mapFilledWith(newHashMap, baseMap);
	}
	
	public static <K, V> Map<K, V> newLinkedHashMap() {
		return new LinkedHashMap<K, V>();
	}
	
	public static <K, V> Map<K, V> newLinkedHashMap(Map<K, V> baseMap) {
		Map<K, V> newLinkedHashMap = newLinkedHashMap();
		return mapFilledWith(newLinkedHashMap, baseMap);
	}
	
	public static <K, V> Map<K, V> mapFilledWith(Map<K, V> toBeFilled, Map<K, V> sourceMap) {
		toBeFilled.putAll(sourceMap);
		return toBeFilled;
	}

	public static <K, V> V getPutIfAbsent(Map<K, V> map, K key, V valueIfAbsent) {
		if (! map.containsKey(key)) {
			map.put(key, valueIfAbsent);
		}
		return map.get(key);
	}
	
	public static <K, V> void putMany(Map<K, V> sourceMap, V value, Collection<K> keys) {
		for (K key : keys) {
			sourceMap.put(key, value);
		}
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
