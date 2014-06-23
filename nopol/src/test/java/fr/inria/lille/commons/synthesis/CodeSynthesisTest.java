package fr.inria.lille.commons.synthesis;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.synthesis.expression.ValuedExpression;
import fr.inria.lille.commons.synthesis.operator.BinaryOperator;
import fr.inria.lille.commons.synthesis.operator.Operator;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;

public class CodeSynthesisTest {

	@Test
	public void justABooleanConstant() {
		Map<String, Integer> locations = MapLibrary.newHashMap();
		locations.put("L@out", 6);
		CodeSynthesis synthesis = new CodeSynthesis(simpleContainerExample(), locations);
		assertEquals("true", synthesis.returnStatement());
	}
	
	@Test
	public void justAVariable() {
		Map<String, Integer> locations = MapLibrary.newHashMap();
		locations.put("L@out", 1);
		CodeSynthesis synthesis = new CodeSynthesis(simpleContainerExample(), locations);
		assertEquals("inhibit", synthesis.returnStatement());
	}
	
	@Test public void justAnIntegerConstant() {
		Map<String, Integer> locations = MapLibrary.newHashMap();
		locations.put("L@out", 3);
		CodeSynthesis synthesis = new CodeSynthesis(simpleContainerExample(), locations);
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
		CodeSynthesis synthesis = new CodeSynthesis(containerExample(), locations);
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
		CodeSynthesis synthesis = new CodeSynthesis(containerExample(), locations);
		assertEquals("((0)!=(up_sep))==(((0)<=(inhibit))<(inhibit))", synthesis.returnStatement());
	}
	
	private LocationVariableContainer simpleContainerExample() {
		return exampleWith((List) Arrays.asList());
	}
	
	private LocationVariableContainer containerExample() {
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
		
		Collection<ValuedExpression<?>> inputs = (List) Arrays.asList(up_sep, inhibit, down_sep, constantM1, constant0, constant1, constantTrue, constantFalse);
		return new LocationVariableContainer(inputs, operators, outputExpression);
	}
}
