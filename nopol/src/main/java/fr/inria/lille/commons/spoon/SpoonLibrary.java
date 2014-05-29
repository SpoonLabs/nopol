package fr.inria.lille.commons.spoon;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.Factory;

import com.martiansoftware.jsap.JSAPException;

public class SpoonLibrary {
	
	public static <T> CtExpression<T> composedExpression(String codeSnippet, BinaryOperatorKind operator, CtExpression<T> expression) {
		CodeFactory codeFactory = codeFactoryOf(expression);
		CtCodeSnippetExpression<T> newExpression = codeFactory.createCodeSnippetExpression(codeSnippet);
		CtBinaryOperator<T> composedExpression = codeFactory.createBinaryOperator(newExpression, expression, operator);
		CtElement parent = expression.getParent();
		newExpression.setParent(composedExpression);
		expression.setParent(composedExpression);
		composedExpression.setParent(parent);
		return composedExpression;
	}
	
	public static CtCodeSnippetStatement statementFrom(String codeSnippet, CtElement parent) {
		CodeFactory codeFactory = codeFactoryOf(parent);
		CtCodeSnippetStatement newStatement = codeFactory.createCodeSnippetStatement(codeSnippet);
		newStatement.setParent(parent);
		return newStatement;
	}
	
	public static CodeFactory codeFactoryOf(CtElement element) {
		return element.getFactory().Code();
	}
	
	public static Environment newEnvironment() {
		return newFactory().getEnvironment();
	}
	
	public static Factory newFactory() {
		return launcher().createFactory();
	}
	
	private static Launcher launcher() {
		if (launcher == null) {
			try {
				launcher = new Launcher();
			} catch (JSAPException e) {
				e.printStackTrace();
			}
		}
		return launcher;
	}
	
	private static Launcher launcher;
}
