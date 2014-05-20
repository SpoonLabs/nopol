package fr.inria.lille.spirals.commons.collections;

import java.util.ArrayList;
import java.util.List;

public class ListLibrary {

	public static <T> List<T> newArrayList() {
		return new ArrayList<T>();
	}
	
	public static <T> List<T> newArrayList(T... elements) {
		List<T> arrayList = newArrayList();
		for (T element : elements) {
			arrayList.add(element);
		}
		return arrayList;
	}
}
