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

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import spoon.SpoonClassLoader;
import spoon.processing.AbstractProcessor;
import spoon.processing.Builder;
import spoon.processing.ProcessingManager;
import spoon.reflect.Factory;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtLiteral;
import spoon.support.DefaultCoreFactory;
import spoon.support.RuntimeProcessingManager;
import spoon.support.StandardEnvironment;
import spoon.support.reflect.code.CtLiteralImpl;

/**
 * @author Favio D. DeMarco
 * 
 */
public class SpoonCodeReplaceTest {

	public static final class ReplaceProcessor extends AbstractProcessor<CtConditional<Object>> {

		@Override
		public void process(final CtConditional<Object> element) {
			System.out.printf("Processing: %s %s: %s%n", element.getClass().getSimpleName(), element.getPosition(),
					element);

			// we declare a new snippet of code to be inserted
			CtLiteral<Boolean> snippet = new CtLiteralImpl<>();
			snippet.setFactory(this.getFactory());
			snippet.setValue(true);

			element.getCondition().replace(snippet);

			System.out.printf("New value: %s%n", element);
		}
	}

	@Test
	public void testProcessCtExpressionOfObject() throws Exception {

		SpoonClassLoader ccl = new SpoonClassLoader();

		ProcessingManager processingManager = ccl.getProcessingManager();
		processingManager.addProcessor(ReplaceProcessor.class);

		Builder builder = ccl.getFactory().getBuilder();
		builder.addInputSource(new File(
				"src/test/java/fr/inria/lille/jsemfix/symexe/spoon/AMethodWithAConditional.java"));
		builder.build();

		processingManager.process();

		Class<?> targetClass = ccl.loadClass(AMethodWithAConditional.class.getName());

		int parameter = 1;
		Object value = targetClass.getMethod("abs", int.class).invoke(null, parameter);
		assertEquals(-parameter, value);
	}

	@Test
	public void testReplaceProcessor() throws Exception {
		StandardEnvironment env = new StandardEnvironment();
		Factory factory = new Factory(new DefaultCoreFactory(), env);

		Builder builder = factory.getBuilder();
		builder.addInputSource(new File(
				"src/test/java/fr/inria/lille/jsemfix/symexe/spoon/AMethodWithAConditional.java"));
		// builder.addInputSource(new File("src/test/java"));
		builder.build();

		ProcessingManager processingManager = new RuntimeProcessingManager(factory);
		processingManager.addProcessor(ReplaceProcessor.class);
		processingManager.process();
	}
}
