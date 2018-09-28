package fr.inria.lille.localization;

import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtIf;
import spoon.reflect.cu.SourcePosition;
import xxl.java.junit.TestCase;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaultLocalizerUtils {
	private FaultLocalizerUtils(){}

	/**
	 * using reflection to build the name of all test methods to be run
	 *
	 * @param classOfTestCase
	 * @return
	 */
	public static List<String> getTestMethods(Class classOfTestCase) {
		List<String> methodsNames = new ArrayList<>();
		for (Method method : classOfTestCase.getMethods()) {
			for (Annotation a : method.getAnnotations()) {
				// clever, but requires that junit is in the nopol classpath, similar problem
				//if (m.isAnnotationPresent((Class<? extends Annotation>) urlClassLoader.loadClass("org.junit.Test"))) {

				// so we do name-based
				if (a.annotationType().getCanonicalName().equals("org.junit.Test")) {
					methodsNames.add(classOfTestCase.getName()+"#"+method.getName());
				}
			}

			if (isJunit3TestMethod(method) && !methodsNames.contains(method.getName())) {
				methodsNames.add(classOfTestCase.getName()+"#"+method.getName());
			}
		}

		return methodsNames;
	}

	public static boolean isJunit3TestMethod(Method m) {
		return m.getParameterTypes().length == 0 && m.getName().startsWith("test") && m.getReturnType().equals(Void.TYPE) && Modifier.isPublic(m.getModifiers());
	}

}
