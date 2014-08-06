package fr.inria.lille.commons.synthesis.smt.locationVariables;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.operator.Operator;

public class LocationVariableContainer {

	public LocationVariableContainer(Collection<Expression<?>> inputs, Collection<Operator<?>> operators, Expression<?> outputExpression) {
		inputVariables = ListLibrary.newLinkedList();
		allParameters = ListLibrary.newLinkedList();
		operatorVariables = ListLibrary.newLinkedList();
		addInputs(inputs);
		addOperators(operators);
		allParameters().addAll(parameterLocationVariablesFrom(operators()));
		int lastLine = numberOfInputs() + numberOfOperators();
		outputVariable = new IndexedLocationVariable(outputExpression, "out", lastLine);
	}
	
	private void addInputs(Collection<Expression<?>> inputs) {
		for (Expression<?> input : inputs) {
			addInput(input);
		}
	}
	
	private void addInput(Expression<?> input) {
		int inputIndex = numberOfInputs();
		inputs().add(new IndexedLocationVariable(input, format("in<%d>", inputIndex), inputIndex));
	}
		
	private void addOperators(Collection<Operator<?>> operators) {
		for (Operator<?> operator : operators) {
			addOperator(operator);
		}
	}
	
	private void addOperator(Operator<?> operator) {
		operators().add(new OperatorLocationVariable(operator, format("op<%d>", numberOfOperators())));
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
	
	public IndexedLocationVariable<?> outputVariable() {
		return outputVariable;
	}
	
	public List<IndexedLocationVariable<?>> inputs() {
		return inputVariables;
	}

	public List<OperatorLocationVariable<?>> operators() {
		return operatorVariables;
	}
	
	public List<ParameterLocationVariable<?>> allParameters() {
		return allParameters;
	}
	
	public List<IndexedLocationVariable<?>> inputsAndOutput() {
		return ListLibrary.flatArrayList(inputs(), asList(outputVariable())); 
	}
	
	public List<LocationVariable<?>> inputsAndOperators() {
		return ListLibrary.flatArrayList(inputs(), operators()); 
	}
	
	public List<LocationVariable<?>> operatorsAndParameters() {
		return ListLibrary.flatArrayList(operators(), allParameters()); 
	}
	
	public List<LocationVariable<?>> operatorsParametersAndOutput() {
		return ListLibrary.flatArrayList(operatorsAndParameters(), asList(outputVariable())); 
	}
	
	public List<LocationVariable<?>> allVariables() {
		return ListLibrary.flatArrayList(inputs(), operatorsParametersAndOutput()); 
	}
	
	private IndexedLocationVariable<?> outputVariable;
	private List<OperatorLocationVariable<?>> operatorVariables;
	private List<ParameterLocationVariable<?>> allParameters;
	private List<IndexedLocationVariable<?>> inputVariables;
}