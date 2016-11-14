package fr.inria.lille.repair.nopol.spoon.symbolic;

import fr.inria.lille.commons.spoon.util.SpoonStatementLibrary;
import fr.inria.lille.repair.common.synth.StatementType;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import gov.nasa.jpf.symbc.Debug;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtTypedElement;

import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newLocalVariableDeclaration;

public class LiteralReplacer extends NopolProcessor {

    public LiteralReplacer(Class<?> cl, CtStatement statement, StatementType statementType) {
        super(statement, statementType);
        if (statement instanceof CtAssignment<?, ?>) {
            super.setDefaultValue(((CtAssignment<?, ?>) statement).getAssignment().toString());
        } else if (statement instanceof CtLocalVariable<?>) {
            super.setDefaultValue(((CtLocalVariable<?>) statement).getDefaultExpression().toString());
        }
        super.setType(cl);
    }

    @Override
    public void process(CtStatement ctStatement) {
        if (!(ctStatement instanceof CtLocalVariable<?>) && !(ctStatement instanceof CtAssignment<?, ?>))
            return;

        Class<?> localVariableClass = ((CtTypedElement<?>) ctStatement).getType().getActualClass();
        if (localVariableClass.equals(Integer.class) || localVariableClass.equals(int.class)) {
            if (ctStatement instanceof CtAssignment<?, ?>) {
                replaceInteger(((CtAssignment<?, ?>) ctStatement).getAssignment());
            } else {
                replaceInteger(((CtLocalVariable<?>) ctStatement).getDefaultExpression());
            }
        } else if (localVariableClass.equals(Double.class) || localVariableClass.equals(double.class)) {
            if (ctStatement instanceof CtAssignment<?, ?>) {
                replaceDouble(((CtAssignment<?, ?>) ctStatement).getAssignment());
            } else {
                replaceDouble(((CtLocalVariable<?>) ctStatement).getDefaultExpression());
            }
        } else if (localVariableClass.equals(Boolean.class) || localVariableClass.equals(boolean.class)) {
            if (ctStatement instanceof CtAssignment<?, ?>) {
                replaceBoolean(((CtAssignment<?, ?>) ctStatement).getAssignment());
            } else {
                replaceBoolean(((CtLocalVariable<?>) ctStatement).getDefaultExpression());
            }
        }
    }

    private void replaceDouble(CtExpression ctElement) {
        if (getValue() == null) {
            CtLocalVariable<Double> evaluation = newLocalVariableDeclaration(
                    ctElement.getFactory(), double.class, "guess_fix",
                    Debug.class.getCanonicalName() + ".makeSymbolicReal(\"guess_fix\")");

            CtStatement firstStatement = getFirstStatement(ctElement);
            if (firstStatement == null) {
                return;
            }
            SpoonStatementLibrary.insertBeforeUnderSameParent(evaluation, firstStatement);
            // SpoonStatementLibrary.insertAfterUnderSameParent(getFactory().Code().createCodeSnippetStatement("System.out.println(\"guess_fix: \" + guess_fix)"),
            // getFirstStatement(ctElement));
            ctElement.replace(getFactory().Code().createCodeSnippetExpression("guess_fix"));
        } else {
            ctElement.replace(getFactory().Code().createCodeSnippetExpression(getValue()));
        }
    }

    private void replaceInteger(CtExpression ctElement) {
        if (getValue() == null) {
            CtLocalVariable<Integer> evaluation = newLocalVariableDeclaration(
                    ctElement.getFactory(), int.class, "guess_fix",
                    Debug.class.getCanonicalName()
                            + ".makeSymbolicInteger(\"guess_fix\")");

            CtStatement firstStatement = getFirstStatement(ctElement);
            if (firstStatement == null) {
                return;
            }
            SpoonStatementLibrary.insertBeforeUnderSameParent(evaluation, firstStatement);
            // SpoonStatementLibrary.insertAfterUnderSameParent(getFactory().Code().createCodeSnippetStatement("System.out.println(\"guess_fix: \" + guess_fix)"),
            // getFirstStatement(ctElement));

            ctElement.replace(getFactory().Code().createCodeSnippetExpression("guess_fix"));
        } else {
            ctElement.replace(getFactory().Code().createCodeSnippetExpression(getValue()));
        }
    }

    private CtStatement getFirstStatement(CtExpression ctElement) {
        CtElement ctParent = ctElement.getParent();
        while (!(ctParent instanceof CtStatement) && ctParent != null) {
            ctParent = ctParent.getParent();
        }
        if (ctParent == null) {
            return null;
        }
        return (CtStatement) ctParent;
    }

    private void replaceBoolean(CtExpression ctElement) {
        if (getValue() == null) {
            CtLocalVariable<Boolean> evaluation = newLocalVariableDeclaration(
                    ctElement.getFactory(), boolean.class, "guess_fix",
                    Debug.class.getCanonicalName() + ".makeSymbolicBoolean(\"guess_fix\")");
            CtStatement firstStatement = getFirstStatement(ctElement);
            if (firstStatement == null) {
                return;
            }
            SpoonStatementLibrary.insertBeforeUnderSameParent(evaluation,
                    firstStatement);
            ctElement.replace(getFactory().Code().createCodeSnippetExpression(
                    "guess_fix"));
        } else {
            switch (getValue()) {
                case "1":
                    ctElement.replace(getFactory().Code().createCodeSnippetExpression("true"));
                    break;
                case "0":
                    ctElement.replace(getFactory().Code().createCodeSnippetExpression("false"));
                    break;
                default:
                    ctElement.replace(getFactory().Code().createCodeSnippetExpression(getValue()));
                    break;
            }
        }
    }
}
