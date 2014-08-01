package fr.inria.lille.commons.trace.collector;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;
import fr.inria.lille.commons.spoon.CodeSnippetFilter;
import fr.inria.lille.commons.spoon.SpoonLibrary;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.commons.trace.RuntimeValuesProcessor;
import fr.inria.lille.toolset.NopolTest;

public class ValuesCollectorTest {

	@Before
	public void setUp() {
		RuntimeValues.discardCollectedValues();
	}
	
	@After
	public void tearDown() {
		RuntimeValues.discardCollectedValues();
	}
	
	@Test
	public final void adding_a_Collection_should_add_the_size_and_if_it_is_empty() {
		
		// GIVEN
		String name = "collection";
		Collection<?> value = asList(1, 2, 3);

		// WHEN
		RuntimeValues.collectValue(name, value);
		
		// THEN
		Iterator<Entry<String, Object>> iterator = RuntimeValues.collectedValues().iterator();

		Entry<String, Object> isEmpty = iterator.next();
		assertEquals(name + ".isEmpty()", isEmpty.getKey());
		assertEquals(value.isEmpty(), isEmpty.getValue());

		Entry<String, Object> size = iterator.next();
		assertEquals(name + ".size()", size.getKey());
		assertEquals(value.size(), size.getValue());

		Entry<String, Object> isNotNull = iterator.next();
		assertEquals(name + "!=null", isNotNull.getKey());
		assertTrue((Boolean) isNotNull.getValue());

		assertFalse(iterator.hasNext());
	}

	@Test
	public final void adding_a_Map_should_add_the_size_and_if_it_is_empty() {
		// GIVEN
		String name = "map";
		Map<?, ?> value = Collections.singletonMap("key", "value");

		// WHEN
		RuntimeValues.collectValue(name, value);
		
		// THEN
		Iterator<Entry<String, Object>> iterator = RuntimeValues.collectedValues().iterator();

		Entry<String, Object> size = iterator.next();
		assertEquals(name + ".size()", size.getKey());
		assertEquals(value.size(), size.getValue());

		Entry<String, Object> isNotNull = iterator.next();
		assertEquals(name + "!=null", isNotNull.getKey());
		assertTrue((Boolean) isNotNull.getValue());

		Entry<String, Object> isEmpty = iterator.next();
		assertEquals(name + ".isEmpty()", isEmpty.getKey());
		assertEquals(value.isEmpty(), isEmpty.getValue());

		assertFalse(iterator.hasNext());
	}

	@Test
	public final void adding_a_CharSequence_should_add_the_length_and_if_it_is_empty() {
		// GIVEN
		String name = "string";
		String value = "Take nothing on its looks; take everything on evidence. There's no better rule.";

		// WHEN
		RuntimeValues.collectValue(name, value);

		// THEN
		Iterator<Entry<String, Object>> iterator = RuntimeValues.collectedValues().iterator();

		Entry<String, Object> length = iterator.next();
		assertEquals(name + ".length()", length.getKey());
		assertEquals(value.length(), length.getValue());

		Entry<String, Object> isNotNull = iterator.next();
		assertEquals(name + "!=null", isNotNull.getKey());
		assertTrue((Boolean) isNotNull.getValue());

		Entry<String, Object> isEmpty = iterator.next();
		assertEquals(name + ".length()==0", isEmpty.getKey());
		assertEquals(value.isEmpty(), isEmpty.getValue());

		assertFalse(iterator.hasNext());
	}

	@Test
	public final void adding_an_array_should_add_the_length_also() {
		// GIVEN
		String name = "array";
		int[] value = { 1, 2, 3 };

		// WHEN
		RuntimeValues.collectValue(name, value);

		// THEN
		Iterator<Entry<String, Object>> iterator = RuntimeValues.collectedValues().iterator();
		Entry<String, Object> entry = iterator.next();
		assertEquals(name + ".length", entry.getKey());
		assertEquals(value.length, entry.getValue());

		Entry<String, Object> isNotNull = iterator.next();
		assertEquals(name + "!=null", isNotNull.getKey());
		assertTrue((Boolean) isNotNull.getValue());

		assertFalse(iterator.hasNext());
	}
	
	@Test
	public final void collectingNullObjectOnlyAddsNullCheck() {
		// GIVEN
		String name = "null";

		// WHEN
		RuntimeValues.collectValue(name, null);

		// THEN
		Iterator<Entry<String, Object>> iterator = RuntimeValues.collectedValues().iterator();
		Entry<String, Object> isNotNull = iterator.next();
		assertEquals(name + "!=null", isNotNull.getKey());
		assertFalse((Boolean) isNotNull.getValue());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void reachedVariablesInExample1() {
		testReachedVariableNames(1, "index == 0", "index", "s", "NopolExample.this.index", "NopolExample.s");
	}
	
	@Test
	public void reachedVariablesInExample2() {
		testReachedVariableNames(2, "(b - a) < 0", "b", "a", "NopolExample.this.fieldOfOuterClass");
	}
	
	@Test
	public void reachedVariablesInExample3() {
		testReachedVariableNames(3, "tmp != 0", "a", "tmp");
	}
	
	@Test
	public void reachedVariablesInExample4() {
		existsCodeSnippet(4, "int uninitializedVariableShouldNotBeCollected");
		testReachedVariableNames(4, "a = a.substring(1)", "a", "initializedVariableShouldBeCollected", "otherInitializedVariableShouldBeCollected");
	}
	
	@Test
	public void reachedVariablesInExample5() {
		testReachedVariableNames(5, "r = -1", "r", "a", "NopolExample.this.unreachableFromInnterStaticClass");
	}
	
	@Test
	public void reachedVariablesInExample6() {
		testReachedVariableNames(6, "a > b", "a", "b");
	}
	
	@Test
	public void reachedVariablesInsideConstructor() {
		testReachedVariableNames(1, "index = 2 * variableInsideConstructor", "variableInsideConstructor");
	}
	
	@Test
	public void reachedVariableOfOuterClass() {
		testReachedVariableNames(2, "int result = 29", "aBoolean", "NopolExample.this.fieldOfOuterClass", "InnerNopolExample.this.fieldOfInnerClass");
	}
	
	@Test
	public void unreachedVariableInIfBranch() {
		existsCodeSnippet(3, "int unreachableVariable");
		testReachedVariableNames(3, "(!aBoolean) || (reachableVariable < 2)", "aBoolean", "reachableVariable");
	}
	
	@Test
	public void reachedVariableInIfBranch() {
		existsCodeSnippet(3, "int uninitializedReachableVariable");
		testReachedVariableNames(3, "(!aBoolean) && (uninitializedReachableVariable < 2)", "aBoolean", "uninitializedReachableVariable");
	}
	
	@Test
	public void unreachedVariableInInnerStaticClass() {
		existsCodeSnippet(5, "private int unreachableFromInnterStaticClass;");
		testReachedVariableNames(5, "!(stringParameter.isEmpty())", "stringParameter");	
	}
	
	@Test
	public void fieldOfAnonymousClass() {
		testReachedVariableNames(2, "(fieldOfOuterClass) > (limit)", "this.limit");
	}
	
	private void testReachedVariableNames(int exampleNumber, String codeSnippet, String... expectedReachedVariables) {
		CtElement firstElement = existsCodeSnippet(exampleNumber, codeSnippet);
		assertTrue(CtCodeElement.class.isInstance(firstElement));
		CtStatement statement = SpoonLibrary.statementOf((CtCodeElement) firstElement);
		Collection<String> reachedVariables = new RuntimeValuesProcessor<>().reachableVariableNames(statement);
		assertEquals(expectedReachedVariables.length, reachedVariables.size());
		assertTrue(reachedVariables.containsAll(Arrays.asList(expectedReachedVariables)));
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private CtElement existsCodeSnippet(int exampleNumber, String codeSnippet) {
		File sourceFile = NopolTest.example(exampleNumber).sourceFile();
		Factory model = SpoonLibrary.modelFor(sourceFile);
		Filter filter = new CodeSnippetFilter(sourceFile, codeSnippet);
		List<CtElement> elements = SpoonLibrary.filteredElements(model, filter);
		assertEquals(1, elements.size());
		return elements.get(0);
	}
}
