package fr.inria.lille.commons.classes;

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
	
}
