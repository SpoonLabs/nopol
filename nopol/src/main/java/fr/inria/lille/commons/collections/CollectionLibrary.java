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
	
	public static <T, C extends Collection<T>> Collection<T> newInstance(Class<C> collectionClass) {
		try {
			return collectionClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> Collection<T> copyOf(Collection<T> collection) {
		Collection<T> copy = newInstance(collection.getClass());
		copy.addAll(collection);
		return copy;
	}
	
	public static <T> T any(Collection<T> collection) {
		for (T element : collection) {
			return element;
		}
		return null;
	}

}