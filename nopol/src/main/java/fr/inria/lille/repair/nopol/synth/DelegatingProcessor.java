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
package fr.inria.lille.repair.nopol.synth;

import com.google.common.base.Predicate;
import spoon.processing.AbstractProcessor;
import spoon.processing.Processor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Favio D. DeMarco
 */
public class DelegatingProcessor extends AbstractProcessor<CtStatement> {

    private final File file;
    private final int line;
    private final List<Processor> processors = new ArrayList<>();
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

    public DelegatingProcessor addProcessor(final Processor<CtElement> processor) {
        this.processors.add(processor);
        return this;
    }

    /**
     * @see spoon.processing.AbstractProcessor#isToBeProcessed(spoon.reflect.declaration.CtElement)
     */
    @Override
    public boolean isToBeProcessed(final CtStatement candidate) {
        boolean isPracticable = this.predicate.apply(candidate);
        if (isPracticable) {
            SourcePosition position = candidate.getPosition();
            if (position == null || position == SourcePosition.NOPOSITION) {
                return false;
            }
            boolean isSameFile = false;
            boolean isSameLine = position.getLine() == this.line;
            try {
                File f1 = position.getFile().getCanonicalFile().getAbsoluteFile();
                File f2 = file.getCanonicalFile();
                isSameFile = f1.getAbsolutePath().equals(f2.getAbsolutePath());
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            isPracticable = this.process && isSameLine && isSameFile;
        }
        return isPracticable;
    }

    @Override
    public void process(CtStatement element) {
        for (Processor processor : this.processors) {
            processor.process(element);
        }
        this.process = false;
    }
}
