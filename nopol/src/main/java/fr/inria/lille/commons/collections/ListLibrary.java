package fr.inria.lille.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ListLibrary {

	public static <T> List<T> newArrayList() {
		return new ArrayList<T>();
	}
	
	public static <T> List<T> newArrayList(T... elements) {
		return (List) CollectionLibrary.collectionWith(newArrayList(), elements);
	}
	
	public static <T> List<T> newArrayList(Collection<T> collection) {
		return (List) newArrayList(collection.toArray());
	}
	
	public static <T> List<T> newLinkedList() {
		return new LinkedList<T>();
	}
	
	public static <T> List<T> newLinkedList(T... elements) {
		return (List) CollectionLibrary.collectionWith(newLinkedList(), elements);
	}
	
	public static <T> List<T> newLinkedList(Collection<T> collection) {
		return (List) newLinkedList(collection.toArray());
	}
	
}
