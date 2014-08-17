package xxl.container.classic;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static xxl.java.container.classic.MetaSet.flatHashSet;
import static xxl.java.container.classic.MetaSet.flatLinkedHashSet;
import static xxl.java.container.classic.MetaSet.newHashSet;
import static xxl.java.container.classic.MetaSet.newLinkedHashSet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

import org.junit.Test;

public class MetaSetTest {

	@Test
	public void setAddAllInOne() {
		Set<String> firstSet = newLinkedHashSet("a", "b", "c");
		Set<String> secondSet  = newLinkedHashSet("d", "e", "f");
		Set<String> combined = flatLinkedHashSet(firstSet, secondSet, firstSet);
		Set<String> hashedCombined = flatHashSet(firstSet, secondSet, firstSet);
		assertEquals(firstSet.size() + secondSet.size(), combined.size());
		assertTrue(combined.containsAll(firstSet));
		assertTrue(combined.containsAll(secondSet));
		assertEquals(asList(combined.toArray()).subList(0, 3), asList(firstSet.toArray()));
		assertEquals(asList(combined.toArray()).subList(3, 6), asList(secondSet.toArray()));
		assertEquals(combined, hashedCombined);
	}
	
	@Test
	public void hashSetConstructorWithEnumeration() {
		Enumeration<String> enumeration = Collections.enumeration(asList("a", "b", "b", "c"));
		Set<String> hashSet = newHashSet(enumeration);
		assertEquals(3, hashSet.size());
		assertTrue(hashSet.containsAll(asList("a", "b", "c")));
	}
	
	@Test
	public void linkedHashSetConstructorWithEnumeration() {
		Enumeration<String> enumeration = Collections.enumeration(asList("a", "b", "b", "c"));
		Set<String> linkedSet = newLinkedHashSet(enumeration);
		assertEquals(3, linkedSet.size());
		assertTrue(linkedSet.containsAll(asList("a", "b", "c")));
	}
}
