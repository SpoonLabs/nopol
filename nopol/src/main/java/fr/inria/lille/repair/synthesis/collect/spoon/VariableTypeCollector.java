package fr.inria.lille.repair.synthesis.collect.spoon;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by spirals on 21/07/15.
 */
public class VariableTypeCollector extends AbstractProcessor<CtVariable> {
    private Map<String, String> variableType;
    private String buggyMethod;
    private int line;

    public VariableTypeCollector(String buggyMethod, int line) {
        this.buggyMethod = buggyMethod;
        this.line = line;
        this.variableType = new HashMap<>();
    }

    @Override
    public boolean isToBeProcessed(CtVariable candidate) {
        CtExecutable parent = candidate.getParent(CtMethod.class);
        if (parent == null) {
            return true;
        }
        return parent.getSimpleName().equals(buggyMethod);
    }

    public void process(CtVariable ctVariable) {
        if (ctVariable instanceof CtField) {
            this.variableType.put("this." + ctVariable.getSimpleName(), ctVariable.getType().getQualifiedName());
        } else if (ctVariable instanceof CtParameter) {
            this.variableType.put(ctVariable.getSimpleName(), ctVariable.getType().getQualifiedName());
        } else if (ctVariable.getPosition().getLine() <= this.line) {
            this.variableType.put(ctVariable.getSimpleName(), ctVariable.getType().getQualifiedName());
        }
    }

    public Map<String, String> getVariableType() {
        return variableType;
    }
}
