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
package fr.inria.lille.repair.symbolic.synth;

import static fr.inria.lille.repair.common.patch.Patch.NO_PATCH;

import java.net.URL;
import java.util.Collection;

import fr.inria.lille.repair.common.synth.StatementType;
import org.slf4j.LoggerFactory;

import xxl.java.junit.TestCase;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.commons.synthesis.CodeGenesis;
import fr.inria.lille.commons.synthesis.ConstraintBasedSynthesis;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.patch.StringPatch;
import fr.inria.lille.repair.symbolic.spoon.SymbolicProcessor;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class DefaultSynthesizer<T> implements Synthesizer {

	private final SourceLocation sourceLocation;
	private final JPFRunner<T> constraintModelBuilder;
	private final StatementType type;
	private final SymbolicProcessor processor;

	public DefaultSynthesizer(JPFRunner<T> constraintModelBuilder,
			SourceLocation sourceLocation, StatementType type,
			SymbolicProcessor processor) {
		this.constraintModelBuilder = constraintModelBuilder;
		this.sourceLocation = sourceLocation;
		this.type = type;
		this.processor = processor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.inria.lille.jefix.synth.Synthesizer#buildPatch(java.net.URL[],
	 * java.lang.String[])
	 */
	@Override
	public Patch buildPatch(URL[] classpath, String[] testClasses,
			Collection<TestCase> failures, SpoonedProject cleanSpoon,
			String mainClass) {
		Collection<Specification<T>> data = constraintModelBuilder.buildFor(
				classpath, testClasses, failures, cleanSpoon, mainClass);

		// XXX FIXME TODO move this
		// there should be at least two sets of values, otherwise the patch
		// would be "true" or "false"
		int dataSize = data.size();

		if (dataSize < 1) {
			LoggerFactory
					.getLogger(this.getClass())
					.info("{} input values set(s). There are not enough tests for {} otherwise the patch would be \"true\" or \"false\"",
							dataSize, sourceLocation);
			return NO_PATCH;
		}
		// and it should be a viable patch, ie. fix the bug
		if (!constraintModelBuilder.isAViablePatch()) {
			LoggerFactory.getLogger(this.getClass()).info(
					"Changing only this statement does not solve the bug. {}",
					sourceLocation);
			return NO_PATCH;
		}
		ConstraintBasedSynthesis synthesis = new ConstraintBasedSynthesis();
		CodeGenesis genesis = synthesis.codesSynthesisedFrom(
                (Class<T>) (type.getType()), data);
		if (!genesis.isSuccessful()) {
			return NO_PATCH;
		}
		return new StringPatch(genesis.returnStatement(), sourceLocation, type);
	}

	@Override
	public SymbolicProcessor getSymbolicProcessor() {
		return this.processor;
	}

}
