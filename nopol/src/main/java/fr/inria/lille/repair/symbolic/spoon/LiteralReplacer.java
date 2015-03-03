package fr.inria.lille.repair.symbolic.spoon;

import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newLocalVariableDeclaration;
import fr.inria.lille.commons.spoon.util.SpoonStatementLibrary;
import gov.nasa.jpf.symbc.Debug;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtTypedElement;

public class LiteralReplacer extends SymbolicProcessor {

	public LiteralReplacer(Class<?> cl, CtStatement statement) {
		super(statement);
		if (statement instanceof CtAssignment<?, ?>) {
			super.defaultValue = ((CtAssignment<?, ?>) statement)
					.getAssignment().toString();
		} else if (statement instanceof CtLocalVariable<?>) {
			super.defaultValue = ((CtLocalVariable<?>) statement)
					.getDefaultExpression().toString();
		}
		super.setType(cl);
	}

	@Override
	public void process(CtStatement ctStatement) {
		if (!(ctStatement instanceof CtLocalVariable<?>)
				&& !(ctStatement instanceof CtAssignment<?, ?>))
			return;

		Class<?> localVariableClass = ((CtTypedElement<?>) ctStatement)
				.getType().getActualClass();
		if (localVariableClass.equals(Integer.class)
				|| localVariableClass.equals(int.class)) {
			if (ctStatement instanceof CtAssignment<?, ?>) {
				replaceInteger(((CtAssignment<?, ?>) ctStatement)
						.getAssignment());
			} else if (ctStatement instanceof CtLocalVariable<?>) {
				replaceInteger(((CtLocalVariable<?>) ctStatement)
						.getDefaultExpression());
			}
		} else if (localVariableClass.equals(Double.class)
				|| localVariableClass.equals(double.class)) {
			if (ctStatement instanceof CtAssignment<?, ?>) {
				replaceDouble(((CtAssignment<?, ?>) ctStatement)
						.getAssignment());
			} else if (ctStatement instanceof CtLocalVariable<?>) {
				replaceDouble(((CtLocalVariable<?>) ctStatement)
						.getDefaultExpression());
			}
		} else if (localVariableClass.equals(Boolean.class)
				|| localVariableClass.equals(boolean.class)) {
			if (ctStatement instanceof CtAssignment<?, ?>) {
				replaceBoolean(((CtAssignment<?, ?>) ctStatement)
						.getAssignment());
			} else if (ctStatement instanceof CtLocalVariable<?>) {
				replaceBoolean(((CtLocalVariable<?>) ctStatement)
						.getDefaultExpression());
			}
		}
		System.out.println(ctStatement);
	}

	private void replaceDouble(CtElement ctElement) {
		if (getValue() == null) {
			CtLocalVariable<Double> evaluation = newLocalVariableDeclaration(
					ctElement.getFactory(), double.class, "guess_fix",
					Debug.class.getCanonicalName()
							+ ".makeSymbolicReal(\"guess_fix\")");

			CtStatement firstStatement = getFristStatement(ctElement);
			if (firstStatement == null) {
				return;
			}
			SpoonStatementLibrary.insertBeforeUnderSameParent(evaluation,
					firstStatement);
			// SpoonStatementLibrary.insertAfterUnderSameParent(getFactory().Code().createCodeSnippetStatement("System.out.println(\"guess_fix: \" + guess_fix)"),
			// getFristStatement(ctElement));
			ctElement.replace(getFactory().Code().createCodeSnippetExpression(
					"guess_fix"));
		} else {
			ctElement.replace(getFactory().Code().createCodeSnippetExpression(
					getValue()));
		}
	}

	private void replaceInteger(CtElement ctElement) {
		if (getValue() == null) {
			CtLocalVariable<Integer> evaluation = newLocalVariableDeclaration(
					ctElement.getFactory(), int.class, "guess_fix",
					Debug.class.getCanonicalName()
							+ ".makeSymbolicInteger(\"guess_fix\")");

			CtStatement firstStatement = getFristStatement(ctElement);
			if (firstStatement == null) {
				return;
			}
			SpoonStatementLibrary.insertBeforeUnderSameParent(evaluation,
					firstStatement);
			// SpoonStatementLibrary.insertAfterUnderSameParent(getFactory().Code().createCodeSnippetStatement("System.out.println(\"guess_fix: \" + guess_fix)"),
			// getFristStatement(ctElement));
			ctElement.replace(getFactory().Code().createCodeSnippetExpression(
					"guess_fix"));
		} else {
			ctElement.replace(getFactory().Code().createCodeSnippetExpression(
					getValue()));
		}
	}

	private CtStatement getFristStatement(CtElement ctElement) {
		CtElement ctParent = ctElement.getParent();
		while (!(ctParent instanceof CtStatement) && ctParent != null) {
			ctParent = ctParent.getParent();
		}
		if (!(ctParent instanceof CtStatement)) {
			return null;
		}
		return (CtStatement) ctParent;
	}

	private void replaceBoolean(CtElement ctElement) {
		if (getValue() == null) {
			CtLocalVariable<Boolean> evaluation = newLocalVariableDeclaration(
					ctElement.getFactory(), boolean.class, "guess_fix",
					Debug.class.getCanonicalName()
							+ ".makeSymbolicBoolean(\"guess_fix\")");
			CtStatement firstStatement = getFristStatement(ctElement);
			if (firstStatement == null) {
				return;
			}
			SpoonStatementLibrary.insertBeforeUnderSameParent(evaluation,
					firstStatement);
			ctElement.replace(getFactory().Code().createCodeSnippetExpression(
					"guess_fix"));
		} else {
            if(getValue().equals("1")) {
                ctElement.replace(getFactory().Code()
                        .createCodeSnippetExpression("true"));
            } else if(getValue().equals("0")) {
                ctElement.replace(getFactory().Code()
                        .createCodeSnippetExpression("false"));
            } else {
                ctElement.replace(getFactory().Code()
                        .createCodeSnippetExpression(getValue()));
            }
		}
	}
}
