package xxl.java.container.map;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaSet;
import xxl.java.support.Factory;

public class Multimap<K, V> extends NeoMap<K, Collection<V>> {

	public static <K, V> Multimap<K, V> newListMultimap() {
		Factory<List<V>> factory = MetaList.linkedListFactory();
		return new Multimap<K, V>(new HashMap<K, Collection<V>>(), factory);
	}
	
	public static <K, V> Multimap<K, V> newSetMultimap() {
		Factory<Set<V>> factory = MetaSet.hashSetFactory();
		return new Multimap<K, V>(new HashMap<K, Collection<V>>(), factory);
	}
	
	public static <K, V> Multimap<K, V> newLinkedHashSetMultimap() {
		Factory<Set<V>> factory = MetaSet.linkedHashSetFactory();
		return new Multimap<K, V>(new HashMap<K, Collection<V>>(), factory);
	}
	
	public static <K, V> Multimap<K, V> newListOrderedMultimap() {
		Factory<List<V>> factory = MetaList.linkedListFactory();
		return new Multimap<K, V>(new LinkedHashMap<K, Collection<V>>(), factory);
	}
	
	public static <K, V> Multimap<K, V> newSetOrderedMultimap() {
		Factory<Set<V>> factory = MetaSet.hashSetFactory();
		return new Multimap<K, V>(new LinkedHashMap<K, Collection<V>>(), factory);
	}
	
	public static <K, V> Multimap<K, V> newLinkedHashSetOrderedMultimap() {
		Factory<Set<V>> factory = MetaSet.linkedHashSetFactory();
		return new Multimap<K, V>(new LinkedHashMap<K, Collection<V>>(), factory);
	}
	
	public static <K, V> Multimap<K, V> newIdentityHashListMultimap(int keyCapacity) {
		Factory<List<V>> factory = MetaList.linkedListFactory();
		return new Multimap<K, V>(new IdentityHashMap<K, Collection<V>>(), factory);
	}
	
	public static <K, V> Multimap<K, V> newIdentityHashSetMultimap(int keyCapacity) {
		Factory<Set<V>> factory = MetaSet.hashSetFactory();
		return new Multimap<K, V>(new IdentityHashMap<K, Collection<V>>(), factory);
	}
	
	public static <K, V> Multimap<K, V> newIdentityLinkedHashSetMultimap(int keyCapacity) {
		Factory<Set<V>> factory = MetaSet.linkedHashSetFactory();
		return new Multimap<K, V>(new IdentityHashMap<K, Collection<V>>(), factory);
	}
	
	private Multimap(Map<K, Collection<V>> subject, Factory<? extends Collection<V>> factory) {
		super(subject);
		this.factory = factory;
	}
	
	@SuppressWarnings("unchecked")
	public void addAll(K key, V... values) {
		addAll(key, asList(values));
	}
	
	public void addAll(K key, Collection<V> values) {
		for (V value : values) {
			add(key, value);
		}
	}
	
	public boolean add(K key, V value) {
		return getPutIfAbsent(key, factory()).add(value);
	}
	
	private Factory<? extends Collection<V>> factory() {
		return factory;
	}
	
	private Factory<? extends Collection<V>> factory;
}
