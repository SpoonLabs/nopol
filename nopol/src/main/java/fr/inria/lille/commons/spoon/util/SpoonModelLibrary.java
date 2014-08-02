package fr.inria.lille.commons.spoon.util;

import static fr.inria.lille.commons.spoon.util.SpoonStatementLibrary.asBlock;

import java.io.File;
import java.util.Collection;
import java.util.List;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.lille.commons.collections.ListLibrary;

public class SpoonModelLibrary {

	public static Factory modelFor(File sourceFile) {
		Factory factory = newFactory();
		factory.getEnvironment().setDebug(true);
		try {
			SpoonCompiler compiler = launcher().createCompiler(factory);
			compiler.addInputSource(sourceFile);
			compiler.addTemplateSource(sourceFile);
			compiler.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return factory;
	}
	
	public static CtBreak newBreak(Factory factory) {
		return factory.Core().createBreak();
	}
	
	public static <T> CtLiteral<T> newLiteral(Factory factory, T value) {
		CtLiteral<T> newLiteral = factory.Core().createLiteral();
		newLiteral.setValue(value);
		return newLiteral;
	}
	
	public static <T> CtLocalVariable<T> newLocalVariableDeclaration(Factory factory, String classSimpleName, String variableName, T defaultValue, CtElement parent) {
		CtLocalVariable<T> localVariable = newLocalVariableDeclaration(factory, classSimpleName, variableName, defaultValue);
		setParent(parent, localVariable);
		return localVariable;
	}
	
	public static <T> CtLocalVariable<T> newLocalVariableDeclaration(Factory factory, String classSimpleName, String variableName, T defaultValue) {
		CtTypeReference<T> type = factory.Core().createTypeReference();
		type.setSimpleName(classSimpleName);
		CtLiteral<T> defaultExpression = newLiteral(factory, defaultValue);
		return factory.Code().createLocalVariable(type, variableName, defaultExpression);
	}

	public static <T> CtExpression<T> newExpressionFromSnippet(Factory factory, String codeSnippet, Class<T> expressionClass, CtElement parent) {
		CtExpression<T> expression = newExpressionFromSnippet(factory, codeSnippet, expressionClass);
		setParent(parent, expression);
		return expression;
	}
	
	public static <T> CtExpression<T> newExpressionFromSnippet(Factory factory, String codeSnippet, Class<T> expressionClass) {
		return factory.Code().createCodeSnippetExpression(codeSnippet);
	}
	
	public static CtStatement newStatementFromSnippet(Factory factory, String codeSnippet, CtElement parent) {
		CtStatement statement = newStatementFromSnippet(factory, codeSnippet);
		setParent(parent, statement);
		return statement;
	}
	
	public static CtStatement newStatementFromSnippet(Factory factory, String codeSnippet) {
		return factory.Code().createCodeSnippetStatement(codeSnippet);
	}

	public static CtBlock<CtStatement> newBlock(Factory factory, CtStatement... statements) {
		return newBlock(factory, ListLibrary.newArrayList(statements));
	}
	
	public static CtBlock<CtStatement> newBlock(Factory factory, List<CtStatement> blockStatements) {
		CtBlock<CtStatement> newBlock = factory.Core().createBlock();
		setParent(newBlock, blockStatements);
		newBlock.setStatements(blockStatements);
		return newBlock;
	}
	
	public static CtExpression<Boolean> newConjunctionExpression(Factory factory, CtExpression<Boolean> leftExpression, CtExpression<Boolean> rightExpression) {
		return newComposedExpression(factory, leftExpression, rightExpression, BinaryOperatorKind.AND);
	}
	
	public static CtExpression<Boolean> newDisjunctionExpression(Factory factory, CtExpression<Boolean> leftExpression, CtExpression<Boolean> rightExpression) {
		return newComposedExpression(factory, leftExpression, rightExpression, BinaryOperatorKind.OR);
	}
	
	public static <T> CtExpression<T> newComposedExpression(Factory factory, CtExpression<T> leftExpression, CtExpression<T> rightExpression, BinaryOperatorKind operator) {
		CtBinaryOperator<T> composedExpression = factory.Code().createBinaryOperator(leftExpression, rightExpression, operator);
		setParent(composedExpression, leftExpression, rightExpression);
		return composedExpression;
	}
	
	public static CtIf newIf(Factory factory, CtExpression<Boolean> condition, CtStatement thenBranch) {
		CtIf newIf = factory.Core().createIf();
		thenBranch = asBlock(thenBranch);
		setParent(newIf, condition, thenBranch);
		newIf.setCondition(condition);
		newIf.setThenStatement(thenBranch);
		return newIf;
	}
	
	public static CtIf newIf(Factory factory, CtExpression<Boolean> condition, CtStatement thenBranch, CtStatement elseBranch) {
		CtIf newIf = newIf(factory, condition, thenBranch);
		elseBranch = asBlock(elseBranch);
		setParent(newIf, elseBranch);
		newIf.setElseStatement(elseBranch);
		return newIf;
	}
	
	public static void setParent(CtElement parent, Collection<? extends CtElement> children) {
		setParent(parent, children.toArray(new CtElement[children.size()]));
	}
	
	public static void setParent(CtElement parent, CtElement... children) {
		for (CtElement child : children) {
			child.setParent(parent);
		}
	}
	
	public static void setLoopBody(CtWhile loop, CtStatement loopBody) {
		loopBody = asBlock(loopBody);
		setParent(loop, loopBody);
		loop.setBody(loopBody);
	}
	
	public static void setLoopingCondition(CtWhile loop, CtExpression<Boolean> loopingCondition) {
		setParent(loop, loopingCondition);
		loop.setLoopingExpression(loopingCondition);
	}
	
	public static CoreFactory coreFactoryOf(CtElement element) {
		return element.getFactory().Core();
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
