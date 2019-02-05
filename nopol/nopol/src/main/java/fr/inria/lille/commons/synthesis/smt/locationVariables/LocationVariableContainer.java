package fr.inria.lille.commons.synthesis.smt.locationVariables;

import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.operator.Operator;
import xxl.java.container.classic.MetaList;

import java.util.Collection;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class LocationVariableContainer {

    public LocationVariableContainer(Collection<Expression<?>> inputs, Collection<Operator<?>> operators, Expression<?> outputExpression) {
        inputVariables = MetaList.newLinkedList();
        allParameters = MetaList.newLinkedList();
        operatorVariables = MetaList.newLinkedList();
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
        List<ParameterLocationVariable<?>> parameters = MetaList.newArrayList();
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
        return MetaList.flatArrayList(inputs(), asList(outputVariable()));
    }

    public List<LocationVariable<?>> inputsAndOperators() {
        return MetaList.flatArrayList(inputs(), operators());
    }

    public List<LocationVariable<?>> operatorsAndParameters() {
        return MetaList.flatArrayList(operators(), allParameters());
    }

    public List<LocationVariable<?>> operatorsParametersAndOutput() {
        return MetaList.flatArrayList(operatorsAndParameters(), asList(outputVariable()));
    }

    public List<LocationVariable<?>> allVariables() {
        return MetaList.flatArrayList(inputs(), operatorsParametersAndOutput());
    }

    private IndexedLocationVariable<?> outputVariable;
    private List<OperatorLocationVariable<?>> operatorVariables;
    private List<ParameterLocationVariable<?>> allParameters;
    private List<IndexedLocationVariable<?>> inputVariables;
}