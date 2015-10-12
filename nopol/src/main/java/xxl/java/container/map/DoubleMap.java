package xxl.java.container.map;

import xxl.java.container.classic.MetaMap;
import xxl.java.support.Factory;

import java.util.Map;

import static java.lang.String.format;

public class DoubleMap<K1, K2, V> extends NeoMap<K1, Map<K2, V>> {

    public static <K1, K2, V> DoubleMap<K1, K2, V> newHashDoubleMap() {
        Map<K1, Map<K2, V>> newMap = MetaMap.newHashMap();
        Factory<Map<K2, V>> factory = MetaMap.hashMapFactory();
        return new DoubleMap<K1, K2, V>(newMap, factory);
    }

    private DoubleMap(Map<K1, Map<K2, V>> subject, Factory<Map<K2, V>> factory) {
        super(subject);
        this.factory = factory;
    }

    @Override
    public void clear() {
        for (Map<K2, V> value : values()) {
            value.clear();
        }
        super.clear();
    }

    public V value(K1 firstKey, K2 secondKey) {
        return get(firstKey).get(secondKey);
    }

    public V put(K1 firstKey, K2 secondKey, V value) {
        return getPutIfAbsent(firstKey).put(secondKey, value);
    }

    public Map<K2, V> getPutIfAbsent(K1 firstKey) {
        return getPutIfAbsent(firstKey, factory());
    }

    private Factory<Map<K2, V>> factory() {
        return factory;
    }

    @Override
    public String toString() {
        return format("DoubleMap[%d keys, %d submaps]", keySet().size(), values().size());
    }

    private Factory<Map<K2, V>> factory;
}
