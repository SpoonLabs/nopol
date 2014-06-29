package fr.inria.lille.commons.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class SetLibrary {

	public static <T> Set<T> newHashSet() {
		return new HashSet<T>();
	}
	
	public static <T> Set<T> newHashSet(T... elements) {
		return (Set) CollectionLibrary.addAll(newHashSet(), elements);
	}
	
	public static <T> Set<T> newHashSet(Collection<T> collection) {
		return (Set) newHashSet(collection.toArray());
	}
	
	public static <T> Set<T> flatHashSet(Collection<T>... collections) {
		return (Set) CollectionLibrary.addAllFlat((Set) newHashSet(), collections);
	}
	
	public static <T> Set<T> newLinkedHashSet() {
		return new LinkedHashSet<T>();
	}
	
	public static <T> Set<T> newLinkedHashSet(T... elements) {
		return (Set) CollectionLibrary.addAll(newLinkedHashSet(), elements);
	}
	
	public static <T> Set<T> newLinkedHashSet(Collection<T> collection) {
		return (Set) newLinkedHashSet(collection.toArray());
	}
	
	public static <T> Set<T> flatLinkedHashSet(Collection<T>... collections) {
		return (Set) CollectionLibrary.addAllFlat((Set) newLinkedHashSet(), collections);
	}
}
