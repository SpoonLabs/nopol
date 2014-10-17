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
import xxl.java.container.classic.MetaMap;
import xxl.java.junit.TestCase;
import xxl.java.library.FileLibrary;
import xxl.java.support.Singleton;
import fr.inria.lille.commons.spoon.collectable.CollectableValueFinder;
import fr.inria.lille.commons.spoon.filter.CodeSnippetFilter;
import fr.inria.lille.commons.spoon.util.SpoonElementLibrary;
import fr.inria.lille.commons.spoon.util.SpoonModelLibrary;
import fr.inria.lille.commons.spoon.util.SpoonStatementLibrary;
import fr.inria.lille.repair.infinitel.InfinitelTest;
import fr.inria.lille.repair.nopol.NopolTest;

public class ValuesCollectorTest {

	@Test
	public final void adding_a_Collection_should_add_the_size_and_if_it_is_empty() {
		
		// GIVEN
		String name = "collection";
		Collection<?> value = asList(1, 2, 3);

		// WHEN
		RuntimeValues<?> runtimeValues = RuntimeValues.newInstance();
		runtimeValues.enable();
		runtimeValues.collectInput(name, value);
		
		// THEN
		Map<String, ?> expected = MetaMap.newHashMap(asList(name + "!=null", name + ".size()", name + ".isEmpty()"), asList(true, value.size(), value.isEmpty()));
		assertEquals(expected, runtimeValues.valueBuffer());
	}

	@Test
	public final void adding_a_Map_should_add_the_size_and_if_it_is_empty() {
		// GIVEN
		String name = "map";
		Map<?, ?> value = Collections.singletonMap("key", "value");

		// WHEN
		RuntimeValues<?> runtimeValues = RuntimeValues.newInstance();
		runtimeValues.enable();
		runtimeValues.collectInput(name, value);
		
		// THEN
		Map<String, ?> expected = MetaMap.newHashMap(asList(name + "!=null", name + ".size()", name + ".isEmpty()"), asList(true, value.size(), value.isEmpty()));
		assertEquals(expected, runtimeValues.valueBuffer());
	}

	@Test
	public final void adding_a_CharSequence_should_add_the_length_and_if_it_is_empty() {
		// GIVEN
		String name = "string";
		String value = "Take nothing on its looks; take everything on evidence. There's no better rule.";

		// WHEN
		RuntimeValues<?> runtimeValues = RuntimeValues.newInstance();
		runtimeValues.enable();
		runtimeValues.collectInput(name, value);

		// THEN
		Map<String, ?> expected = MetaMap.newHashMap(asList(name + "!=null", name + ".length()", name + ".length()==0"), asList(true, value.length(), value.isEmpty()));
		assertEquals(expected, runtimeValues.valueBuffer());
	}

	@Test
	public final void adding_an_array_should_add_the_length_also() {
		// GIVEN
		String name = "array";
		int[] value = { 1, 2, 3 };

		// WHEN
		RuntimeValues<?> runtimeValues = RuntimeValues.newInstance();
		runtimeValues.enable();
		runtimeValues.collectInput(name, value);

		// THEN
		Map<String, ?> expected = MetaMap.newHashMap(asList(name + "!=null", name + ".length"), asList(true, value.length));
		assertEquals(expected, runtimeValues.valueBuffer());
	}
	
	@Test
	public final void collectingNullObjectOnlyAddsNullCheck() {
		// GIVEN
		String name = "null";

		// WHEN
		RuntimeValues<?> runtimeValues = RuntimeValues.newInstance();
		runtimeValues.enable();
		runtimeValues.collectInput(name, null);

		// THEN
		Map<String, ?> expected = MetaMap.newHashMap(asList(name + "!=null"), asList(false));
		assertEquals(expected, runtimeValues.valueBuffer());
	}
	
	@Test
	public void collectingCharacters() {
		// GIVEN
		String name = "separator";

		// WHEN
		RuntimeValues<?> runtimeValues = RuntimeValues.newInstance();
		runtimeValues.enable();
		runtimeValues.collectInput(name, ';');

		// THEN
		Map<String, ?> expected = MetaMap.newHashMap(asList(name), asList(';'));
		assertEquals(expected, runtimeValues.valueBuffer());
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void inconsistentSpecificationsAreDiscarded() {
		TestCase testA = TestCase.from("com.example", "testA", 1);
		TestCase testB = TestCase.from("com.example", "testB", 2);
		TestCase testC = TestCase.from("com.example", "testC", 3);
		RuntimeValues<Boolean> runtimeValues = RuntimeValues.newInstance();
		
		Collection<Specification<Boolean>> specifications;
		Collection<Map<String, Object>> inconsistencies;
		Map<String, Object> otherValue = (Map) MetaMap.newHashMap(asList("c"), asList(3));
		Map<String, Object> values = (Map) MetaMap.newHashMap(asList("a", "b"), asList(1, 2));
		Specification<Boolean> consistent = new Specification<Boolean>(otherValue, true);
		Specification<Boolean> inconsistent = new Specification<Boolean>(values, false);
		SpecificationTestCasesListener<Boolean> listener = new SpecificationTestCasesListener<Boolean>(runtimeValues);
		
		listener.processBeforeRun();
		listener.processTestStarted(testA);
		runtimeValues.collectInput("a", values.get("a"));
		runtimeValues.collectInput("b", values.get("b"));
		runtimeValues.collectOutput(false);
		runtimeValues.collectionEnds();
		assertFalse(runtimeValues.isEmpty());
		listener.processSuccessfulRun(testA);
		specifications = listener.specifications();
		assertEquals(1, specifications.size());
		assertTrue(specifications.contains(inconsistent));
		inconsistencies = listener.inconsistentInputs();
		assertTrue(inconsistencies.isEmpty());
		
		listener.processTestStarted(testB);
		runtimeValues.collectInput("c", otherValue.get("c"));
		runtimeValues.collectOutput(true);
		runtimeValues.collectionEnds();
		runtimeValues.collectInput("a", values.get("a"));
		runtimeValues.collectInput("b", values.get("b"));
		runtimeValues.collectOutput(true);
		runtimeValues.collectionEnds();
		assertFalse(runtimeValues.isEmpty());
		listener.processSuccessfulRun(testB);
		specifications = listener.specifications();
		assertEquals(1, specifications.size());
		assertTrue(specifications.contains(consistent));
		inconsistencies = listener.inconsistentInputs();
		assertEquals(1, inconsistencies.size());
		assertTrue(inconsistencies.containsAll(asList(inconsistent.inputs())));
		
		listener.processTestStarted(testC);
		runtimeValues.collectInput("a", values.get("a"));
		runtimeValues.collectInput("b", values.get("b"));
		runtimeValues.collectOutput(true);
		runtimeValues.collectionEnds();
		assertFalse(runtimeValues.isEmpty());
		listener.processSuccessfulRun(testC);
		listener.processAfterRun();
		specifications = listener.specifications();
		assertEquals(1, specifications.size());
		assertTrue(specifications.contains(consistent));
		inconsistencies = listener.inconsistentInputs();
		assertEquals(1, inconsistencies.size());
		assertTrue(inconsistencies.containsAll(asList(inconsistent.inputs())));
	}
	
	@Test
	public void reachedVariablesInExample1() {
		CtElement element = elementInNopolProject(1, "index == 0");
		testReachedVariableNames(element, "s",
										  "index",
										  "nopol_examples.nopol_example_1.NopolExample.this.index",
										  "nopol_examples.nopol_example_1.NopolExample.s");
	}
	
	@Test
	public void reachedVariablesInExample2() {
		CtElement element = elementInNopolProject(2, "(b - a) < 0");
		testReachedVariableNames(element, "b", "a", "nopol_examples.nopol_example_2.NopolExample.this.fieldOfOuterClass");
	}
	
	@Test
	public void reachedVariablesInExample3() {
		CtElement element = elementInNopolProject(3, "tmp != 0");
		testReachedVariableNames(element, "a", "tmp");
	}
	
	@Test
	public void reachedVariablesInExample4() {
		elementInNopolProject(4, "int uninitializedVariableShouldNotBeCollected");
		CtElement element = elementInNopolProject(4, "a = a.substring(1)");
		testReachedVariableNames(element, "a", "initializedVariableShouldBeCollected", "otherInitializedVariableShouldBeCollected");
	}
	
	@Test
	public void reachedVariablesInExample5() {
		CtElement element = elementInNopolProject(5, "r = -1");
		testReachedVariableNames(element, "r", "a", "nopol_examples.nopol_example_5.NopolExample.this.unreachableFromInnterStaticClass");
	}
	
	@Test
	public void reachedVariablesInExample6() {
		CtElement element = elementInNopolProject(6, "a > b");
		testReachedVariableNames(element, "a", "b");
	}
	
	@Test
	public void reachedVariablesInsideConstructor() {
		CtElement element = elementInNopolProject(1, "index = 2 * variableInsideConstructor");
		testReachedVariableNames(element, "variableInsideConstructor");
	}
	
	@Test
	public void reachedVariableOfOuterClass() {
		CtElement element = elementInNopolProject(2, "int result = 29");
		testReachedVariableNames(element, "aBoolean",
										  "nopol_examples.nopol_example_2.NopolExample.InnerNopolExample.this.fieldOfInnerClass",
										  "nopol_examples.nopol_example_2.NopolExample.this.fieldOfOuterClass");
	}
	
	@Test
	public void unreachedVariableInIfBranch() {
		elementInNopolProject(3, "int unreachableVariable");
		CtElement element = elementInNopolProject(3, "(!aBoolean) || (reachableVariable < 2)");
		testReachedVariableNames(element, "aBoolean", "reachableVariable");
	}
	
	@Test
	public void reachedVariableInIfBranch() {
		elementInNopolProject(3, "int uninitializedReachableVariable");
		CtElement element = elementInNopolProject(3, "(!aBoolean) && (uninitializedReachableVariable < 2)");
		testReachedVariableNames(element, "aBoolean", "uninitializedReachableVariable");
	}
	
	@Test
	public void unreachedVariableInInnerStaticClass() {
		elementInNopolProject(5, "private java.lang.Integer unreachableFromInnterStaticClass;");
		CtElement element = elementInNopolProject(5, "!(stringParameter.isEmpty())");
		testReachedVariableNames(element, "stringParameter");	
	}
	
	@Test
	public void fieldOfAnonymousClass() {
		CtElement element = elementInNopolProject(2, "(fieldOfOuterClass) > (limit)");
		testReachedVariableNames(element, "this.limit");
	}
	
	@Test
	public void fieldsOfParameters() {
		CtElement element = elementInClassToSpoon("nested2 != null");
		testReachedVariableNames(element, "comparable",
										  "nested2",
										  "comparable.privateNestedInstanceField",
										  "comparable.publicNestedInstanceField",
										  "comparable.protectedNestedInstanceField",
										  "spoon.example.ClassToSpoon.protectedStaticField",
										  "spoon.example.ClassToSpoon.privateStaticField",
										  "spoon.example.ClassToSpoon.publicStaticField",
										  "nested2.privateInstanceField",
										  "nested2.publicInstanceField",
										  "nested2.protectedInstanceField",
										  "spoon.example.ClassToSpoon.this.publicInstanceField",
										  "spoon.example.ClassToSpoon.this.privateInstanceField",
										  "spoon.example.ClassToSpoon.this.protectedInstanceField");
	}
	
	@Test
	public void fieldsOfParametersFromNestedClass() {
		CtElement element = elementInClassToSpoon("nested != null");
		testReachedVariableNames(element, "comparable",
										  "nested",
										  "comparable.privateNestedInstanceField",
										  "comparable.publicNestedInstanceField",
										  "comparable.protectedNestedInstanceField",
										  "spoon.example.ClassToSpoon.protectedStaticField",
										  "spoon.example.ClassToSpoon.privateStaticField",
										  "spoon.example.ClassToSpoon.publicStaticField",
										  "nested.publicInstanceField",
										  "nested.protectedInstanceField",
										  "spoon.example.ClassToSpoon.NestedClassToSpoon.this.protectedNestedInstanceField",
										  "spoon.example.ClassToSpoon.NestedClassToSpoon.this.publicNestedInstanceField",
										  "spoon.example.ClassToSpoon.NestedClassToSpoon.this.privateNestedInstanceField",
										  "spoon.example.ClassToSpoon.this.publicInstanceField",
										  "spoon.example.ClassToSpoon.this.privateInstanceField",
										  "spoon.example.ClassToSpoon.this.protectedInstanceField");
	}
	
	@Test
	public void fieldsOfParametersFromAnonymousClass() {
		CtElement element = elementInClassToSpoon("comparable != null");
		testReachedVariableNames(element, "comparable",
										  "this.anonymousField",
										  "comparable.privateNestedInstanceField",
										  "comparable.protectedNestedInstanceField",
										  "comparable.publicNestedInstanceField");
	}
	
	@Test
	public void gettersOfFields() {
		CtElement element = elementInInfinitelProject(5, "canKeepConsuming(index, word)");
		testReachedVariableNames(element, "word", "index",
										  "infinitel_examples.infinitel_example_5.InfinitelExample.this.consumer",
										  "infinitel_examples.infinitel_example_5.InfinitelExample.this.consumer.getSize()",
										  "infinitel_examples.infinitel_example_5.InfinitelExample.this.consumer.getConsumed()");
	}
	
	@Test
	public void replaceQuotationMarksToCollectSubconditions() {
		RuntimeValues<Boolean> runtimeValues = RuntimeValues.newInstance();
		String invocation = runtimeValues.invocationOnCollectionOf("\"aaaa\".startsWith(\"b\")");
		String toMatch = "collectInput(\"\\\"aaaa\\\".startsWith(\\\"b\\\")\",\"aaaa\".startsWith(\"b\"))";
		assertTrue(invocation.endsWith(toMatch));
	}
	
	@Test
	public void collectSubexpressionValues() {
		CtElement element = elementInNopolProject(8, "((((a * b) < 11) || (productLowerThan100(a, b))) || (!(a < b))) || ((a = -b) > 0)");
		CtIf ifStatement = (CtIf) testReachedVariableNames(element, "a", "b");
		List<String> expected = asList("0", "11", "a", "b", "-b",
									   "(a * b)",
									   "(a < b)", 
									   "(!(a < b))", 
									   "((a * b) < 11)");
		checkFoundFromIf(ifStatement, expected);
	}
	
	private CtStatement testReachedVariableNames(CtElement element, String... expectedReachedVariables) {
		assertTrue(CtCodeElement.class.isInstance(element));
		CtStatement statement = SpoonStatementLibrary.statementOf((CtCodeElement) element);
		Collection<String> reachedVariables = collectableFinder().findFromStatement(statement);
		System.out.println(reachedVariables);
		assertEquals(expectedReachedVariables.length, reachedVariables.size());
		assertTrue(reachedVariables.containsAll(Arrays.asList(expectedReachedVariables)));
		return statement;
	}
	
	private CtElement elementInNopolProject(int exampleNumber, String codeSnippet) {
		return elementInClass(NopolTest.absolutePathOf(exampleNumber), codeSnippet);
	}
	
	private CtElement elementInInfinitelProject(int exampleNumber, String codeSnippet) {
		return elementInClass(InfinitelTest.absolutePathOf(exampleNumber), codeSnippet);
	}
	
	private CtElement elementInClassToSpoon(String codeSnippet) {
		String sourcePath = "src/test/resources/spoon/example/ClassToSpoon.java";
		return elementInClass(sourcePath, codeSnippet);
	}
	
	private CtElement elementInClass(String sourcePath, String codeSnippet) {
		File file = FileLibrary.fileFrom(sourcePath);
		return elementFromSnippet(file, codeSnippet);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private CtElement elementFromSnippet(File sourceFile, String codeSnippet) {
		Factory model = SpoonModelLibrary.modelFor(sourceFile);
		Filter filter = new CodeSnippetFilter(sourceFile, codeSnippet);
		List<CtElement> elements = SpoonElementLibrary.filteredElements(model, filter);
		assertEquals(1, elements.size());
		return elements.get(0);
	}
	
	private void checkFoundFromIf(CtIf ifStatement, Collection<String> expected) {
		Collection<String> actual = collectableFinder().findFromIf(ifStatement);
		System.out.println(actual);
		assertEquals(expected.size(), actual.size());
		assertTrue(actual.containsAll(expected));
	}
	
	private CollectableValueFinder collectableFinder() {
		return Singleton.of(CollectableValueFinder.class);
	}
}
