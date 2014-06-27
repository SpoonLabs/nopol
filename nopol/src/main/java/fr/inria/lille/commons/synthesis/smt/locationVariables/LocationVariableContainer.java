package fr.inria.lille.commons.synthesis.smt.locationVariables;

import static java.lang.String.format;

import java.util.Collection;
import java.util.List;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.operator.Operator;

public class LocationVariableContainer {

	public LocationVariableContainer(Collection<Expression<?>> inputs, Collection<Operator<?>> operators, Expression<?> outputExpression) {
		addInputs(inputs);
		addOperators(operators);
		allParameters().addAll(parameterLocationVariablesFrom(operators()));
		int lastLine = numberOfInputs() + numberOfOperators();
		outputVariable = new IndexedLocationVariable<>(outputExpression, "out", lastLine);
	}
	
	private void addInputs(Collection<Expression<?>> inputs) {
		for (Expression<?> input : inputs) {
			addInput(input);
		}
	}
	
	private void addInput(Expression<?> input) {
		int inputIndex = numberOfInputs();
		inputs().add(new IndexedLocationVariable<>(input, format("in<%d>", inputIndex), inputIndex));
	}
		
	private void addOperators(Collection<Operator<?>> operators) {
		for (Operator<?> operator : operators) {
			addOperator(operator);
		}
	}
	
	private void addOperator(Operator<?> operator) {
		operators().add(new OperatorLocationVariable<>(operator, format("op<%d>", numberOfOperators())));
	}
	
	private List<ParameterLocationVariable<?>> parameterLocationVariablesFrom(Collection<OperatorLocationVariable<?>> operators) {
		List<ParameterLocationVariable<?>> parameters = ListLibrary.newArrayList();
		for (OperatorLocationVariable<?> operator : operators) {
			parameters.addAll(operator.parameterLocationVariables());
		}
		return parameters;
	}

	public int numberOfInputs() {
		return inputs().size();
	}
	
	public int numberOfOperators() {
		return operators().size();
	}
	
	public List<IndexedLocationVariable<?>> copyOfInputs() {
		List<IndexedLocationVariable<?>> locationVariables = ListLibrary.newArrayList();
		locationVariables.addAll(inputs());
		return locationVariables;
	}
	
	public List<LocationVariable<?>> copyOfInputsAndOutput() {
		List<LocationVariable<?>> locationVariables = ListLibrary.newLinkedList();
		locationVariables.addAll(inputs());
		locationVariables.add(outputVariable());
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
	
	public List<LocationVariable<?>> copyOfOperatorsInputsAndOutput() {
		List<LocationVariable<?>> locationVariables = ListLibrary.newLinkedList();
		locationVariables.addAll(operators());
		locationVariables.addAll(inputs());
		locationVariables.add(outputVariable());
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
		locationVariables.add(outputVariable());
		return locationVariables;
	}
	
	public List<LocationVariable<?>> copyOfAllLocationVariables() {
		List<LocationVariable<?>> locationVariables = copyOfOperatorsParametersAndOutput();
		locationVariables.addAll(0, inputs());
		return locationVariables;
	}
	
	public IndexedLocationVariable<?> outputVariable() {
		return outputVariable;
	}
	
	public List<IndexedLocationVariable<?>> inputs() {
		if (inputVariables == null) {
			inputVariables = ListLibrary.newLinkedList();
		}
		return inputVariables;
	}

	public List<OperatorLocationVariable<?>> operators() {
		if (operatorVariables == null) {
			operatorVariables = ListLibrary.newLinkedList();
		}
		return operatorVariables;
	}
	
	public List<ParameterLocationVariable<?>> allParameters() {
		if (allParameters == null) {
			allParameters = ListLibrary.newLinkedList();
		}
		return allParameters;
	}
	
	private IndexedLocationVariable<?> outputVariable;
	private List<OperatorLocationVariable<?>> operatorVariables;
	private List<ParameterLocationVariable<?>> allParameters;
	private List<IndexedLocationVariable<?>> inputVariables;
}