package xxl.java.extensions.collection;

import static java.lang.String.format;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DoubleMap<K1, K2, V> implements Map<K1, Map<K2, V>> {

	private static interface MapFactory<K, V> {
		public Map<K, V> newMap();
	}
	
	public static class HashMapFactory<K, V> implements MapFactory<K, V> {
		@Override
		public Map<K, V> newMap() {
			return new HashMap<K, V>();
		}
	}
	
	public static <K1, K2, V> DoubleMap<K1, K2, V> newHashDoubleMap() {
		return new DoubleMap<K1, K2, V>(new HashMap<K1, Map<K2, V>>(), new HashMapFactory<K2, V>());
	}
	
	private DoubleMap(Map<K1, Map<K2, V>> baseMap, MapFactory<K2, V> mapFactory) {
		this.baseMap = baseMap;
		this.mapFactory = mapFactory;
	}
	
	@Override
	public int size() {
		return baseMap().size();
	}

	@Override
	public boolean isEmpty() {
		return baseMap().isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return baseMap().containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return baseMap().containsValue(value);
	}

	@Override
	public Map<K2, V> get(Object key) {
		return baseMap().get(key);
	}

	@Override
	public Map<K2, V> put(K1 key, Map<K2, V> value) {
		return baseMap().put(key, value);
	}

	@Override
	public Map<K2, V> remove(Object key) {
		return baseMap().remove(key);
	}

	@Override
	public void putAll(Map<? extends K1, ? extends Map<K2, V>> m) {
		baseMap().putAll(m);
	}

	@Override
	public void clear() {
		for (Map<K2, V> value : values()) {
			value.clear();
		}
		baseMap().clear();
	}

	@Override
	public Set<K1> keySet() {
		return baseMap().keySet();
	}

	@Override
	public Collection<Map<K2, V>> values() {
		return baseMap().values();
	}

	@Override
	public Set<Entry<K1, Map<K2, V>>> entrySet() {
		return baseMap().entrySet();
	}

	public V value(K1 firstKey, K2 secondKey) {
		return get(firstKey).get(secondKey);
	}
	
	public V put(K1 firstKey, K2 secondKey, V value) {
		return getCreateIfAbsent(firstKey).put(secondKey, value);
	}
	
	public Map<K2, V> getCreateIfAbsent(K1 key) {
		if (! baseMap().containsKey(key)) {
			baseMap().put(key, mapFactory().newMap());
		}
		return baseMap().get(key);
	}
	
	private Map<K1, Map<K2, V>> baseMap() {
		return baseMap;
	}
	
	private MapFactory<K2, V> mapFactory() {
		return mapFactory;
	}
	
	@Override
	public String toString() {
		return format("DoubleMap[%d keys, %d submaps]", keySet().size(), values().size());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((baseMap() == null) ? 0 : baseMap().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (getClass() != object.getClass())
			return false;
		DoubleMap<?, ?, ?> other = (DoubleMap<?, ?, ?>) object;
		if (baseMap() == null) {
			if (other.baseMap() != null)
				return false;
		} else if (!baseMap().equals(other.baseMap()))
			return false;
		return true;
	}

	private Map<K1, Map<K2, V>> baseMap;
	private MapFactory<K2, V> mapFactory;
}
