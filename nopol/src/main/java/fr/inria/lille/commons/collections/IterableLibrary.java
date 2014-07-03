package fr.inria.lille.commons.collections;

import java.util.Collection;
import java.util.List;

public class IterableLibrary {

	public static <T> List<T> asList(Iterable<T> iterable) {
		List<T> asList = ListLibrary.newLinkedList();
		addTo(asList, iterable);
		return asList;
	}
	
	public static <T> void addTo(Collection<T> collection, Iterable<T> iterable) {
		for (T element : iterable) {
			collection.add(element);
		}
	}
}
