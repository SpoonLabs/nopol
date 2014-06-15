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
package fr.inria.lille.nopol.synth;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

import com.google.common.base.Predicate;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class DelegatingProcessor extends AbstractProcessor<CtCodeElement> {

	private final File file;
	private final int line;
	private final List<AbstractProcessor<CtCodeElement>> processors = new ArrayList<>();
	private final Predicate<CtElement> predicate;
	private boolean process = true;

	/**
	 * @param file
	 * @param line
	 */
	public DelegatingProcessor(final Predicate<CtElement> instance, final File file, final int line) {
		this.file = file;
		this.line = line;
		this.predicate = instance;
	}

	public DelegatingProcessor addProcessor(AbstractProcessor<CtCodeElement> processor) {
		this.processors.add(processor);
		return this;
	}

	/**
	 * @see spoon.processing.AbstractProcessor#isToBeProcessed(spoon.reflect.declaration.CtElement)
	 */
	@Override
	public boolean isToBeProcessed(final CtCodeElement candidate) {
		SourcePosition position = candidate.getPosition();
		File f1;
		File f2;
		boolean isNotNullPosition =	position != null; 
		boolean isPraticable =	this.predicate.apply(candidate);
		boolean isSameLine =	position.getLine() == this.line;
		boolean isSameFile = false;
		try {
			f1 = position.getFile().getCanonicalFile().getAbsoluteFile();
			f2 = this.file.getCanonicalFile();
			isSameFile = f1.getAbsolutePath().equals(f2.getAbsolutePath());
		} catch (Exception e){
			throw new IllegalStateException(e);
		}
		return  this.process && isNotNullPosition &&  isPraticable && isSameLine && isSameFile;

	}

	@Override
	public void process(final CtCodeElement element) {
		for (AbstractProcessor<CtCodeElement> processor : this.processors) {
			processor.process(element);
		}
		this.process = false;
	}
}
