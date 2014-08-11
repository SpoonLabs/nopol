package fr.inria.lille.commons.trace;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.trace.Specification;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SpecificationTest {

	@Test
	public void specificationLivesAfterClearingMap() {
		Map<String, Object> values = (Map) MapLibrary.newHashMap(asList("a", "b"), asList(1, 2));
		Specification<Character> specification = new Specification<Character>(values, '1');
		assertFalse(specification.inputs().isEmpty());
		assertTrue(specification.inputs().containsKey("a"));
		assertTrue(specification.inputs().containsKey("b"));
		values.clear();
		assertFalse(specification.inputs().isEmpty());
		assertTrue(specification.inputs().containsKey("a"));
		assertTrue(specification.inputs().containsKey("b"));
	}
}
