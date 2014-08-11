package fr.inria.lille.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
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
	
	public static <T> List<T> newArrayList(Enumeration<T> enumeration) {
		return (List) CollectionLibrary.addEnumeration((List) newArrayList(), enumeration);
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
	
	public static <T> List<T> newLinkedList(Enumeration<T> enumeration) {
		return (List) CollectionLibrary.addEnumeration((List) newLinkedList(), enumeration);
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
	
	public static <T> List<T> firstElements(int numberOfElements, List<T> list) {
		return list.subList(0, Math.min(list.size(), numberOfElements));
	}
	
	public static <T> List<T> lastElements(int numberOfElements, List<T> list) {
		int length = list.size();
		return list.subList(Math.max(0, length - numberOfElements), length);
		
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
	
	public static <T> boolean addSameInstanceIfRepeated(List<T> list, T element) {
		int index = list.indexOf(element);
		if (index < 0) {
			list.add(element);
			return false;
		}
		list.add(list.get(index));
		return true;
	}
}
