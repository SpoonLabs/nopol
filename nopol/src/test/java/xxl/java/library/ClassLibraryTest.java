package xxl.java.library;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static xxl.java.library.ClassLibrary.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

public class ClassLibraryTest {

	@Test
	public void classesOfObjects() {
		List<? extends Class<?>> classes = asList(String.class, ClassLibraryTest.class, OtherClass.class);
		List<? extends Object> objects = asList("p", new ClassLibraryTest(), new OtherClass());
		assertEquals(classes, asClasses(objects));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void hasMethodCheck() {
		assertTrue(hasMethod(Class.class, "getMethod", (List) asList(String.class, Class[].class)));
		assertTrue(hasMethod(String.class, "replaceAll", (List) asList(String.class, String.class)));
		assertFalse(hasMethod(String.class, "replaceWithARegexOrDie", (List) asList(String.class)));
	}
	
	@Test
	public void subclassRelationship() {
		Class<?> string = String.class;
		Class<?> object = Object.class;
		assertTrue(isSubclassOf(object, string));
		assertFalse(isSubclassOf(string, object));
		assertTrue(isSuperclassOf(string, object));
		assertFalse(isSuperclassOf(object, string));
	}
	
	@Test
	public void createInstanceWithReflection() {
		Class<?> objectClass = ClassLibraryTest.class;
		Object object = newInstance(objectClass);
		assertEquals(objectClass, object.getClass());
	}
	
	@Test
	public void comparisonWithReflection() {
		OtherClass one = new OtherClass();
		OtherClass other = new OtherClass();
		assertTrue(isGreaterThan(2, 1));
		assertTrue(isLessThan("AAA", "BBB"));
		assertFalse(isGreaterThan(one, other));
		assertFalse(isLessThan(one, other));
	}
	
	@Test
	public void invocationWithReflection() {
		OtherClass object = new OtherClass();
		Method one = method("one", object.getClass());
		Method print = method("print", object.getClass(), Number.class);
		assertEquals(1, invoke(one, object));
		assertEquals("1", invoke(print, object, 1));
		assertEquals("1.0", invoke(print, object, 1.0));
	}
	
	@Test
	public void invocationWithReflectionToPrivateMethod() {
		OtherClass object = new OtherClass();
		Method printWithSpaces = method("privatePrint", object.getClass(), Number.class);
		assertFalse(printWithSpaces.isAccessible());
		Object result = invokeTrespassing(printWithSpaces, object, 123);
		assertEquals("1#2#3#", result);
		assertFalse(printWithSpaces.isAccessible());
	}
}

class OtherClass {
	public int one() {
		return 1;
	}
	
	public String print(Number number) {
		return number.toString();
	}
	
	@SuppressWarnings("unused")
	private String privatePrint(Number number) {
		String toString = print(number);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < toString.length(); i += 1) {
			builder.append(toString.charAt(i) + "#");
		}
		return builder.toString();
	}
}