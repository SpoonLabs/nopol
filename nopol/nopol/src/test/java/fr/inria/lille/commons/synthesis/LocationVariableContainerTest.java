package fr.inria.lille.commons.synthesis;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import xxl.java.container.classic.MetaList;
import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.operator.BinaryOperator;
import fr.inria.lille.commons.synthesis.operator.Operator;
import fr.inria.lille.commons.synthesis.operator.TernaryOperator;
import fr.inria.lille.commons.synthesis.operator.UnaryOperator;
import fr.inria.lille.commons.synthesis.smt.locationVariables.IndexedLocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.smt.locationVariables.OperatorLocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.ParameterLocationVariable;

public class LocationVariableContainerTest {

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void checkStateOfLocationVariableContainer() {
		Expression<Boolean> firstExpression = new Expression<>(Boolean.class, "i != null");
		Expression<Integer> secondExpression = new Expression<>(Integer.class, "i.size()");
		Expression<Integer> thirdExpression = new Expression<>(Integer.class, "i.get(\"n\")");
		Collection<Expression<?>> inputs = (List) Arrays.asList(firstExpression, secondExpression, thirdExpression);
		Collection<Operator<?>> operators = (List) Arrays.asList(BinaryOperator.addition(), BinaryOperator.or(), TernaryOperator.ifThenElse(), UnaryOperator.not());
		Expression<?> outputExpression = new Expression<>(Boolean.class, "...");
		LocationVariableContainer container = new LocationVariableContainer(inputs, operators, outputExpression);
	
		checkInput(container, Boolean.class, 0);
		checkInput(container, Integer.class, 1);
		checkInput(container, Integer.class, 2);

		checkOperator(container, Number.class, 0, "+", "+", 2);
		checkOperator(container, Boolean.class, 1, "||", "or", 2);
		checkOperator(container, Number.class, 2, "?:", "ite", 3);
		checkOperator(container, Boolean.class, 3, "!", "not", 1);
		
		checkParameter(container, Number.class, 0, 0, 0);
		checkParameter(container, Number.class, 1, 0, 1);
		checkParameter(container, Boolean.class, 2, 1, 0);
		checkParameter(container, Boolean.class, 3, 1, 1);
		checkParameter(container, Boolean.class, 4, 2, 0);
		checkParameter(container, Number.class, 5, 2, 1);
		checkParameter(container, Number.class, 6, 2, 2);
		checkParameter(container, Boolean.class, 7, 3, 0);
		
		assertTrue(MetaList.isPartitionOf(container.inputsAndOutput(), container.inputs(), asList(container.outputVariable())));
		assertTrue(MetaList.isPartitionOf(container.inputsAndOperators(), container.inputs(), container.operators()));
		assertTrue(MetaList.isPartitionOf(container.operatorsAndParameters(), container.operators(), container.allParameters()));
		assertTrue(MetaList.isPartitionOf(container.operatorsParametersAndOutput(), container.operatorsAndParameters(), asList(container.outputVariable())));
		assertTrue(MetaList.isPartitionOf(container.allVariables(), container.inputs(), container.operatorsParametersAndOutput()));
		
		assertTrue(3 == container.numberOfInputs());
		assertTrue(4 == container.numberOfOperators());
		assertTrue(7 == container.outputVariable().index());
	}

	private void checkInput(LocationVariableContainer container, Class<?> expectedClass, int inputIndex) {
		IndexedLocationVariable<?> input = container.inputs().get(inputIndex);
		assertEquals(String.format("%d", inputIndex), input.encodedLineNumber().toString());
		assertEquals(expectedClass, input.type());
		assertEquals(String.format("in<%d>", inputIndex), input.subexpression());
		assertEquals("L@" + input.subexpression(), input.expression());
	}
	
	private void checkOperator(LocationVariableContainer container, Class<?> expectedClass, int outputIndex, String symbol, String smtSymbol, int numberOfParameters) {
		OperatorLocationVariable<?> operator = container.operators().get(outputIndex);
		assertEquals(operator.expression(), operator.encodedLineNumber().toString());
		assertEquals(expectedClass, operator.type());
		assertEquals(String.format("op<%d>", outputIndex), operator.subexpression());
		assertEquals("L@" + operator.subexpression(), operator.expression());
		assertEquals(numberOfParameters, operator.objectTemplate().arity());
		assertEquals(symbol, operator.objectTemplate().symbol());
		assertEquals(smtSymbol, operator.objectTemplate().smtlibIdentifier().value());
	}
	
	private void checkParameter(LocationVariableContainer container, Class<?> expectedClass, int parameterIndex, int operatorIndex, int argumentIndex) {
		ParameterLocationVariable<?> parameter = container.allParameters().get(parameterIndex);
		assertEquals(parameter.expression(), parameter.encodedLineNumber().toString());
		assertEquals(expectedClass, parameter.type());
		assertEquals(String.format("op<%d><%d>", operatorIndex, argumentIndex), parameter.subexpression());
		assertEquals("L@" + parameter.subexpression(), parameter.expression());
		assertTrue((Object) container.operators().get(operatorIndex) == (Object) parameter.operatorLocationVariable());
	}
}
