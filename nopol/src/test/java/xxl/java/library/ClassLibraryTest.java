package xxl.java.library;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static xxl.java.library.ClassLibrary.asClasses;
import static xxl.java.library.ClassLibrary.invoke;
import static xxl.java.library.ClassLibrary.isGreaterThan;
import static xxl.java.library.ClassLibrary.isLessThan;
import static xxl.java.library.ClassLibrary.method;
import static xxl.java.library.ClassLibrary.newInstance;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

public class ClassLibraryTest {

	public class InnerClass {
		public int one() {
			return 1;
		}
		
		public String print(Number number) {
			return number.toString();
		}
	}
	
	@Test
	public void classesOfObjects() {
		List<? extends Class<?>> classes = asList(String.class, ClassLibraryTest.class, InnerClass.class);
		List<? extends Object> objects = asList("p", new ClassLibraryTest(), new InnerClass());
		assertEquals(classes, asClasses(objects));
	}
	
	@Test
	public void createInstanceWithReflection() {
		Class<?> objectClass = ClassLibraryTest.class;
		Object object = newInstance(objectClass);
		assertEquals(objectClass, object.getClass());
	}
	
	@Test
	public void comparisonWithReflection() {
		InnerClass one = new InnerClass();
		InnerClass other = new InnerClass();
		assertTrue(isGreaterThan(2, 1));
		assertTrue(isLessThan("AAA", "BBB"));
		assertFalse(isGreaterThan(one, other));
		assertFalse(isLessThan(one, other));
	}
	
	@Test
	public void invocationWithReflection() {
		InnerClass inner = new InnerClass();
		Method one = method("one", inner.getClass());
		Method print = method("print", inner.getClass(), Number.class);
		assertEquals(1, invoke(one, inner));
		assertEquals("1", invoke(print, inner, 1));
		assertEquals("1.0", invoke(print, inner, 1.0));
	}
}
