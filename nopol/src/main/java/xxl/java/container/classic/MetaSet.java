package xxl.java.container.classic;

import xxl.java.support.Factory;

import java.util.*;

import static java.util.Arrays.asList;
import static xxl.java.container.classic.MetaCollection.withAll;
import static xxl.java.container.classic.MetaCollection.withAllFlat;
import static xxl.java.library.NumberLibrary.ifNegative;

public class MetaSet {

    /**
     * Factory
     */

    public static <T> Factory<Set<T>> hashSetFactory() {
        return new Factory<Set<T>>() {
            @Override
            public Set<T> newInstance() {
                return newHashSet();
            }
        };
    }

    public static <T> Factory<Set<T>> linkedHashSetFactory() {
        return new Factory<Set<T>>() {
            @Override
            public Set<T> newInstance() {
                return newLinkedHashSet();
            }
        };
    }


    /**
     * HashSet
     */

    public static <T> Set<T> newHashSet() {
        return newHashSet(1);
    }

    public static <T> Set<T> newHashSet(int initialCapacity) {
        return new HashSet<T>(ifNegative(initialCapacity, 1));
    }

    @SafeVarargs
    public static <T> Set<T> newHashSet(T... elements) {
        return newHashSet(asList(elements));
    }

    public static <T> Set<T> newHashSet(Collection<? extends T> collection) {
        Set<T> newSet = newHashSet(collection.size());
        return (Set<T>) withAll(newSet, collection);
    }

    public static <T> Set<T> newHashSet(Enumeration<? extends T> enumeration) {
        Set<T> newSet = newHashSet();
        return (Set<T>) withAll(newSet, enumeration);
    }

    @SafeVarargs
    public static <T> Set<T> flatHashSet(Collection<? extends T>... collections) {
        Set<T> newSet = newHashSet(collections.length);
        return (Set<T>) withAllFlat(newSet, collections);
    }


    /**
     * LinkedHashSet
     */

    public static <T> Set<T> newLinkedHashSet() {
        return newLinkedHashSet(1);
    }

    public static <T> Set<T> newLinkedHashSet(int initialCapacity) {
        return new LinkedHashSet<T>(ifNegative(initialCapacity, 1));
    }

    @SafeVarargs
    public static <T> Set<T> newLinkedHashSet(T... elements) {
        return newLinkedHashSet(asList(elements));
    }

    public static <T> Set<T> newLinkedHashSet(Collection<? extends T> collection) {
        Set<T> newSet = newLinkedHashSet(collection.size());
        return (Set<T>) withAll(newSet, collection);
    }

    public static <T> Set<T> newLinkedHashSet(Enumeration<? extends T> enumeration) {
        Set<T> newSet = newLinkedHashSet();
        return (Set<T>) withAll(newSet, enumeration);
    }

    @SafeVarargs
    public static <T> Set<T> flatLinkedHashSet(Collection<? extends T>... collections) {
        Set<T> newSet = newLinkedHashSet(collections.length);
        return (Set<T>) withAllFlat(newSet, collections);
    }
}
