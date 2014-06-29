package fr.inria.lille.commons.collections;

import java.lang.reflect.Array;
import java.util.Collection;

public class CollectionLibrary {
	
	protected static <T> Collection<T> addAll(Collection<T> destination, T... elements) {
		for (T element : elements) {
			destination.add(element);
		}
		return destination;
	}
	
	protected static <T> Collection<T> addAllFlat(Collection<T> destination, Collection<? extends T>... toBeAdded) {
		for (Collection<? extends T> collection : toBeAdded) {
			destination.addAll(collection);
		}
		return destination;
	}
	
	public static int combinedSize(Collection<?>... collections) {
		int size = 0;
		for (Collection<?> collection : collections) {
			size += collection.size();
		}
		return size;
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
	
	public static <T> int repetitions(Collection<T> collection, T targetElement) {
		int repetitions = 0;
		for (T element : collection) {
			if (element.equals(targetElement)) {
				repetitions += 1;
			}
		}
		return repetitions;
	}
	
	public static <T> void addMany(Collection<T> collection, int numberOfCopies, T element) {
		for (int repetition = 0; repetition < numberOfCopies; repetition += 1) {
			collection.add(element);
		}
	}
	
	public static <T> T any(Collection<T> collection) {
		for (T element : collection) {
			return element;
		}
		return null;
	}
}