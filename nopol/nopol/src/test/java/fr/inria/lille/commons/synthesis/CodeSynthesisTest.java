package fr.inria.lille.commons.synthesis;

import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.expression.ObjectTemplate;
import fr.inria.lille.commons.synthesis.operator.BinaryOperator;
import fr.inria.lille.commons.synthesis.operator.Operator;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.commons.synthesis.theory.*;
import fr.inria.lille.commons.trace.Specification;
import org.junit.Test;
import org.smtlib.ISort;
import xxl.java.container.classic.MetaMap;
import xxl.java.container.classic.MetaSet;
import xxl.java.container.map.Multimap;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CodeSynthesisTest {

	@Test
	public void useRealInsteadOfIntegerToSolveScript() {
		ConstraintBasedSynthesis synthesiser = new ConstraintBasedSynthesis();
		Expression<?> outputExpression = synthesiser.outputExpressionFor(Integer.class);
		assertEquals(SMTLib.numberSort(), outputExpression.smtSort());

		Map<String, Integer> inputs = MetaMap.newHashMap(asList("a", "b"), asList(1, 2));
		Collection<Expression<?>> inputExpressions = synthesiser.inputExpressions((Collection) asList(inputs), outputExpression);
		assertEquals(2, inputExpressions.size());
		Multimap<ISort, ObjectTemplate<?>> bySort = ObjectTemplate.bySort((List) inputExpressions);
		assertEquals(MetaSet.newHashSet(SMTLib.numberSort()), bySort.keySet());
	}

	@Test
	public void scriptResolutionWithoutComponents() {
		Collection<OperatorTheory> theories = (List) asList(new EmptyTheory());
		ConstraintBasedSynthesis synthesiser = new ConstraintBasedSynthesis(SolverFactory.solverLogic(), (Map) MetaMap.newHashMap(), theories);
		Map<String, Object> firstValues = (Map) MetaMap.newHashMap(asList("array.length", "iterations"), asList(10, 15));
		Map<String, Object> secondValues = (Map) MetaMap.newHashMap(asList("array.length", "iterations"), asList(10, 7));
		Specification firstSpecification = new Specification<>(firstValues, 10);
		Specification secondSpecification = new Specification<>(secondValues, 10);
		CodeGenesis genesis = synthesiser.codesSynthesisedFrom(Number.class, (List) asList(firstSpecification, secondSpecification));
		assertTrue(genesis.isSuccessful());
		assertEquals("array.length", genesis.returnStatement());
	}

	@Test
	public void scriptResolutionWithoutInputs() {
		ConstraintBasedSynthesis synthesiser = new ConstraintBasedSynthesis();
		Map<String, Object> firstValues = MetaMap.newHashMap();
		Map<String, Object> secondValues = MetaMap.newHashMap();
		Specification firstSpecification = new Specification<Integer>(firstValues, 0);
		Specification secondSpecification = new Specification<Integer>(secondValues, 0);
		CodeGenesis genesis = synthesiser.codesSynthesisedFrom(Number.class, (List) asList(firstSpecification, secondSpecification));
		assertTrue(genesis.isSuccessful());
		assertEquals("0", genesis.returnStatement());
	}

	@Test
	public void scriptResolutionWithOneTheory() {
		Collection<OperatorTheory> theories = (List) asList(new LinearTheory());
		ConstraintBasedSynthesis synthesiser = new ConstraintBasedSynthesis(SolverFactory.solverLogic(), (Map) MetaMap.newHashMap(), theories);
		Map<String, Object> firstValues = (Map) MetaMap.newHashMap(asList("array.length", "iterations"), asList(10, 10));
		Map<String, Object> secondValues = (Map) MetaMap.newHashMap(asList("array.length", "iterations"), asList(15, 5));
		Specification firstSpecification = new Specification<Integer>(firstValues, 20);
		Specification secondSpecification = new Specification<Integer>(secondValues, 20);
		CodeGenesis genesis = synthesiser.codesSynthesisedFrom(Number.class, (List) asList(firstSpecification, secondSpecification));
		assertTrue(genesis.isSuccessful());
		assertTrue(genesis.returnStatement(), asList("iterations + array.length", "array.length + iterations").contains(genesis.returnStatement()));
	}

	@Test
	public void scriptResolutionWithOneTheoryBooleanOutput() {
		Collection<OperatorTheory> theories = (List) asList(new NumberComparisonTheory());
		ConstraintBasedSynthesis synthesiser = new ConstraintBasedSynthesis(SolverFactory.solverLogic(), (Map) MetaMap.newHashMap(), theories);
		Map<String, Object> firstValues = (Map) MetaMap.newHashMap(asList("array.length", "iterations"), asList(4, 15));
		Map<String, Object> secondValues = (Map) MetaMap.newHashMap(asList("array.length", "iterations"), asList(16, 5));
		Map<String, Object> thirdValues = (Map) MetaMap.newHashMap(asList("array.length", "iterations"), asList(16, 16));
		Specification<Boolean> firstSpecification = new Specification<>(firstValues, false);
		Specification<Boolean> secondSpecification = new Specification<>(secondValues, true);
		Specification<Boolean> thirdSpecification = new Specification<>(thirdValues, true);
		CodeGenesis genesis = synthesiser.codesSynthesisedFrom(Boolean.class, (List) asList(firstSpecification, secondSpecification, thirdSpecification));
		assertTrue(genesis.isSuccessful());
		assertEquals(genesis.returnStatement(), "iterations <= array.length", genesis.returnStatement());
	}

	@Test
	public void scriptResolutionWithTwoTheories() {
		Collection<OperatorTheory> theories = (List) asList(new IfThenElseTheory(), new LinearTheory());
		ConstraintBasedSynthesis synthesiser = new ConstraintBasedSynthesis(SolverFactory.solverLogic(), (Map) MetaMap.newHashMap(asList("1"), asList(1)), theories);
		Map<String, Object> firstValues = (Map) MetaMap.newHashMap(asList("array.length", "iterations", "isEmpty"), asList(12, 11, true));
		Map<String, Object> secondValues = (Map) MetaMap.newHashMap(asList("array.length", "iterations", "isEmpty"), asList(11, 15, false));
		Specification firstSpecification = new Specification<>(firstValues, 10);
		Specification secondSpecification = new Specification<>(secondValues, 10);
		CodeGenesis genesis = synthesiser.codesSynthesisedFrom(Number.class, (List) asList(firstSpecification, secondSpecification));
		assertTrue(genesis.isSuccessful());
		assertTrue(genesis.returnStatement(), asList("((array.length)-((isEmpty)?((1)+(1)):(1)))", "(((isEmpty)?(iterations):(array.length))) - (1)").contains(genesis.returnStatement()));
	}

	@Test
	public void testSynthesisTernaryOperator() throws Exception {
		Collection<OperatorTheory> theories = (List) asList(new NumberComparisonTheory(), new IfThenElseTheory(), new LinearTheory());
		ConstraintBasedSynthesis synthesiser = new ConstraintBasedSynthesis(SolverFactory.solverLogic(), (Map) MetaMap.newHashMap(asList("0", "1"), asList(0, 1)), theories);
		Map<String, Object> firstValues = (Map) MetaMap.newHashMap(asList("0", "cond", "size"), asList(0, true, 4));
		Map<String, Object> secondValues = (Map) MetaMap.newHashMap(asList("0", "cond", "size"), asList(0, false, 7));
		Specification firstSpecification = new Specification<>(firstValues, false);
		Specification secondSpecification = new Specification<>(secondValues, true);
		CodeGenesis genesis = synthesiser.codesSynthesisedFrom(Boolean.class, (List) asList(firstSpecification, secondSpecification));
		assertTrue(genesis.isSuccessful());
		assertTrue(genesis.returnStatement() + " is not a valid patch", Arrays.asList("0 == ((cond)?(size):(0))", "1 == ((cond)?(size):(1))","((cond)?(1):(size)) == size").contains(genesis.returnStatement()));
	}

	@Test
	public void goldbachConjectureSynthesis() {
		Collection<OperatorTheory> theories = (List) asList(new NumberComparisonTheory(), new LinearTheory());
		ConstraintBasedSynthesis synthesiser = new ConstraintBasedSynthesis(SolverFactory.solverLogic(), (Map) MetaMap.newHashMap(), theories);
		Map<String, Object> first = (Map) MetaMap.newHashMap(asList("p", "q", "n"), asList(3, 3, 6));
		Map<String, Object> second = (Map) MetaMap.newHashMap(asList("p", "q", "n"), asList(13, 5, 18));
		Map<String, Object> third = (Map) MetaMap.newHashMap(asList("p", "q", "n"), asList(2, 5, 6));
		Map<String, Object> fourth = (Map) MetaMap.newHashMap(asList("p", "q", "n"), asList(13, 5, 12));
		Specification firstS = new Specification<Boolean>(first, true); // true is the expected value
		Specification secondS = new Specification<Boolean>(second, true);
		Specification thirdS = new Specification<Boolean>(third, false); // false is the expected value
		Specification fourthS = new Specification<Boolean>(fourth, false);
		CodeGenesis genesis = synthesiser.codesSynthesisedFrom(Boolean.class, (List) asList(firstS, secondS, thirdS, fourthS));
		assertTrue(genesis.isSuccessful());
		assertTrue(asList("(q + p) == n", "p == (n) - (q)", "q + p <= n","(n) - (q) == p").contains(genesis.returnStatement()));
	}

	@Test
	public void synthesisWithCharactersInSpecifications() {
		Collection<OperatorTheory> theories = (List) asList(new NumberComparisonTheory(), new LinearTheory());
		ConstraintBasedSynthesis synthesiser = new ConstraintBasedSynthesis(SolverFactory.solverLogic(), (Map) MetaMap.newHashMap(), theories);
		Map<String, Object> first = (Map) MetaMap.newHashMap(asList("value", "letter"), asList((int) 'a', 'a'));
		Map<String, Object> second = (Map) MetaMap.newHashMap(asList("value", "letter"), asList((int) 'b', 'b'));
		Map<String, Object> third = (Map) MetaMap.newHashMap(asList("value", "letter"), asList((int) 'z', 'c'));
		Map<String, Object> fourth = (Map) MetaMap.newHashMap(asList("value", "letter"), asList((int) 'x', 'd'));
		Specification firstS = new Specification<>(first, true);
		Specification secondS = new Specification<>(second, true);
		Specification thirdS = new Specification<>(third, false);
		Specification fourthS = new Specification<>(fourth, false);
		CodeGenesis genesis = synthesiser.codesSynthesisedFrom(Boolean.class, (List) asList(firstS, secondS, thirdS, fourthS));
		assertTrue(genesis.isSuccessful());
		assertTrue(genesis.returnStatement(), asList("letter == value", "value == letter").contains(genesis.returnStatement()));
	}

	@Test
	public void reduceValuesWithConstants() {
		Map<String, Number> constants = MetaMap.<String, Number>newHashMap(asList("one", "two", "three"), Arrays.<Number>asList(1, 2, 3));
		ConstraintBasedSynthesis synthesiser = new ConstraintBasedSynthesis(SolverFactory.solverLogic(), constants, (List) asList());
		Map<String, Object> inputA = (Map) MetaMap.newHashMap(asList("v", "w", "x", "y", "z"), asList(0, 1, 'a', 2, 3));
		Map<String, Object> inputB = (Map) MetaMap.newHashMap(asList("v", "w", "x", "y", "z"), asList(0, 1, 'a', 0, 3));
		Map<String, Object> inputC = (Map) MetaMap.newHashMap(asList("v", "w", "x", "y", "z"), asList(0, 1, 'a', 2, 3));
		Collection<Map<String, Object>> maps = asList(inputA, inputB, inputC);
		Collection<String> duplicates = synthesiser.reduceInputsWithConstants(maps);
		assertTrue(duplicates.size() == 2);
		assertTrue(duplicates.containsAll(asList("w", "z")));
		assertFalse(inputA.containsKey("w") || inputA.containsKey("z"));
		assertFalse(inputB.containsKey("w") || inputB.containsKey("z"));
		assertFalse(inputC.containsKey("w") || inputC.containsKey("z"));
	}

	@Test
	public void defaultConstants() {
		ConstraintBasedSynthesis synthesiser = new ConstraintBasedSynthesis();
		Map<String, Integer> expectedDefaultContants = MetaMap.newHashMap(asList("-1", "0", "1"), asList(-1, 0, 1));
		assertEquals(expectedDefaultContants, synthesiser.constants());
	}

	@Test
	public void justABooleanConstant() {
		Map<String, Integer> locations = MetaMap.newHashMap();
		locations.put("L@out", 6);
		CodeGenesis synthesis = new CodeGenesis(exampleWithoutOperators(), locations);
		assertTrue(synthesis.isSuccessful());
		assertEquals(8, synthesis.numberOfInputLines());
		assertEquals(8, synthesis.totalNumberOfLines());
		assertEquals("true", synthesis.returnStatement());
	}

	@Test
	public void justAVariable() {
		Map<String, Integer> locations = MetaMap.newHashMap();
		locations.put("L@out", 1);
		CodeGenesis synthesis = new CodeGenesis(exampleWithoutOperators(), locations);
		assertEquals("inhibit", synthesis.returnStatement());
	}

	@Test
	public void justAnIntegerConstant() {
		Map<String, Integer> locations = MetaMap.newHashMap();
		locations.put("L@out", 3);
		CodeGenesis synthesis = new CodeGenesis(exampleWithoutOperators(), locations);
		assertEquals("-1", synthesis.returnStatement());
	}

	@Test
	public void justOneComponent() {
		Map<String, Integer> locations = MetaMap.newHashMap();
		locations.put("L@out", 9);
		locations.put("L@op<0>", 10);
		locations.put("L@op<0><0>", 0);
		locations.put("L@op<0><1>", 1);
		locations.put("L@op<1>", 8);
		locations.put("L@op<1><0>", 4);
		locations.put("L@op<1><1>", 1);
		locations.put("L@op<2>", 11);
		locations.put("L@op<2><0>", 1);
		locations.put("L@op<2><1>", 1);
		locations.put("L@op<3>", 9);
		locations.put("L@op<3><0>", 4);
		locations.put("L@op<3><1>", 0);
		CodeGenesis synthesis = new CodeGenesis(exampleWithOperators(), locations);
		assertEquals("(0) != (up_sep)", synthesis.returnStatement());
	}

	@Test
	public void moreThanOneComponent() {
		Map<String, Integer> locations = MetaMap.newHashMap();
		locations.put("L@out", 11);
		locations.put("L@op<0>", 10);
		locations.put("L@op<0><0>", 8);
		locations.put("L@op<0><1>", 1);
		locations.put("L@op<1>", 8);
		locations.put("L@op<1><0>", 4);
		locations.put("L@op<1><1>", 1);
		locations.put("L@op<2>", 11);
		locations.put("L@op<2><0>", 9);
		locations.put("L@op<2><1>", 10);
		locations.put("L@op<3>", 9);
		locations.put("L@op<3><0>", 4);
		locations.put("L@op<3><1>", 0);
		CodeGenesis synthesis = new CodeGenesis(exampleWithOperators(), locations);
		assertEquals("(0) != (up_sep) == 0 <= inhibit < inhibit", synthesis.returnStatement());
	}

	private LocationVariableContainer exampleWithoutOperators() {
		return exampleWith((List) Arrays.asList());
	}

	private LocationVariableContainer exampleWithOperators() {
		Operator<?> lessThan = BinaryOperator.lessThan();
		Operator<?> lessOrEqThan = BinaryOperator.lessOrEqualThan();
		Operator<?> equals = BinaryOperator.numberEquality();
		Operator<?> distinct = BinaryOperator.numberDistinction();
		Collection<Operator<?>> operators = Arrays.asList(lessThan, lessOrEqThan, equals, distinct);
		return exampleWith(operators);
	}

	private LocationVariableContainer exampleWith(Collection<Operator<?>> operators) {
		Expression<Number> up_sep = new Expression<>(Number.class, "up_sep");
		Expression<Number> inhibit = new Expression<>(Number.class, "inhibit");
		Expression<Number> down_sep = new Expression<>(Number.class, "down_sep");
		Expression<Number> constantM1 = new Expression<>(Number.class, "-1");
		Expression<Number> constant0 = new Expression<>(Number.class, "0");
		Expression<Number> constant1 = new Expression<>(Number.class, "1");
		Expression<Boolean> constantTrue = new Expression<>(Boolean.class, "true");
		Expression<Boolean> constantFalse = new Expression<>(Boolean.class, "false");
		Expression<?> outputExpression = new Expression<>(Boolean.class, "...");

		Collection<Expression<?>> inputs = (List) Arrays.asList(up_sep, inhibit, down_sep, constantM1, constant0, constant1, constantTrue, constantFalse);
		return new LocationVariableContainer(inputs, operators, outputExpression);
	}
}
