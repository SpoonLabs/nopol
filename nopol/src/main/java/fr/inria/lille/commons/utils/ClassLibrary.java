package fr.inria.lille.commons.utils;

import java.lang.reflect.Modifier;

public class ClassLibrary {

	public static <T> T castTo(Class<T> aClass, Object object) {
		return (T) object;
	}
	
	public static boolean isInstanceOf(Class<?> aClass, Object object) {
		return aClass.isInstance(object);
	}
	
	public static boolean isSuperclassOf(Class<?> aClass, Class<?> queriedClass) {
		return queriedClass.isAssignableFrom(aClass);
	}
	
	public static boolean isSubclassOf(Class<?> aClass, Class<?> queriedClass) {
		return aClass.isAssignableFrom(queriedClass);
	}
	
	public static boolean isAbstract(Class<?> aClass) {
		return Modifier.isAbstract(aClass.getModifiers());
	}
	
	public static <T> boolean isGreaterThan(T comparedOne, T comparingOne) {
		return comparison(comparingOne, comparedOne) > 0;
	}
	
	public static <T> boolean isLessThan(T comparedOne, T comparingOne) {
		return comparison(comparingOne, comparedOne) < 0;
	}
	
	public static <T> int comparison(T one, T other) {
		if (isInstanceOf(Comparable.class, one)) {
			return ((Comparable<T>) one).compareTo(other);
		}
		return 0;
	}
}
