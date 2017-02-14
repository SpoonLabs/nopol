package fr.inria.lille.repair.synthesis.collect.spoon;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtVariableReference;

import java.util.*;

/**
 * Created by spirals on 27/03/15.
 */
public class StatCollector extends AbstractProcessor<CtElement> {
    private final String buggyMethod;
    private int totalBinaryOperatorKind = 0;
    private int totalVariable = 0;
    private int totalMethodCall = 0;
    private Map<BinaryOperatorKind, Integer> statOperator = new HashMap<>();
    private Map<CtVariableReference, Integer> statVariable = new HashMap<>();
    private Map<CtExecutableReference, Integer> statMethod = new HashMap<>();


    public StatCollector(String buggyMethod) {
        this.buggyMethod = buggyMethod;
    }

    @Override
    public boolean isToBeProcessed(CtElement candidate) {
        return isMethod(candidate);
    }

    @Override
    public void process(CtElement ctElement) {
        if (ctElement instanceof CtBinaryOperator) {
            if (!isMethod(ctElement)) {
                return;
            }
            BinaryOperatorKind kind = ((CtBinaryOperator) ctElement).getKind();
            if (!statOperator.containsKey(kind)) {
                statOperator.put(kind, 1);
            } else {
                statOperator.put(kind, statOperator.get(kind) + 1);
            }
            totalBinaryOperatorKind++;
        } else if (ctElement instanceof CtVariableAccess) {
            if (!isMethod(ctElement)) {
                return;
            }
            CtVariableReference variable = ((CtVariableAccess) ctElement).getVariable();
            if (!statVariable.containsKey(variable)) {
                statVariable.put(variable, 1);
            } else {
                statVariable.put(variable, statVariable.get(variable) + 1);
            }
            totalVariable++;
        }
        if (ctElement instanceof CtFieldAccess) {
            if (!isMethod(ctElement)) {
                return;
            }
            CtVariableReference variable = ((CtFieldAccess) ctElement).getVariable();
            if (!statVariable.containsKey(variable)) {
                statVariable.put(variable, 1);
            } else {
                statVariable.put(variable, statVariable.get(variable) + 1);
            }
            totalVariable++;
        } else if (ctElement instanceof CtInvocation) {
            if (!isMethod(ctElement)) {
                return;
            }
            CtExecutableReference executable = ((CtInvocation) ctElement).getExecutable();
            if (executable.isConstructor()) {
                return;
            }
            if (!statMethod.containsKey(executable)) {
                statMethod.put(executable, 1);
            } else {
                statMethod.put(executable, statMethod.get(executable) + 1);
            }
            totalMethodCall++;
        }
    }

    private boolean isMethod(CtElement ctElement) {
        CtExecutable parent = ctElement.getParent(CtExecutable.class);
        if (parent == null) {
            return false;
        }
        return parent.getSimpleName().equals(buggyMethod);
    }

    public Map<BinaryOperatorKind, Integer> getStatOperator() {
        return statOperator;
    }

    public Map<CtVariableReference, Integer> getStatVariable() {
        return statVariable;
    }

    public Map<CtExecutableReference, Integer> getStatMethod() {
        return statMethod;
    }

    @Override
    public String toString() {
        String content = "";
        content += "========================\n";
        content += "         Stat\n";
        content += "========================\n";
        content += "Operators: \n";
        List<BinaryOperatorKind> ops = new ArrayList<>(this.statOperator.keySet());
        Collections.sort(ops, new Comparator<BinaryOperatorKind>() {
            @Override
            public int compare(BinaryOperatorKind binaryOperatorKind, BinaryOperatorKind t1) {
                return statOperator.get(t1) - statOperator.get(binaryOperatorKind);
            }
        });
        for (int i = 0; i < ops.size(); i++) {
            BinaryOperatorKind binaryOperatorKind = ops.get(i);
            content += binaryOperatorKind + " " + statOperator.get(binaryOperatorKind) + "\n";
        }
        content += "Methods: \n";
        List<CtExecutableReference> methods = new ArrayList<>(this.statMethod.keySet());
        Collections.sort(methods, new Comparator<CtExecutableReference>() {
            @Override
            public int compare(CtExecutableReference t1, CtExecutableReference t2) {
                return statMethod.get(t2) - statMethod.get(t1);
            }
        });
        for (int i = 0; i < methods.size(); i++) {
            CtExecutableReference elem = methods.get(i);
            content += elem + " " + statMethod.get(elem) + "\n";
        }

        content += "Variable: \n";
        List<CtVariableReference> variables = new ArrayList<>(this.statVariable.keySet());
        Collections.sort(variables, new Comparator<CtVariableReference>() {
            @Override
            public int compare(CtVariableReference t1, CtVariableReference t2) {
                return statVariable.get(t2) - statVariable.get(t1);
            }
        });
        for (int i = 0; i < variables.size(); i++) {
            CtVariableReference elem = variables.get(i);
            content += elem + " " + statVariable.get(elem) + "\n";
        }
        return content;
    }
}
