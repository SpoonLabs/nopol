package fr.inria.lille.commons.synthesis;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.smt.locationVariables.OperatorLocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.ParameterLocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.ValuedExpressionLocationVariable;

public class CodeSynthesis {

	public CodeSynthesis(LocationVariableContainer container, Map<String, Integer> smtSolverResult) {
		this.container = container;
		this.smtSolverResult = smtSolverResult;
		writeCode();
	}
	
	public String returnStatement() {
		LocationVariable<?> outputVariable = container().outputVariable();
		CodeLine codeLineFor = codeLineFor(outputVariable);
		return codeLineFor.content();
	}
	
	public List<CodeLine> codeLines() {
		return Arrays.asList(code());
	}

	public int numberOfInputLines() {
		return container().numberOfInputs();
	}
	
	public int totalNumberOfLines() {
		return numberOfInputLines() + container().numberOfOperators();
	}
	
	private void writeCode() {
		code = new CodeLine[totalNumberOfLines()];
		writeInputExpressions();
		writeBody();
	}
	
	private void writeInputExpressions() {
		for (ValuedExpressionLocationVariable<?> input : container().inputs()) {
			int lineIndex = input.index();
			CodeLine newCodeLine = new CodeLine(lineIndex, input.objectTemplate().expression());
			setLine(lineIndex, newCodeLine);
		}
	}
	
	private void writeBody() {
		for (int lineNumber = numberOfInputLines(); lineNumber < totalNumberOfLines(); lineNumber += 1) {
			OperatorLocationVariable<?> operator = orderedOperators().get(lineNumber);
			List<CodeLine> parameterExpressions = codeLinesFor(operator.parameterLocationVariables());
			CodeLine newCodeLine = new OperationCodeLine(lineNumber, operator.objectTemplate(), parameterExpressions);
			setLine(lineNumber, newCodeLine);
		}
	}

	private List<CodeLine> codeLinesFor(Collection<ParameterLocationVariable<?>> parameterLocationVariables) {
		List<CodeLine> codeLines = ListLibrary.newArrayList();
		for (ParameterLocationVariable<?> parameter : parameterLocationVariables) {
			codeLines.add(codeLineFor(parameter));
		}
		return codeLines;
	}
	
	private CodeLine codeLineFor(LocationVariable<?> locationVariable) {
		return code()[lineFor(locationVariable)];
	}
	
	private void setLine(int lineNumber, CodeLine codeLine) {
		code()[lineNumber] = codeLine;
	}
	
	private int lineFor(LocationVariable<?> locationVariable) {
		return smtSolverResult().get(locationVariable.expression());
	}
	
	private CodeLine[] code() {
		return code;
	}
	
	private LocationVariableContainer container() {
		return container;
	}
	
	private Map<String, Integer> smtSolverResult() {
		return smtSolverResult;
	}
	
	private Map<Integer, OperatorLocationVariable<?>> orderedOperators() {
		if (orderedOperators == null) {
			Map<Integer, OperatorLocationVariable<?>> operatorMap = MapLibrary.newHashMap();
			for (OperatorLocationVariable<?> operator : container().operators()) {
				operatorMap.put(smtSolverResult().get(operator.expression()), operator);
			}
			orderedOperators = operatorMap;
		}
		return orderedOperators;
	}
	
	private CodeLine[] code;
	private LocationVariableContainer container;
	private Map<String, Integer> smtSolverResult;
	private Map<Integer, OperatorLocationVariable<?>> orderedOperators;
}
