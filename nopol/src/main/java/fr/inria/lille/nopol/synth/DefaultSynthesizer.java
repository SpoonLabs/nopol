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

import static fr.inria.lille.nopol.patch.Patch.NO_PATCH;
import static fr.inria.lille.nopol.synth.smt.model.Level.ARITHMETIC;
import static fr.inria.lille.nopol.synth.smt.model.Level.CONSTANTS;

import java.net.URL;

import org.slf4j.LoggerFactory;

import fr.inria.lille.nopol.SourceLocation;
import fr.inria.lille.nopol.patch.Patch;
import fr.inria.lille.nopol.patch.StringPatch;
import fr.inria.lille.nopol.synth.smt.constraint.ConstraintSolver;
import fr.inria.lille.nopol.synth.smt.model.InputModel;
import fr.inria.lille.nopol.synth.smt.model.InputModelBuilder;
import fr.inria.lille.nopol.synth.smt.model.Level;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class DefaultSynthesizer implements Synthesizer {

	private final SourceLocation sourceLocation;
	private final ConstraintModelBuilder constraintModelBuilder;
	private final BugKind type;

	public DefaultSynthesizer(final ConstraintModelBuilder constraintModelBuilder, final SourceLocation sourceLocation,
			final BugKind type) {
		this.constraintModelBuilder = constraintModelBuilder;
		this.sourceLocation = sourceLocation;
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.inria.lille.jefix.synth.Synthesizer#buildPatch(java.net.URL[], java.lang.String[])
	 */
	@Override
	public Patch buildPatch(final URL[] classpath, final String[] testClasses) {
		InputOutputValues data = constraintModelBuilder.buildFor(classpath, testClasses);

		// XXX FIXME TODO move this
		// there should be at least two sets of values, otherwise the patch would be "true" or "false"
		int dataSize = data.getOutputValues().size();
		if (dataSize < 2) {
			LoggerFactory.getLogger(this.getClass()).info("{} input values set(s). There are not enough tests for {}",
					dataSize, sourceLocation);
			return NO_PATCH;
		}

		// and it should be a viable patch, ie. fix the bug
		if (!constraintModelBuilder.isAViablePatch()) {
			LoggerFactory.getLogger(this.getClass()).info("Changing only this statement does not solve the bug. {}",
					sourceLocation);
			return NO_PATCH;
		}

		InputModelBuilder modelBuilder = new InputModelBuilder(data);
		Level level = CONSTANTS;
		InputModel model = modelBuilder.buildFor(level);
		ConstraintSolver constraintSolver = new ConstraintSolver();
		RepairCandidate newRepair = constraintSolver.solve(model);
		while (null == newRepair && level != ARITHMETIC) {
			level = level.next();
			model = modelBuilder.buildFor(level);
			newRepair = constraintSolver.solve(model);
		}
		if (null == newRepair) {
			return NO_PATCH;
		}
		return new StringPatch(newRepair.toString(), sourceLocation, type);
	}
}
