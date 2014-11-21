package fr.inria.lille.repair.symbolic.spoon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.reference.CtTypeReference;

/**
 * replace assert by a condition and a Assert.fail
 * 
 * @author T. Durieux
 * 
 */
public class AssertReplacer extends AbstractProcessor<CtInvocation<?>> {

	public AssertReplacer() {
	}

	/**
	 * Create the content of the condition
	 * 
	 * @param ctInvocation
	 * @return
	 */
	private CtBlock<Object> createThen(CtInvocation ctInvocation) {
		CtCodeSnippetStatement invocation = ctInvocation.getFactory().Core()
				.createCodeSnippetStatement();

		CtBlock<Object> thenStatement = ctInvocation.getFactory().Core()
				.createBlock();
		
		CtAssert<Object> ctAssert = ctInvocation.getFactory().Core()
				.createAssert();

		CtCodeSnippetStatement assertInvocation = ctInvocation.getFactory()
				.Core().createCodeSnippetStatement();

		assertInvocation.setValue(Assert.class.getCanonicalName() +
		 ".fail(\"" + ctInvocation.toString().replace("\"", "\\\"") + "\")");
		CtCodeSnippetExpression<Boolean> assertExpression = getFactory().Core().createCodeSnippetExpression();
		assertExpression.setValue("false");
		
		ctAssert.setAssertExpression(assertExpression);
		ctAssert.setExpression(getFactory().Code().createCodeSnippetExpression("\"" + ctInvocation.toString().replace("\"", "\\\"") + "\""));
		thenStatement.addStatement((CtStatement) ctAssert);

		return thenStatement;
	}

	/**
	 * replace assert Equals
	 * 
	 * @param ctInvocation
	 * @return
	 */
	private CtElement replaceAssertEquals(CtInvocation ctInvocation) {
		List arguments = ctInvocation.getArguments();
		CtIf newIf = ctInvocation.getFactory().Core().createIf();
		CtCodeSnippetExpression<Boolean> condition = ctInvocation.getFactory()
				.Core().createCodeSnippetExpression();
		Object elem1 = arguments.get(0);

		if (elem1 instanceof CtTypedElement<?>) {
			Class classArg1 = ((CtTypedElement<?>) elem1).getType()
					.getActualClass();

			if (classArg1.equals(int.class) || classArg1.equals(Integer.class)
					|| classArg1.equals(boolean.class)
					|| classArg1.equals(Boolean.class)
					|| classArg1.equals(double.class)
					|| classArg1.equals(Double.class)
					|| classArg1.equals(float.class)
					|| classArg1.equals(Float.class)
					|| classArg1.equals(char.class)
					|| classArg1.equals(Character.class)) {
				condition.setValue("" + arguments.get(0) + " != "
						+ arguments.get(1) + "");
			} else {
				condition.setValue("!" + arguments.get(0) + ".equals("
						+ arguments.get(1) + ")");
			}
		}
		newIf.setCondition(condition);

		newIf.setThenStatement(createThen(ctInvocation));

		return newIf;
	}

	/**
	 * replace assertTrue
	 * 
	 * @param ctInvocation
	 * @return
	 */
	private CtElement replaceAssertTrueFalse(boolean type, CtInvocation<?> ctInvocation) {
		List<?> arguments = ctInvocation.getArguments();
		CtIf newIf = ctInvocation.getFactory().Core().createIf();
		newIf.setCondition((CtExpression<Boolean>) arguments.get(0));
		if(type) {
			newIf.setElseStatement(createThen(ctInvocation));
		} else {
			newIf.setThenStatement(createThen(ctInvocation));
		}
		return newIf;
	}

	@Override
	public void process(CtInvocation<?> ctInvocation) {
		// ignore no assert invocation
		if (!ctInvocation.getExecutable().getSimpleName().contains("assert")) {
			return;
		}
		switch (ctInvocation.getExecutable().getSimpleName()) {
		case "assertEquals":
			ctInvocation.replace(replaceAssertEquals(ctInvocation));
			break;
		case "assertFalse":
			ctInvocation.replace(replaceAssertTrueFalse(false, ctInvocation));
			break;
		case "assertTrue":
			ctInvocation.replace(replaceAssertTrueFalse(true, ctInvocation));
			break;
		default:
			System.out.println("Assert transformation not found: "
					+ ctInvocation.getExecutable().getSimpleName());
			return;
		}
		
		CtClass<?> parent = ctInvocation.getParent(CtClass.class);
		CtTypeReference<String[]> typedReference = getFactory().Class().createReference(String[].class);
		if(parent.getMethod("runTest", typedReference) == null) {
			CtTypeReference<Object> returntypedReference = getFactory().Class()
					.createReference("void");
			
			Set<ModifierKind> modifiers = new LinkedHashSet<ModifierKind>(2);
			modifiers.add(ModifierKind.PUBLIC);
			modifiers.add(ModifierKind.STATIC);
			
			
			CtBlock<?> body = getFactory().Core().createBlock();
			body.addStatement(getFactory().Code().createCodeSnippetStatement("for (String method : methods) {" + parent.getQualifiedName() + ".class.getMethod(method, null).invoke(new " + parent.getQualifiedName() +"());}"));
			HashSet<CtTypeReference<? extends Throwable>> exceptions = new HashSet<CtTypeReference<? extends Throwable>>();
			exceptions.add(getFactory().Class().createReference(Exception.class));
			CtMethod<?> method = getFactory().Method().create(parent, modifiers, returntypedReference, "runTest", new ArrayList<CtParameter<?>>(), exceptions, body);
			getFactory().Method().createParameter(method, typedReference, "methods");
		}
	}

}
