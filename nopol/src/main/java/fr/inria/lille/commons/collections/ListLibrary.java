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
		return (List) CollectionLibrary.addAll(newArrayList(), elements);
	}
	
	public static <T> List<T> newArrayList(Collection<? extends T> collection) {
		return (List) newArrayList(collection.toArray());
	}
	
	public static <T> List<T> flatArrayList(Collection<? extends T>... collections) {
		return (List) CollectionLibrary.addAllFlat((List) newArrayList(), collections);
	}
	
	public static <T> List<T> newLinkedList() {
		return new LinkedList<T>();
	}
	
	public static <T> List<T> newLinkedList(T... elements) {
		return (List) CollectionLibrary.addAll(newLinkedList(), elements);
	}
	
	public static <T> List<T> newLinkedList(Collection<? extends T> collection) {
		return (List) newLinkedList(collection.toArray());
	}
	
	public static <T> List<T> flatLinkedList(Collection<? extends T>... collections) {
		return (List) CollectionLibrary.addAllFlat((List) newLinkedList(), collections);
	}
	
	public static <T> T head(List<T> list) {
		if (! list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public static <T> T last(List<T> list) {
		if (! list.isEmpty()) {
			return list.get(list.size() - 1);
		}
		return null;
	}
	
	public static <T> boolean isPartitionOf(List<T> queriedList, List<? extends T>... partition) {
		boolean sameSize = queriedList.size() == CollectionLibrary.combinedSize(partition);
		if (sameSize) {
			int startIndex = 0;
			for (List<? extends T> subPartition : partition) {
				List<T> subList = queriedList.subList(startIndex, startIndex + subPartition.size());
				if (! subList.equals(subPartition)) {
					return false;
				}
				startIndex += subPartition.size();
			}
		}
		return sameSize;
	}
	
}
