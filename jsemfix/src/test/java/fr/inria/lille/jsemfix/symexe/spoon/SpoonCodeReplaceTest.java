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
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.support.reflect.code.CtLiteralImpl;

/**
 * @author Favio D. DeMarco
 * 
 */
public class SpoonCodeReplaceTest {

	public static final class ReplaceConditionalProcessor extends AbstractProcessor<CtConditional<Object>> {

		@Override
		public void process(final CtConditional<Object> element) {
			// we declare a new snippet of code to be inserted
			CtLiteral<Boolean> snippet = new CtLiteralImpl<>();
			snippet.setFactory(this.getFactory());
			snippet.setValue(true);

			element.getCondition().replace(snippet);
		}
	}

	public static final class ReplaceIfConditionProcessor extends AbstractProcessor<CtIf> {

		@Override
		public void process(final CtIf element) {
			// we declare a new snippet of code to be inserted
			CtLiteral<Boolean> snippet = new CtLiteralImpl<>();
			snippet.setFactory(this.getFactory());
			snippet.setValue(true);

			element.getCondition().replace(snippet);
		}
	}

	@Test
	public void testProcessCtExpressionOfObject() throws Exception {

		SpoonClassLoader ccl = new SpoonClassLoader();

		ProcessingManager processingManager = ccl.getProcessingManager();
		processingManager.addProcessor(ReplaceConditionalProcessor.class);

		Builder builder = ccl.getFactory().getBuilder();
		builder.addInputSource(new File("src/spoon/java"));
		builder.build();

		// fragile...
		Class<?> targetClass = ccl.loadClass("fr.inria.lille.jsemfix.symexe.spoon.AMethodWithAConditional");

		int parameter = 1;
		Object value = targetClass.getMethod("abs", int.class).invoke(null, parameter);
		assertEquals(-parameter, value);
	}

	@Test
	public void testReplaceIfCondition() throws Exception {

		SpoonClassLoader ccl = new SpoonClassLoader();

		ProcessingManager processingManager = ccl.getProcessingManager();
		processingManager.addProcessor(ReplaceIfConditionProcessor.class);

		Builder builder = ccl.getFactory().getBuilder();
		builder.addInputSource(new File("src/spoon/java"));
		builder.build();

		// fragile...
		Class<?> targetClass = ccl.loadClass("fr.inria.lille.jsemfix.symexe.spoon.AMethodWithAnIfThenElse");

		int parameter = 1;
		Object value = targetClass.getMethod("abs", int.class).invoke(null, parameter);
		assertEquals(-parameter, value);
	}
}
