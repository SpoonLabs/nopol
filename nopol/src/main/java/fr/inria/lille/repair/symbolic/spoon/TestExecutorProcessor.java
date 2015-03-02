package fr.inria.lille.repair.symbolic.spoon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import fr.inria.lille.commons.spoon.SpoonedProject;

public class TestExecutorProcessor {

	public static void createMainTestClass(SpoonedProject spooner,
			String className) {
		Factory factory = spooner.spoonFactory();
		CtClass<Object> executeTestClass = factory.Class().create(className);

		CtTypeReference<String[]> typedReference = factory.Class()
				.createReference(String[].class);
		CtTypeReference<Object> returntypedReference = factory.Class()
				.createReference("void");

		Set<ModifierKind> modifiers = new LinkedHashSet<ModifierKind>(2);
		modifiers.add(ModifierKind.PUBLIC);
		modifiers.add(ModifierKind.STATIC);

		HashSet<CtTypeReference<? extends Throwable>> exceptions = new HashSet<CtTypeReference<? extends Throwable>>();
		exceptions.add(factory.Class().createReference(Exception.class));

		CtBlock<?> body = spooner.spoonFactory().Core().createBlock();

		body.addStatement(factory
				.Code()
				.createCodeSnippetStatement(
						"for (String method : methods) {\n\t\t"
								+ "String[] split = method.split(\"\\\\.\");\n\t\t"
								+ "Class.forName(method.replace(\".\" + split[split.length - 1], \"\")).getMethod(\"runJPFTest\", String[].class).invoke(null, new Object[] { new String[] { split[split.length - 1] }});}"));

		CtMethod<?> method = spooner
				.spoonFactory()
				.Method()
				.create(executeTestClass, modifiers, returntypedReference,
						"main", new ArrayList<CtParameter<?>>(), exceptions,
						body);
		spooner.spoonFactory().Method()
				.createParameter(method, typedReference, "methods");
	}
}
