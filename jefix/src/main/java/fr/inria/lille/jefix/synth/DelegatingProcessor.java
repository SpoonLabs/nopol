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
package fr.inria.lille.jefix.synth;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.cu.SourcePosition;

import com.google.common.base.Predicate;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class DelegatingProcessor extends AbstractProcessor<CtCodeElement> {

	private final File file;
	private final int line;
	private final List<Processor> processors = new ArrayList<>();
	private final Predicate<CtCodeElement> predicate;
	private boolean process = true;

	/**
	 * @param file
	 * @param line
	 */
	public DelegatingProcessor(final Predicate<CtCodeElement> instance, final File file, final int line) {
		this.file = file;
		this.line = line;
		this.predicate = instance;
	}

	public DelegatingProcessor addProcessor(final Processor processor) {
		this.processors.add(processor);
		return this;
	}

	/**
	 * @see spoon.processing.AbstractProcessor#isToBeProcessed(spoon.reflect.declaration.CtElement)
	 */
	@Override
	public boolean isToBeProcessed(final CtCodeElement candidate) {
		SourcePosition position = candidate.getPosition();
		return this.process && position != null && this.predicate.apply(candidate) && position.getLine() == this.line
				&& position.getFile().getAbsolutePath().equals(this.file.getAbsolutePath());
	}

	@Override
	public void process(final CtCodeElement element) {
		for (Processor processor : this.processors) {
			processor.process(this.getFactory(), element);
		}
		this.process = false;
	}
}
