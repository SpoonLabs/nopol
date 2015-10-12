package xxl.java.container.map;

import xxl.java.container.classic.MetaMap;
import xxl.java.support.Factory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class NeoMap<K, V> implements Map<K, V> {

    public NeoMap(Map<K, V> subject) {
        this.subject = subject;
    }

    @Override
    public int size() {
        return subject().size();
    }

    @Override
    public boolean isEmpty() {
        return subject().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return subject().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return subject().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return subject().get(key);
    }

    @Override
    public V put(K key, V value) {
        return subject().put(key, value);
    }

    @Override
    public V remove(Object key) {
        return subject().remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        subject().putAll(m);
    }

    @Override
    public void clear() {
        subject().clear();
    }

    @Override
    public Set<K> keySet() {
        return subject().keySet();
    }

    @Override
    public Collection<V> values() {
        return subject().values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return subject().entrySet();
    }

    public V getPutIfAbsent(K key, V valueIfAbsent) {
        return MetaMap.getPutIfAbsent(this, key, valueIfAbsent);
    }

    public V getPutIfAbsent(K key, Factory<? extends V> factoryIfAbsent) {
        return MetaMap.getPutIfAbsent(this, key, factoryIfAbsent);
    }

    public V getIfAbsent(K key, V valueIfAbsent) {
        return MetaMap.getIfAbsent(this, key, valueIfAbsent);
    }

    public V getIfAbsent(K key, Factory<? extends V> factoryIfAbsent) {
        return MetaMap.getIfAbsent(this, key, factoryIfAbsent);
    }

    protected Map<K, V> subject() {
        return subject;
    }

    @Override
    public int hashCode() {
        return subject().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return subject().equals(object);
    }

    @Override
    public String toString() {
        return subject().toString();
    }

    private Map<K, V> subject;
}
