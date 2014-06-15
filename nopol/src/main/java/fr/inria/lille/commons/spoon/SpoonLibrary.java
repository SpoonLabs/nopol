package fr.inria.lille.commons.spoon;

import java.io.File;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.Factory;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.lille.commons.classes.ClassLibrary;

public class SpoonLibrary {
	
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
	
	public static <T> CtExpression<T> composedExpression(String codeSnippet, BinaryOperatorKind operator, CtExpression<T> expression) {
		CodeFactory codeFactory = codeFactoryOf(expression);
		CtCodeSnippetExpression<T> newExpression = codeFactory.createCodeSnippetExpression(codeSnippet);
		CtBinaryOperator<T> composedExpression = codeFactory.createBinaryOperator(newExpression, expression, operator);
		groupBranch(expression.getParent(), composedExpression, newExpression, expression);
		return composedExpression;
	}
	
	public static void groupBranch(CtElement root, CtElement parentNode, CtElement... siblingNodes) {
		parentNode.setParent(root);
		for (CtElement sibling : siblingNodes) {
			sibling.setParent(parentNode);
		}
	}
	
	public static CtCodeSnippetStatement statementFrom(String codeSnippet, CtElement parent) {
		CodeFactory codeFactory = codeFactoryOf(parent);
		CtCodeSnippetStatement newStatement = codeFactory.createCodeSnippetStatement(codeSnippet);
		newStatement.setParent(parent);
		return newStatement;
	}
	
	public static boolean isBlock(CtElement element) {
		return ClassLibrary.isInstanceOf(CtBlock.class, element);
	}
	
	public static boolean isLocalVariable(CtElement element) {
		return ClassLibrary.isInstanceOf(CtLocalVariable.class, element);
	}
	
	public static boolean isAnonymousClass(CtElement element) {
		return ClassLibrary.isInstanceOf(CtNewClass.class, element);
	}
	
	public static boolean isConstructor(CtElement element) {
		return ClassLibrary.isInstanceOf(CtConstructor.class, element);
	}
	
	public static boolean isInitializationBlock(CtElement element) {
		return ClassLibrary.isInstanceOf(CtAnonymousExecutable.class, element);
	}
	
	public static boolean isAType(CtElement element) {
		return ClassLibrary.isInstanceOf(CtSimpleType.class, element);
	}
	
	public static boolean isField(CtElement element) {
		return ClassLibrary.isInstanceOf(CtField.class, element);
	}
	
	public static boolean allowsModifiers(CtElement element) {
		return ClassLibrary.isInstanceOf(CtModifiable.class, element);
	}
	
	public static boolean hasStaticModifier(CtElement element) {
		if (allowsModifiers(element)) {
			return ClassLibrary.castTo(CtModifiable.class, element).getModifiers().contains(ModifierKind.STATIC);
		}
		return false;
	}
	
	public static boolean inStaticCode(CtElement element) {
		if (allowsModifiers(element)) {
			return hasStaticModifier(element);
		}
		return hasStaticModifier(element.getParent(CtModifiable.class));
	}
	
	public static CtStatement statementOf(CtCodeElement codeElement) {
		Class<CtStatement> statementClass = CtStatement.class;
		if (ClassLibrary.isInstanceOf(statementClass, codeElement)) {
			return ClassLibrary.castTo(statementClass, codeElement);
		}
		return codeElement.getParent(statementClass);
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
