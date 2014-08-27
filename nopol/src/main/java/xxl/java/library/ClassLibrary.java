package xxl.java.library;

import static java.lang.String.format;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

import xxl.java.container.classic.MetaList;

public class ClassLibrary {

	public static <T> T newInstance(Class<T> theClass) {
		try {
			return theClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Colud not instantiate " + theClass,e);
		}
	}
	
	public static Object invoke(Method method, Object receiver, Object... arguments) {
		try {
			return method.invoke(receiver, arguments);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(format("Failed invocation to %s", method.toString()), e);
		}
	}
	
	public static Object invokeTrespassing(Method method, Object receiver, Object... arguments) {
		boolean oldValue = method.isAccessible();
		method.setAccessible(true);
		try {
			Object response = invoke(method, receiver, arguments);
			return response;
		} catch (Exception e) {
			throw e;
		} finally {
			method.setAccessible(oldValue);
		}
	}

	public static Method method(String methodName, Class<?> aClass, Collection<Class<?>> argumentClasses) {
		return method(methodName, aClass, argumentClasses.toArray(new Class[argumentClasses.size()]));
	}
	
	public static Method method(String methodName, Class<?> aClass, Class<?>... argumentClasses) {
		try {
			return aClass.getDeclaredMethod(methodName, argumentClasses);
		} catch (NoSuchMethodException nsme) {
			throw new RuntimeException(format("Method not found %s#%s", aClass.getName(), methodName), nsme);
		}
	}
	
	public static List<Class<?>> asClasses(Collection<? extends Object> objects) {
		List<Class<?>> classes = MetaList.newArrayList(objects.size());
		for (Object object : objects) {
			classes.add(object.getClass());
		}
		return classes;
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
	
	public static <T> boolean isGreaterThan(T compared, T inRelationTo) {
		return comparison(compared, inRelationTo) > 0;
	}
	
	public static <T> boolean isLessThan(T compared, T inRelationTo) {
		return comparison(compared, inRelationTo) < 0;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> int comparison(T one, T other) {
		if (isInstanceOf(Comparable.class, one)) {
			return ((Comparable<T>) one).compareTo(other);
		}
		return 0;
	}
}
