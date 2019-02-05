package xxl.java.container.classic;

import xxl.java.support.Factory;

import java.util.*;

import static java.util.Arrays.asList;
import static xxl.java.container.classic.MetaCollection.*;
import static xxl.java.library.NumberLibrary.bounded;
import static xxl.java.library.NumberLibrary.ifNegative;

public class MetaList {

    /**
     * Factory
     */

    public static <T> Factory<List<T>> arrayListFactory() {
        return new Factory<List<T>>() {
            @Override
            public List<T> newInstance() {
                return newArrayList();
            }
        };
    }

    public static <T> Factory<List<T>> linkedListFactory() {
        return new Factory<List<T>>() {
            @Override
            public List<T> newInstance() {
                return newLinkedList();
            }
        };
    }


    /**
     * ArrayList
     */

    public static <T> List<T> newArrayList() {
        return newArrayList(1);
    }

    public static <T> List<T> newArrayList(int initialCapacity) {
        return new ArrayList<T>(ifNegative(initialCapacity, 1));
    }

    public static <T> List<T> newArrayList(T element, int repetitions) {
        List<T> newList = newArrayList(repetitions);
        return (List<T>) withMany(newList, element, repetitions);
    }

    @SafeVarargs
    public static <T> List<T> newArrayList(T... elements) {
        return newArrayList(asList(elements));
    }

    public static <T> List<T> newArrayList(Collection<? extends T> collection) {
        List<T> newList = newArrayList(collection.size());
        return (List<T>) withAll(newList, collection);
    }

    public static <T> List<T> newArrayList(Enumeration<? extends T> enumeration) {
        List<T> newList = newArrayList();
        return (List<T>) withAll(newList, enumeration);
    }

    @SafeVarargs
    public static <T> List<T> flatArrayList(Collection<? extends T>... collections) {
        List<T> newList = newArrayList(combinedSize(collections));
        return (List<T>) withAllFlat(newList, collections);
    }


    /**
     * LinkedList
     */

    public static <T> List<T> newLinkedList() {
        return new LinkedList<T>();
    }

    public static <T> List<T> newLinkedList(T element, int repetitions) {
        List<T> newList = newLinkedList();
        return (List<T>) withMany(newList, element, repetitions);
    }

    @SafeVarargs
    public static <T> List<T> newLinkedList(T... elements) {
        return newLinkedList(asList(elements));
    }

    public static <T> List<T> newLinkedList(Collection<? extends T> collection) {
        List<T> newList = newLinkedList();
        return (List<T>) withAll(newList, collection);
    }

    public static <T> List<T> newLinkedList(Enumeration<? extends T> enumeration) {
        List<T> newList = newLinkedList();
        return (List<T>) withAll(newList, enumeration);
    }

    @SafeVarargs
    public static <T> List<T> flatLinkedList(Collection<? extends T>... collections) {
        List<T> newList = newLinkedList();
        return (List<T>) withAllFlat(newList, collections);
    }


    /**
     * Operations
     */

    public static <T> T head(List<T> list) {
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public static <T> List<T> head(int numberOfElements, List<T> list) {
        if (!list.isEmpty()) {
            return list.subList(0, bounded(0, list.size(), numberOfElements));
        }
        return (List<T>) copyOf(list);
    }

    public static <T> List<T> tail(List<T> list) {
        if (!list.isEmpty()) {
            return tail(list.size() - 1, list);
        }
        return null;
    }

    public static <T> List<T> tail(int numberOfElements, List<T> list) {
        if (!list.isEmpty()) {
            int length = list.size();
            return list.subList(bounded(0, length, length - numberOfElements), length);
        }
        return (List<T>) copyOf(list);
    }

    public static <T> T last(List<T> list) {
        if (!list.isEmpty()) {
            return list.get(list.size() - 1);
        }
        return null;
    }

    @SafeVarargs
    public static <T> boolean isPartitionOf(List<T> queriedList, List<? extends T>... partition) {
        boolean sameSize = queriedList.size() == combinedSize(partition);
        if (sameSize) {
            int startIndex = 0;
            for (List<? extends T> subPartition : partition) {
                List<T> subList = queriedList.subList(startIndex, startIndex + subPartition.size());
                if (!subList.equals(subPartition)) {
                    return false;
                }
                startIndex += subPartition.size();
            }
        }
        return sameSize;
    }
}
