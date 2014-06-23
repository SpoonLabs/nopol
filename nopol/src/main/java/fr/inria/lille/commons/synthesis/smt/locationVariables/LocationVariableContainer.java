package fr.inria.lille.commons.synthesis.smt.locationVariables;

import java.util.Collection;
import java.util.List;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.synthesis.expression.ValuedExpression;
import fr.inria.lille.commons.synthesis.operator.Operator;

public class LocationVariableContainer {

	public LocationVariableContainer(Collection<ValuedExpression<?>> inputs, Collection<Operator<?>> operators, ValuedExpression<?> outputExpression) {
		inputVariables = locationVariablesFor(inputs);
		operatorVariables = operatorLocationVariablesFor(operators);
		allParameters = parameterLocationVariablesFrom(operators());
		int lastLine = numberOfInputs() + numberOfOperators();
		outputVariable = new ValuedExpressionLocationVariable<>(outputExpression, "out", lastLine);
	}
	
	private List<ParameterLocationVariable<?>> parameterLocationVariablesFrom(Collection<OperatorLocationVariable<?>> operators) {
		List<ParameterLocationVariable<?>> parameters = ListLibrary.newArrayList();
		for (OperatorLocationVariable<?> operator : operators) {
			parameters.addAll(operator.parameterLocationVariables());
		}
		return parameters;
	}

	private List<ValuedExpressionLocationVariable<?>> locationVariablesFor(Collection<ValuedExpression<?>> inputs) {
		List<ValuedExpressionLocationVariable<?>> variables = ListLibrary.newArrayList();
		int inputIndex = 0;
		for (ValuedExpression<?> expression : inputs) {
			variables.add(new ValuedExpressionLocationVariable<>(expression, "in<" + inputIndex + ">", inputIndex));
			inputIndex += 1;
		}
		return variables;
	}
	
	private List<OperatorLocationVariable<?>> operatorLocationVariablesFor(Collection<Operator<?>> operators) {
		List<OperatorLocationVariable<?>> variables = ListLibrary.newArrayList();
		int operatorIndex = 0;
		for (Operator<?> operator : operators) {
			variables.add(new OperatorLocationVariable<>(operator, "op<" + operatorIndex + ">"));
			operatorIndex += 1;
		}
		return variables;
	}
	
	public int numberOfInputs() {
		return inputs().size();
	}
	
	public int numberOfOperators() {
		return operators().size();
	}
	
	public List<ValuedExpressionLocationVariable<?>> copyOfInputs() {
		List<ValuedExpressionLocationVariable<?>> locationVariables = ListLibrary.newArrayList();
		locationVariables.addAll(inputs());
		return locationVariables;
	}
	
	public List<LocationVariable<?>> copyOfInputsAndOutput() {
		List<LocationVariable<?>> locationVariables = ListLibrary.newLinkedList();
		locationVariables.add(outputVariable());
		locationVariables.addAll(inputs());
		return locationVariables;
	}
	
	public List<OperatorLocationVariable<?>> copyOfOperators() {
		List<OperatorLocationVariable<?>> locationVariables = ListLibrary.newArrayList();
		locationVariables.addAll(operators());
		return locationVariables;
	}
	
	public List<LocationVariable<?>> copyOfOperatorsAndInputs() {
		List<LocationVariable<?>> locationVariables = ListLibrary.newLinkedList();
		locationVariables.addAll(operators());
		locationVariables.addAll(inputs());
		return locationVariables;
	}
	
	public List<LocationVariable<?>> copyOfOperatorsAndParameters() {
		List<LocationVariable<?>> locationVariables = ListLibrary.newLinkedList();
		locationVariables.addAll(operators());
		locationVariables.addAll(allParameters());
		return locationVariables;
	}
	
	public List<LocationVariable<?>> copyOfOperatorsParametersAndOutput() {
		List<LocationVariable<?>> locationVariables = copyOfOperatorsAndParameters();
		locationVariables.add(outputVariable());
		return locationVariables;
	}
	
	public List<ParameterLocationVariable<?>> copyOfAllParameters() {
		List<ParameterLocationVariable<?>> locationVariables = ListLibrary.newArrayList();
		locationVariables.addAll(allParameters());
		return locationVariables;
	}
	
	public List<LocationVariable<?>> copyOfAllParametersAndOutput() {
		List<LocationVariable<?>> locationVariables = ListLibrary.newLinkedList();
		locationVariables.addAll(allParameters());
		locationVariables.add(outputVariable);
		return locationVariables;
	}
	
	public List<LocationVariable<?>> copyOfAllLocationVariables() {
		List<LocationVariable<?>> locationVariables = copyOfOperatorsParametersAndOutput();
		locationVariables.addAll(inputs());
		return locationVariables;
	}
	
	public ValuedExpressionLocationVariable<?> outputVariable() {
		return outputVariable;
	}
	
	public List<ValuedExpressionLocationVariable<?>> inputs() {
		return inputVariables;
	}

	public List<OperatorLocationVariable<?>> operators() {
		return operatorVariables;
	}
	
	public List<ParameterLocationVariable<?>> allParameters() {
		return allParameters;
	}
	
	private ValuedExpressionLocationVariable<?> outputVariable;
	private List<OperatorLocationVariable<?>> operatorVariables;
	private List<ParameterLocationVariable<?>> allParameters;
	private List<ValuedExpressionLocationVariable<?>> inputVariables;
}