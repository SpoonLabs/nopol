package xxl.java.extensions.collection;

import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static xxl.java.extensions.collection.MapLibrary.getIfAbsent;
import static xxl.java.extensions.collection.MapLibrary.getPutIfAbsent;
import static xxl.java.extensions.collection.MapLibrary.newHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Bag<T> {

	public static <T> Bag<T> newHashBag() {
		return new Bag<T>((Map) newHashMap());
	}
	
	public static <T> Bag<T> newHashBag(T... elements) {
		return newHashBag(asList(elements));
	}
	
	public static <T> Bag<T> newHashBag(Collection<T> elements) {
		Bag<T> newBag = newHashBag();
		newBag.addAll(elements);
		return newBag;
	}
	
	public static <T> Bag<T> newHashBag(List<T> elements, List<Integer> repetitions) {
		Bag<T> newBag = newHashBag();
		newBag.addAll(elements, repetitions);
		return newBag;
	}
	
	public static <T> Bag<T> flatBag(Collection<Bag<T>> bags) {
		Bag<T> flatBag = newHashBag();
		for (Bag<T> bag : bags) {
			flatBag.addAll(bag);
		}
		return flatBag;
	}
	
	protected Bag(Map<T, Integer> emptyMap) {
		size = 0;
		frequencyMap = emptyMap;
	}
	
	public boolean isEmpty() {
		return frequencyMap().isEmpty();
	}
	
	public void clear() {
		setSize(0);
		frequencyMap().clear();
	}
	
	public int size() {
		return size;
	}
	
	public void addAll(List<T> elements, List<Integer> repetitions) {
		int elementsSize = elements.size();
		if (elementsSize == repetitions.size()) {
			for (int i = 0 ; i < elementsSize; i += 1) {
				add(elements.get(i), repetitions.get(i));
			}
		}
	}
	
	public void addAll(Collection<T> elements) {
		for (T element : elements) {
			add(element);
		}
	}
	
	public void addAll(Bag<T> aBag) {
		Map<T, Integer> frequencyMap = aBag.asFrequencyMap();
		for (T element : frequencyMap.keySet()) {
			add(element, frequencyMap.get(element));
		}
	}

	public int add(T object) {
		return add(object, 1);
	}
	
	public int add(T object, int numberOfTimes) {
		int oldValue = getPutIfAbsent(frequencyMap(), object, 0);
		if (numberOfTimes > 0) {
			setSize(size() + numberOfTimes);
			frequencyMap().put(object, oldValue + numberOfTimes);
		}
		return oldValue;
	}
	
	public boolean remove(T object) {
		return remove(object, 1);
	}
	
	public boolean remove(T object, int numberOfTimes) {
		int repetitions = repetitionsOf(object);
		if (repetitions > 0 && numberOfTimes > 0) {
			numberOfTimes = min(repetitions, numberOfTimes);
			frequencyMap().put(object, repetitions - numberOfTimes);
			if (repetitions == numberOfTimes) {
				frequencyMap().remove(object);
			}
			setSize(size() - numberOfTimes);
			return true;
		}
		return false;
	}
	
	public boolean contains(T object) {
		return repetitionsOf(object) > 0;
	}
	
	public int repetitionsOf(T object) {
		return getIfAbsent(frequencyMap(), object, 0);
	}
	
	public Set<T> asSet() {
		return frequencyMap().keySet();
	}
	
	public Map<T, Integer> asFrequencyMap() {
		return frequencyMap();
	}
	
	private Map<T, Integer> frequencyMap() {
		return frequencyMap;
	}
	
	private void setSize(int value) {
		size = value;
	}

	@Override
	public int hashCode() {
		return frequencyMap().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (getClass() != object.getClass())
			return false;
		Bag other = (Bag) object;
		return frequencyMap().equals(other.frequencyMap());
	}

	@Override
	public String toString() {
		return frequencyMap().toString();
	}
	
	private int size;
	private Map<T, Integer> frequencyMap;
}
