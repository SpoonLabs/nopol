package fr.inria.lille.commons.synthesis;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.smtlib.ISort;

import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.collections.Multimap;
import fr.inria.lille.commons.collections.SetLibrary;
import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.expression.ObjectTemplate;
import fr.inria.lille.commons.synthesis.expression.ValuedExpression;
import fr.inria.lille.commons.synthesis.operator.BinaryOperator;
import fr.inria.lille.commons.synthesis.operator.Operator;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.theory.EmptyTheory;
import fr.inria.lille.commons.synthesis.theory.IfThenElseTheory;
import fr.inria.lille.commons.synthesis.theory.LinearTheory;
import fr.inria.lille.commons.synthesis.theory.OperatorTheory;
import fr.inria.lille.commons.trace.Specification;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CodeSynthesisTest {

	@Test
	public void useRealInsteadOfIntegerToSolveScript() {
		ConstraintBasedSynthesis synthesiser = new ConstraintBasedSynthesis();
		Expression<?> outputExpression = synthesiser.outputExpressionFor(Integer.class);
		assertEquals(SMTLib.numberSort(), outputExpression.smtSort());
		
		Map<String, Integer> inputs = MapLibrary.newHashMap(asList("a", "b"), asList(1, 2));
		Collection<Expression<?>> inputExpressions = synthesiser.inputExpressions((Collection) asList(inputs), outputExpression);
		assertEquals(2, inputExpressions.size());
		Multimap<ISort,ObjectTemplate<?>> bySort = ObjectTemplate.bySort((List) inputExpressions);
		assertEquals(SetLibrary.newHashSet(SMTLib.numberSort()), bySort.keySet());
	}
	
	@Test
	public void scriptResolutionWithoutComponents() {
		Collection<OperatorTheory> theories = (List) asList(new EmptyTheory());
		ConstraintBasedSynthesis synthesiser = new ConstraintBasedSynthesis((Map) MapLibrary.newHashMap(), theories);
		Map<String, Object> firstValues = (Map) MapLibrary.newHashMap(asList("array.length", "iterations"), asList(10, 15));
		Map<String, Object> secondValues = (Map) MapLibrary.newHashMap(asList("array.length", "iterations"), asList(10, 7));
		Specification firstSpecification = new Specification<>(firstValues, 10);
		Specification secondSpecification = new Specification<>(secondValues, 10);
		CodeGenesis genesis = synthesiser.codesSynthesisedFrom(Number.class, (List) asList(firstSpecification, secondSpecification));
		assertTrue(genesis.isSuccessful());
		assertEquals("array.length", genesis.returnStatement());
	}
	
	@Test
	public void scriptResolutionWithOneTheory() {
		Collection<OperatorTheory> theories = (List) asList(new LinearTheory());
		ConstraintBasedSynthesis synthesiser = new ConstraintBasedSynthesis((Map) MapLibrary.newHashMap(), theories);
		Map<String, Object> firstValues = (Map) MapLibrary.newHashMap(asList("array.length", "iterations"), asList(10, 10));
		Map<String, Object> secondValues = (Map) MapLibrary.newHashMap(asList("array.length", "iterations"), asList(15, 5));
		Specification firstSpecification = new Specification<>(firstValues, 20);
		Specification secondSpecification = new Specification<>(secondValues, 20);
		CodeGenesis genesis = synthesiser.codesSynthesisedFrom(Number.class, (List) asList(firstSpecification, secondSpecification));
		assertTrue(genesis.isSuccessful());
		assertEquals("(iterations)+(array.length)", genesis.returnStatement());
	}
	
	@Test
	public void scriptResolutionWithTwoTheories() {
		Collection<OperatorTheory> theories = (List) asList(new LinearTheory(), new IfThenElseTheory());
		ConstraintBasedSynthesis synthesiser = new ConstraintBasedSynthesis((Map) MapLibrary.newHashMap(), theories);
		Map<String, Object> firstValues = (Map) MapLibrary.newHashMap(asList("array.length", "iterations", "isEmpty"), asList(11, 10, true));
		Map<String, Object> secondValues = (Map) MapLibrary.newHashMap(asList("array.length", "iterations", "isEmpty"), asList(10, 11, false));
		Specification firstSpecification = new Specification<>(firstValues, 10);
		Specification secondSpecification = new Specification<>(secondValues, 10);
		CodeGenesis genesis = synthesiser.codesSynthesisedFrom(Number.class, (List) asList(firstSpecification, secondSpecification));
		assertTrue(genesis.isSuccessful());
		assertEquals("(isEmpty)?(iterations):(array.length)", genesis.returnStatement());
	}
	
	@Test
	public void justABooleanConstant() {
		Map<String, Integer> locations = MapLibrary.newHashMap();
		locations.put("L@out", 6);
		CodeGenesis synthesis = new CodeGenesis(exampleWithoutOperators(), locations);
		assertTrue(synthesis.isSuccessful());
		assertEquals(8, synthesis.numberOfInputLines());
		assertEquals(8, synthesis.totalNumberOfLines());
		assertEquals("true", synthesis.returnStatement());
	}
	
	@Test
	public void justAVariable() {
		Map<String, Integer> locations = MapLibrary.newHashMap();
		locations.put("L@out", 1);
		CodeGenesis synthesis = new CodeGenesis(exampleWithoutOperators(), locations);
		assertEquals("inhibit", synthesis.returnStatement());
	}
	
	@Test public void justAnIntegerConstant() {
		Map<String, Integer> locations = MapLibrary.newHashMap();
		locations.put("L@out", 3);
		CodeGenesis synthesis = new CodeGenesis(exampleWithoutOperators(), locations);
		assertEquals("-1", synthesis.returnStatement());
	}
	
	@Test
	public void justOneComponent() {
		Map<String, Integer> locations = MapLibrary.newHashMap();
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
		assertEquals("(0)!=(up_sep)", synthesis.returnStatement());
	}
	
	@Test
	public void moreThanOneComponent() {
		Map<String, Integer> locations = MapLibrary.newHashMap();
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
		assertEquals("((0)!=(up_sep))==(((0)<=(inhibit))<(inhibit))", synthesis.returnStatement());
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
		ValuedExpression<Number> up_sep = new ValuedExpression<>(Number.class, "up_sep", 11);
		ValuedExpression<Number> inhibit = new ValuedExpression<>(Number.class, "inhibit", 1);
		ValuedExpression<Number> down_sep = new ValuedExpression<>(Number.class, "down_sep", 110);
		ValuedExpression<Number> constantM1 = new ValuedExpression<>(Number.class, "-1", -1);
		ValuedExpression<Number> constant0 = new ValuedExpression<>(Number.class, "0", 0);
		ValuedExpression<Number> constant1 = new ValuedExpression<>(Number.class, "1", 1);
		ValuedExpression<Boolean> constantTrue = new ValuedExpression<>(Boolean.class, "true", true);
		ValuedExpression<Boolean> constantFalse = new ValuedExpression<>(Boolean.class, "false", false);
		ValuedExpression<?> outputExpression = new ValuedExpression<>(Boolean.class, "...", true);
		
		Collection<Expression<?>> inputs = (List) Arrays.asList(up_sep, inhibit, down_sep, constantM1, constant0, constant1, constantTrue, constantFalse);
		return new LocationVariableContainer(inputs, operators, outputExpression);
	}
}
