package fr.inria.lille.commons.collections;

import static fr.inria.lille.commons.collections.MapLibrary.getPutIfAbsent;
import static fr.inria.lille.commons.collections.MapLibrary.newHashMap;
import static fr.inria.lille.commons.collections.MapLibrary.newIdentityHashMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Multimap<K, V> implements Map<K, Collection<V>> {

	public static <K, V> Multimap<K, V> newListMultimap() {
		return new Multimap(newHashMap(), LinkedList.class);
	}
	
	public static <K, V> Multimap<K, V> newSetMultimap() {
		return new Multimap(newHashMap(), HashSet.class);
	}
	
	public static <K, V> Multimap<K, V> newLinkedHashSetMultimap() {
		return new Multimap(newHashMap(), LinkedHashSet.class);
	}
	
	public static <K, V> Multimap<K, V> newIdentityHashListMultimap(int keyCapacity) {
		return new Multimap(newIdentityHashMap(keyCapacity), LinkedList.class);
	}
	
	public static <K, V> Multimap<K, V> newIdentityHashSetMultimap(int keyCapacity) {
		return new Multimap(newIdentityHashMap(keyCapacity), HashSet.class);
	}
	
	public static <K, V> Multimap<K, V> newIdentityLinkedHashSetMultimap(int keyCapacity) {
		return new Multimap(newIdentityHashMap(keyCapacity), LinkedHashSet.class);
	}
	
	private Multimap(Map<K, Collection<V>> realSubject, Class<? extends Collection<V>> containerClass) {
		this.realSubject = realSubject;
		this.containerClass = containerClass;
	}
	
	public void addAll(K key, V... values) {
		addAll(key, Arrays.asList(values));
	}
	
	public void addAll(K key, Collection<V> values) {
		for (V value : values) {
			add(key, value);
		}
	}
	
	public boolean add(K key, V value) {
		Collection<V> associatedCollection = getPutIfAbsent(this, key, valuesContainer());
		return associatedCollection.add(value);
	}
	
	protected Collection<V> valuesContainer() {
		try {
			return containerClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		throw new IllegalStateException("Multimap initialized with incorrect container class: " + containerClass());
	}
	
	@Override
	public int size() {
		return realSubject().size();
	}

	@Override
	public boolean isEmpty() {
		return realSubject().isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return realSubject().containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return realSubject().containsValue(value);
	}

	@Override
	public Collection<V> get(Object key) {
		return realSubject().get(key);
	}

	@Override
	public Collection<V> put(K key, Collection<V> value) {
		return realSubject().put(key, value);
	}

	@Override
	public Collection<V> remove(Object key) {
		return realSubject().remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends Collection<V>> m) {
		realSubject().putAll(m);
	}

	@Override
	public void clear() {
		realSubject().clear();
	}

	@Override
	public Set<K> keySet() {
		return realSubject().keySet();
	}

	@Override
	public Collection<Collection<V>> values() {
		return realSubject().values();
	}

	@Override
	public Set<java.util.Map.Entry<K, Collection<V>>> entrySet() {
		return realSubject().entrySet();
	}
	
	private Map<K, Collection<V>> realSubject() {
		return realSubject;
	}
	
	private Class<? extends Collection<V>> containerClass() {
		return containerClass;
	}
	
	@Override
	public int hashCode() {
		return realSubject().hashCode();
	}
	
	@Override
	public boolean equals(Object object) {
		return realSubject().equals(object);
	}
	
	@Override
	public String toString() {
		return realSubject().toString();
	}
	
	private Map<K, Collection<V>> realSubject;
	private Class<? extends Collection<V>> containerClass;
}
