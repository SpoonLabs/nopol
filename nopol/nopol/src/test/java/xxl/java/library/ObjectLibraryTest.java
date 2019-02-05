package xxl.java.library;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static xxl.java.library.ObjectLibrary.methodIdentity;
import static xxl.java.library.ObjectLibrary.methodToString;
import static xxl.java.library.ObjectLibrary.methodYourself;

import org.junit.Test;

import xxl.java.support.Function;

public class ObjectLibraryTest {

	@Test
	public void methodToStringReturnsString() {
		assertEquals("1", methodToString().outputFor(1));
		assertEquals("true", methodToString().outputFor(true));
		assertEquals(getClass().toString(), methodToString().outputFor(getClass()));
	}
	
	@Test
	public void methodIdentityReturnsSameObject() {
		Integer three = 3;
		Function<Object, Integer> identity = methodIdentity(three);
		assertEquals(three, identity.outputFor("1"));
		assertEquals(three, identity.outputFor(2));
		three = null;
		assertEquals(Integer.valueOf(3), identity.outputFor(null));
		assertTrue(null == methodIdentity(three).outputFor("1"));
		assertTrue(null == methodIdentity(null).outputFor("1"));
	}
	
	@Test
	public void methodYourselfReturnsArgument() {
		for (Object object : asList(1, 2, true, "AAA", 'c')) {
			assertTrue(object == methodYourself().outputFor(object));
		}
	}
}
