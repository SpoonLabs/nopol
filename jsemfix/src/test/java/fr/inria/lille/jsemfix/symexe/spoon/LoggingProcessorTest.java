/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.lille.jsemfix.symexe.spoon;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import spoon.SpoonClassLoader;
import spoon.processing.Builder;
import spoon.processing.ProcessingManager;
import fr.inria.lille.jsemfix.gzoltar.ObjectsTest;

/**
 * @author Favio D. DeMarco
 *
 */
public class LoggingProcessorTest {

	/**
	 * Test method for
	 * {@link fr.inria.lille.jsemfix.symexe.spoon.LoggingProcessor#process(spoon.reflect.code.CtExpression)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProcessCtExpressionOfObject() throws Exception {

		SpoonClassLoader ccl = new SpoonClassLoader();

		ProcessingManager processingManager = ccl.getProcessingManager();
		processingManager.addProcessor(LoggingProcessor.class);

		Builder builder = ccl.getFactory().getBuilder();
		builder.addInputSource(new File("src/test/java/fr/inria/lille/jsemfix/gzoltar/Objects.java"));
		// builder.addInputSource(new File("src/test/java"));
		builder.build();

		processingManager.process();

		Class<?> targetClass = ccl.loadClass(ObjectsTest.class.getName());

		Method[] targetMethods = targetClass.getMethods();

		for (Method targetMethod : targetMethods) {
			if (targetMethod.getName().startsWith("test") && targetMethod.getParameterTypes().length == 0) {

				try {
					targetMethod.invoke(targetClass.newInstance());
				} catch (InvocationTargetException e) {
					if (e.getCause() instanceof NullPointerException) {
						// expected, do nothing
					}
				}
			}
		}
	}
}
