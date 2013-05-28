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
package fr.inria.lille.jsemfix.patch.spoon;

import java.io.File;

import spoon.SpoonClassLoader;
import spoon.processing.AbstractProcessor;
import spoon.processing.Builder;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.support.reflect.code.CtLiteralImpl;
import fr.inria.lille.jsemfix.JavaProgram;
import fr.inria.lille.jsemfix.patch.Patch;

/**
 * @author Favio D. DeMarco
 *
 */
public final class SpoonPatch  implements Patch {

	private static final class ConditionReplacer extends AbstractProcessor<CtConditional<Object>> {
		@Override
		public void process(final CtConditional<Object> element) {
			// we declare a new snippet of code to be inserted
			CtLiteral<Boolean> snippet = new CtLiteralImpl<>();
			snippet.setFactory(this.getFactory());
			snippet.setValue(true);
			element.getCondition().replace(snippet);
		}
	}

	private static final class ReplaceIfConditionProcessor extends AbstractProcessor<CtIf> {

		@Override
		public void process(final CtIf element) {
			// we declare a new snippet of code to be inserted
			CtLiteral<Boolean> snippet = new CtLiteralImpl<>();
			snippet.setFactory(this.getFactory());
			snippet.setValue(true);
			element.getCondition().replace(snippet);
		}
	}

	private static final Processor<?> CONDITION_PROCESSOR = new ConditionReplacer();
	private static final Processor<?> IF_CONDITION_PROCESSOR = new ReplaceIfConditionProcessor();

	@Override
	public JavaProgram apply(final JavaProgram program) {
		SpoonClassLoader ccl = new SpoonClassLoader();

		ProcessingManager processingManager = ccl.getProcessingManager();
		processingManager.addProcessor(CONDITION_PROCESSOR);
		processingManager.addProcessor(IF_CONDITION_PROCESSOR);

		Builder builder = ccl.getFactory().getBuilder();

		try {
			builder.addInputSource(new File("src/main/java"));
			builder.build();

			// fragile...
			ccl.loadClass("fr.inria.lille.jsemfix.examples.bool.constant.Neg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		return new SpoonedProgram(ccl);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "if(true)";
	}

	/**
	 * @see fr.inria.lille.jsemfix.patch.Patch#asString()
	 */
	@Override
	public String asString() {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("SpoonPatch.asString");
	}

	/**
	 * @see fr.inria.lille.jsemfix.patch.Patch#getFile()
	 */
	@Override
	public File getFile() {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("SpoonPatch.getFile");
	}

	/**
	 * @see fr.inria.lille.jsemfix.patch.Patch#getLineNumber()
	 */
	@Override
	public int getLineNumber() {
		// TODO Auto-generated method stub
		// return 0;
		throw new UnsupportedOperationException("SpoonPatch.getLineNumber");
	}
}
