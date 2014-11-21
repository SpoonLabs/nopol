package fr.inria.lille.repair.symbolic.spoon;

import static fr.inria.lille.commons.spoon.util.SpoonModelLibrary.newLocalVariableDeclaration;

import java.io.File;
import java.util.List;

import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.commons.spoon.util.SpoonStatementLibrary;
import fr.inria.lille.repair.nopol.SourceLocation;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.support.reflect.code.CtUnaryOperatorImpl;
import xxl.java.library.FileLibrary;

public class ToJPFReplacer extends AbstractProcessor<CtStatement> {

	private String className;
	private int lineNumber;

	public ToJPFReplacer(String className, int lineNumber) {
		super();
		this.className = className;
		this.lineNumber = lineNumber;
	}

	private Class<?> getClassOfStatement(CtStatement candidate) {
		CtElement parent = candidate.getParent();
		while (parent != null && !(parent instanceof CtClass<?>)) {
			parent = parent.getParent();
		}
		if (parent != null && parent instanceof CtClass<?>) {
			return ((CtClass<?>) parent).getActualClass();
		}
		return null;
	}

	public boolean isToBeProcessed(SourceLocation location, SpoonedClass spooner) {
		for (CtPackage pack : spooner.allPackages()) {
			if (location.getContainingClassName().contains(pack.getQualifiedName())) {
				List<CtStatement> elements = pack.getElements(new AbstractFilter<CtStatement>(CtStatement.class) {
					@Override
					public boolean matches(CtStatement candidate) {
						return isToBeProcessed(candidate);
					}
				});
				return elements.size() > 0;
			}
		}
		return false;
	}

	@Override
	public boolean isToBeProcessed(CtStatement candidate) {
		SourcePosition position = candidate.getPosition();
		if (position == null) {
			return false;
		}
		boolean isSameLine = position.getLine() == this.lineNumber;
		if (!isSameLine)
			return false;

		Class<?> statementClass = getClassOfStatement(candidate);
		if (statementClass == null) {
			return false;
		}
		boolean isSameFile = statementClass.getCanonicalName().equals(
				this.className);
		if (!isSameFile) {
			return false;
		}
		CtElement parent = candidate.getParent();
		if (parent == null) {
			return false;
		}
		boolean isLocalVariable = candidate instanceof CtLocalVariable<?>;
		boolean isPrimitiveLiteral = false;

		if (isLocalVariable) {
			CtLocalVariable<?> ctLocalVariable = (CtLocalVariable<?>) candidate;
			Class<?> localVariableClass = ctLocalVariable.getType()
					.getActualClass();

			isPrimitiveLiteral = localVariableClass.equals(Integer.class)
					|| localVariableClass.equals(int.class)
					|| localVariableClass.equals(Boolean.class)
					|| localVariableClass.equals(boolean.class);
		} else if (candidate instanceof CtAssignment<?, ?>) {
			CtAssignment<?, ?> ctAssignment = (CtAssignment<?, ?>) candidate;
			Class<?> localVariableClass = ctAssignment.getType().getActualClass();

			isPrimitiveLiteral = localVariableClass.equals(Integer.class)
					|| localVariableClass.equals(int.class)
					|| localVariableClass.equals(Boolean.class)
					|| localVariableClass.equals(boolean.class);
		}

		return isPrimitiveLiteral;
	}

	@Override
	public void process(CtStatement ctStatement) {
		if (!(ctStatement instanceof CtLocalVariable<?>) && !(ctStatement instanceof CtAssignment<?,?>))
			return;

		CtStatement ctLocalVariable =  ctStatement;

		List<CtElement> elements = ctLocalVariable
				.getElements(new AbstractFilter<CtElement>(Object.class) {

					@Override
					public boolean matches(CtElement elem) {
						return elem instanceof CtLiteral<?>
								|| elem instanceof CtUnaryOperatorImpl<?>;
					}
				});
		for (CtElement ctElement : elements) {
			CtTypedElement ctTyped = (CtTypedElement) ctElement;
			Class localVariableClass = ctTyped.getType().getActualClass();
			if (localVariableClass.equals(Integer.class)
					|| localVariableClass.equals(int.class)) {
				replaceInteger(ctElement);
			} else if (localVariableClass.equals(Boolean.class)
					|| localVariableClass.equals(boolean.class)) {
				replaceBoolean(ctElement);
			}
			System.out.println(ctElement.getParent());
		}
	}

	private void replaceInteger(CtElement ctElement) {
		CtCodeSnippetStatement statement = ctElement.getFactory().Core()
				.createCodeSnippetStatement();

		statement
				.setValue("gov.nasa.jpf.symbc.Debug.makeSymbolicInteger(\"guess_fix\");");

		CtLocalVariable<Integer> evaluation = newLocalVariableDeclaration(
				ctElement.getFactory(), int.class, "guess_fix",
				"gov.nasa.jpf.symbc.Debug.makeSymbolicInteger(\"guess_fix\")");

		SpoonStatementLibrary.insertBeforeUnderSameParent(evaluation, getFristStatement(ctElement));
		//SpoonStatementLibrary.insertAfterUnderSameParent(getFactory().Code().createCodeSnippetStatement("System.out.println(\"guess_fix: \" + guess_fix)"), getFristStatement(ctElement));
		ctElement.replace(getFactory().Code().createCodeSnippetExpression(
				"guess_fix"));
	}
	
	private CtStatement getFristStatement(CtElement ctElement) {
		CtElement ctParent = ctElement.getParent();
		while (!(ctParent instanceof CtStatement) && ctParent != null) {
			ctParent = ctParent.getParent();
		}
		return (CtStatement) ctParent;
	}

	private void replaceBoolean(CtElement ctElement) {
		CtCodeSnippetExpression statement = ctElement.getFactory().Core()
				.createCodeSnippetExpression();
		statement
				.setValue("gov.nasa.jpf.symbc.Debug.makeSymbolicBoolean(\"guess_fix\");");
		ctElement.replace(statement);
	}

}
