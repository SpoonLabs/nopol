package fr.inria.lille.commons.collections;

import java.lang.reflect.Array;
import java.util.Collection;

public class CollectionLibrary {

	protected static <T> Collection<T> collectionWith(Collection<T> collection, T... elements) {
		addTo(collection, elements);
		return collection;
	}
	
	protected static <T> void addTo(Collection<T> aCollection, T... elements) {
		for (T element : elements) {
			aCollection.add(element);
		}
	}
	
	public static <T> T[] toArray(Class<T> aClass, Collection<T> aCollection) {
		T[] array = (T[]) Array.newInstance(aClass, aCollection.size());
		int index = 0;
		for (T element : aCollection) {
			array[index] = element;
			index += 1;
		}
		return array;
	}
	
}