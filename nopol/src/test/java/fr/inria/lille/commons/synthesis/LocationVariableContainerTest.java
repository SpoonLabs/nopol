package fr.inria.lille.commons.synthesis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.operator.BinaryOperator;
import fr.inria.lille.commons.synthesis.operator.Operator;
import fr.inria.lille.commons.synthesis.operator.TernaryOperator;
import fr.inria.lille.commons.synthesis.operator.UnaryOperator;
import fr.inria.lille.commons.synthesis.smt.locationVariables.IndexedLocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
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
		
		assertEquals(3, container.inputs().size());
		assertEquals(3, container.copyOfInputs().size());
		equalButDistinct(container.inputs(), container.copyOfInputs());

		assertEquals(4, container.operators().size());
		assertEquals(4, container.copyOfOperators().size());
		equalButDistinct(container.operators(), container.copyOfOperators());
		
		assertEquals(8, container.allParameters().size());
		assertEquals(8, container.copyOfAllParameters().size());
		equalButDistinct(container.allParameters(), container.copyOfAllParameters());
		
		assertEquals(4, container.copyOfInputsAndOutput().size());
		containsCopiesOf((Collection) container.inputs(), (Collection) container.copyOfInputsAndOutput());
		containsCopiesOf((Collection) Arrays.asList(container.outputVariable()), (Collection) container.copyOfInputsAndOutput());
		
		assertEquals(9, container.copyOfAllParametersAndOutput().size());
		containsCopiesOf((Collection) container.allParameters(), (Collection) container.copyOfAllParametersAndOutput());
		containsCopiesOf((Collection) Arrays.asList(container.outputVariable()), (Collection) container.copyOfInputsAndOutput());
		containsCopiesOf((Collection) Arrays.asList(container.outputVariable()), (Collection) container.copyOfInputsAndOutput());
		
		assertEquals(7, container.copyOfOperatorsAndInputs().size());
		containsCopiesOf((Collection) container.inputs(), (Collection) container.copyOfOperatorsAndInputs());
		containsCopiesOf((Collection) container.operators(), (Collection) container.copyOfOperatorsAndInputs());
		
		assertEquals(12, container.copyOfOperatorsAndParameters().size());
		containsCopiesOf((Collection) container.allParameters(), (Collection) container.copyOfOperatorsAndParameters());
		containsCopiesOf((Collection) container.operators(), (Collection) container.copyOfOperatorsAndParameters());
		
		assertEquals(13, container.copyOfOperatorsParametersAndOutput().size());
		containsCopiesOf((Collection) container.allParameters(), (Collection) container.copyOfOperatorsParametersAndOutput());
		containsCopiesOf((Collection) container.operators(), (Collection) container.copyOfOperatorsParametersAndOutput());
		containsCopiesOf((Collection) Arrays.asList(container.outputVariable()), (Collection) container.copyOfOperatorsParametersAndOutput());
		
		assertEquals(16, container.copyOfAllLocationVariables().size());
		containsCopiesOf((Collection) container.inputs(), (Collection) container.copyOfAllLocationVariables());
		containsCopiesOf((Collection) container.allParameters(), (Collection) container.copyOfAllLocationVariables());
		containsCopiesOf((Collection) container.operators(), (Collection) container.copyOfAllLocationVariables());
		containsCopiesOf((Collection) Arrays.asList(container.outputVariable()), (Collection) container.copyOfAllLocationVariables());
		
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
	}
	
	private void equalButDistinct(Object anObject, Object otherObject) {
		assertTrue(anObject.equals(otherObject));
		assertTrue(otherObject.equals(anObject));
		assertFalse(anObject == otherObject);
	}
	
	private void containsCopiesOf(Collection<LocationVariable<?>> aCollection, Collection<LocationVariable<?>> queriedCollection) {
		assertTrue(queriedCollection.containsAll(aCollection));
		assertFalse(aCollection == queriedCollection);
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
