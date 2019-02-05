package fr.inria.lille.commons.synthesis;

import fr.inria.lille.commons.synthesis.smt.locationVariables.IndexedLocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;
import fr.inria.lille.commons.synthesis.smt.locationVariables.OperatorLocationVariable;
import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class CodeGenesis {

    public CodeGenesis(LocationVariableContainer container, Map<String, Integer> smtSolverResult) {
        this.container = container;
        this.smtSolverResult = smtSolverResult;
        writeCode();
    }

    public boolean isSuccessful() {
        return true;
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

    protected void writeCode() {
        code = new CodeLine[totalNumberOfLines()];
        writeInputExpressions();
        writeBody();
    }

    private void writeInputExpressions() {
        for (IndexedLocationVariable<?> input : container().inputs()) {
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

    private List<CodeLine> codeLinesFor(Collection<? extends LocationVariable<?>> locationVariables) {
        List<CodeLine> codeLines = MetaList.newArrayList();
        for (LocationVariable<?> parameter : locationVariables) {
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
            Map<Integer, OperatorLocationVariable<?>> operatorMap = MetaMap.newHashMap();
            for (OperatorLocationVariable<?> operator : container().operators()) {
                operatorMap.put(smtSolverResult().get(operator.expression()), operator);
            }
            orderedOperators = operatorMap;
        }
        return orderedOperators;
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        for (CodeLine line : code()) {
            toString.append(format("%d: %s\n", line.lineNumber(), line.content()));
        }
        return toString.toString();
    }

    private CodeLine[] code;
    private LocationVariableContainer container;
    private Map<String, Integer> smtSolverResult;
    private Map<Integer, OperatorLocationVariable<?>> orderedOperators;
}
