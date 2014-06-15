package fr.inria.lille.commons.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Multimap<K, V> extends HashMap<K, Collection<V>> {

	public static <K, V> Multimap<K, V> newListMultimap() {
		return new Multimap(LinkedList.class);
	}
	
	public static <K, V> Multimap<K, V> newSetMultimap() {
		return new Multimap(HashSet.class);
	}
	
	public Multimap(Class<? extends Collection<V>> containerClass) {
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
		Collection<V> associatedCollection = MapLibrary.getPutIfAbsent(this, key, valuesContainer());
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
	
	private Class<? extends Collection<V>> containerClass() {
		return containerClass;
	}
	
	private Class<? extends Collection<V>> containerClass;
	private static final long serialVersionUID = -1890049968772724880L;
}
