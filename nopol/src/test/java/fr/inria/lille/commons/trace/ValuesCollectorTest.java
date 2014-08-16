package fr.inria.lille.commons.trace;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;
import xxl.java.extensions.collection.MapLibrary;
import xxl.java.extensions.junit.TestCase;
import xxl.java.extensions.support.Function;
import xxl.java.extensions.support.Singleton;
import fr.inria.lille.commons.spoon.collectable.CollectableValueFinder;
import fr.inria.lille.commons.spoon.filter.CodeSnippetFilter;
import fr.inria.lille.commons.spoon.util.SpoonElementLibrary;
import fr.inria.lille.commons.spoon.util.SpoonModelLibrary;
import fr.inria.lille.commons.spoon.util.SpoonStatementLibrary;
import fr.inria.lille.repair.nopol.NopolTest;

public class ValuesCollectorTest {
	
	@Test
	public final void adding_a_Collection_should_add_the_size_and_if_it_is_empty() {
		
		// GIVEN
		String name = "collection";
		Collection<?> value = asList(1, 2, 3);

		// WHEN
		RuntimeValues runtimeValues = RuntimeValues.newInstance();
		runtimeValues.enable();
		runtimeValues.collectValue(name, value);
		
		// THEN
		Map<String, ?> expected = MapLibrary.newHashMap(asList(name + "!=null", name + ".size()", name + ".isEmpty()"), asList(true, value.size(), value.isEmpty()));
		assertEquals(expected, runtimeValues.valueBuffer());
	}

	@Test
	public final void adding_a_Map_should_add_the_size_and_if_it_is_empty() {
		// GIVEN
		String name = "map";
		Map<?, ?> value = Collections.singletonMap("key", "value");

		// WHEN
		RuntimeValues runtimeValues = RuntimeValues.newInstance();
		runtimeValues.enable();
		runtimeValues.collectValue(name, value);
		
		// THEN
		Map<String, ?> expected = MapLibrary.newHashMap(asList(name + "!=null", name + ".size()", name + ".isEmpty()"), asList(true, value.size(), value.isEmpty()));
		assertEquals(expected, runtimeValues.valueBuffer());
	}

	@Test
	public final void adding_a_CharSequence_should_add_the_length_and_if_it_is_empty() {
		// GIVEN
		String name = "string";
		String value = "Take nothing on its looks; take everything on evidence. There's no better rule.";

		// WHEN
		RuntimeValues runtimeValues = RuntimeValues.newInstance();
		runtimeValues.enable();
		runtimeValues.collectValue(name, value);

		// THEN
		Map<String, ?> expected = MapLibrary.newHashMap(asList(name + "!=null", name + ".length()", name + ".length()==0"), asList(true, value.length(), value.isEmpty()));
		assertEquals(expected, runtimeValues.valueBuffer());
	}

	@Test
	public final void adding_an_array_should_add_the_length_also() {
		// GIVEN
		String name = "array";
		int[] value = { 1, 2, 3 };

		// WHEN
		RuntimeValues runtimeValues = RuntimeValues.newInstance();
		runtimeValues.enable();
		runtimeValues.collectValue(name, value);

		// THEN
		Map<String, ?> expected = MapLibrary.newHashMap(asList(name + "!=null", name + ".length"), asList(true, value.length));
		assertEquals(expected, runtimeValues.valueBuffer());
	}
	
	@Test
	public final void collectingNullObjectOnlyAddsNullCheck() {
		// GIVEN
		String name = "null";

		// WHEN
		RuntimeValues runtimeValues = RuntimeValues.newInstance();
		runtimeValues.enable();
		runtimeValues.collectValue(name, null);

		// THEN
		Map<String, ?> expected = MapLibrary.newHashMap(asList(name + "!=null"), asList(false));
		assertEquals(expected, runtimeValues.valueBuffer());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void inconsistentSpecificationsAreDiscarded() {
		TestCase testA = new TestCase("com.example", "testA");
		TestCase testB = new TestCase("com.example", "testB");
		TestCase testC = new TestCase("com.example", "testC");
		RuntimeValues runtimeValues = RuntimeValues.newInstance();
		Function<Integer, Boolean> outputForEachTrace = new Function<Integer, Boolean>() {
			@Override
			public Boolean outputFor(Integer value) {
				return value % 2 == 0;
			}
		};
		Map<String, Object> otherValue = (Map) MapLibrary.newHashMap(asList("c"), asList(3));
		Map<String, Object> values = (Map) MapLibrary.newHashMap(asList("a", "b"), asList(1, 2));
		Collection<Map<String, Object>> inconsistentTraces;
		SpecificationTestCasesListener<Boolean> listener = new SpecificationTestCasesListener<Boolean>(runtimeValues, outputForEachTrace);
		
		listener.processBeforeRun();
		runtimeValues.collectValue("a", values.get("a"));
		runtimeValues.collectValue("b", values.get("b"));
		runtimeValues.collectionEnds();
		assertFalse(runtimeValues.isEmpty());
		assertEquals(1, runtimeValues.numberOfTraces());
		listener.processSuccessfulRun(testA);
		listener.processTestFinished(testA);
		inconsistentTraces = listener.inconsistentTraces();
		assertTrue(inconsistentTraces.isEmpty());
		
		runtimeValues.collectValue("c", otherValue.get("c"));
		runtimeValues.collectionEnds();
		runtimeValues.collectValue("a", values.get("a"));
		runtimeValues.collectValue("b", values.get("b"));
		runtimeValues.collectionEnds();
		assertFalse(runtimeValues.isEmpty());
		assertEquals(2, runtimeValues.numberOfTraces());
		listener.processSuccessfulRun(testB);
		listener.processTestFinished(testB);
		inconsistentTraces = listener.inconsistentTraces();
		assertEquals(1, inconsistentTraces.size());
		assertTrue(inconsistentTraces.contains(values));
		
		runtimeValues.collectValue("a", values.get("a"));
		runtimeValues.collectValue("b", values.get("b"));
		runtimeValues.collectionEnds();
		assertFalse(runtimeValues.isEmpty());
		assertEquals(1, runtimeValues.numberOfTraces());
		listener.processSuccessfulRun(testC);
		listener.processTestFinished(testC);
		listener.processAfterRun();
		inconsistentTraces = listener.inconsistentTraces();
		assertEquals(1, inconsistentTraces.size());
		assertTrue(inconsistentTraces.contains(values));
		
		Collection<Specification<Boolean>> specifications = listener.specifications();
		assertEquals(1, specifications.size());
		assertTrue(specifications.contains(new Specification<Boolean>(otherValue, true)));
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
		existsCodeSnippet(5, "private java.lang.Integer unreachableFromInnterStaticClass;");
		testReachedVariableNames(5, "!(stringParameter.isEmpty())", "stringParameter");	
	}
	
	@Test
	public void fieldOfAnonymousClass() {
		testReachedVariableNames(2, "(fieldOfOuterClass) > (limit)", "this.limit");
	}
	
	@Test
	public void replaceQuotationMarksToCollectSubconditions() {
		RuntimeValues runtimeValues = RuntimeValues.newInstance();
		String invocation = runtimeValues.invocationOnCollectionOf("\"aaaa\".startsWith(\"b\")");
		String toMatch = "collectValue(\"\\\"aaaa\\\".startsWith(\\\"b\\\")\", \"aaaa\".startsWith(\"b\"))";
		assertTrue(invocation.endsWith(toMatch));
	}
	
	@Test
	public void collectSubexpressionValues() {
		CtIf ifStatement = (CtIf) testReachedVariableNames(8, "((a * b) < 11) || (productLowerThan100(a, b))", "a", "b");
		List<String> expected = asList("a", "b", "((a * b) < 11)", "11", "(a * b)", "(productLowerThan100(a, b))", "((a * b) < 11) || (productLowerThan100(a, b))");
		checkFoundFromIf(ifStatement, expected);
	}
	
	private void checkFoundFromIf(CtIf ifStatement, Collection<String> expected) {
		Collection<String> collectablesFromIf = collectableFinder().findFromIf(ifStatement);
		assertEquals(expected.size(), collectablesFromIf.size());
		assertTrue(collectablesFromIf.containsAll(expected));
	}
	
	private CtStatement testReachedVariableNames(int exampleNumber, String codeSnippet, String... expectedReachedVariables) {
		CtElement firstElement = existsCodeSnippet(exampleNumber, codeSnippet);
		assertTrue(CtCodeElement.class.isInstance(firstElement));
		CtStatement statement = SpoonStatementLibrary.statementOf((CtCodeElement) firstElement);
		Collection<String> reachedVariables = collectableFinder().findFromStatement(statement);
		assertEquals(expectedReachedVariables.length, reachedVariables.size());
		assertTrue(reachedVariables.containsAll(Arrays.asList(expectedReachedVariables)));
		return statement;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private CtElement existsCodeSnippet(int exampleNumber, String codeSnippet) {
		File sourceFile = NopolTest.projectForExample(exampleNumber).sourceFile();
		Factory model = SpoonModelLibrary.modelFor(sourceFile);
		Filter filter = new CodeSnippetFilter(sourceFile, codeSnippet);
		List<CtElement> elements = SpoonElementLibrary.filteredElements(model, filter);
		assertEquals(1, elements.size());
		return elements.get(0);
	}
	
	private CollectableValueFinder collectableFinder() {
		return Singleton.of(CollectableValueFinder.class);
	}
}
