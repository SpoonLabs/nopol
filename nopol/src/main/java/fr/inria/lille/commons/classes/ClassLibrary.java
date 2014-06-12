package fr.inria.lille.commons.classes;

public class ClassLibrary {

	public static boolean isInstanceOf(Class<?> aClass, Object object) {
		return aClass.isInstance(object);
	}
	
	public static <T> T castTo(Class<T> aClass, Object object) {
		return (T) object;
	}
}
