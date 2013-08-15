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
package fr.inria.lille.jefix.synth.conditional;

import static fr.inria.lille.jefix.patch.Level.CONSTANTS;
import static fr.inria.lille.jefix.patch.Level.MULTIPLICATION;
import static fr.inria.lille.jefix.patch.Patch.NO_PATCH;

import java.io.File;
import java.net.URL;

import org.slf4j.LoggerFactory;

import fr.inria.lille.jefix.SourceLocation;
import fr.inria.lille.jefix.patch.Level;
import fr.inria.lille.jefix.patch.Patch;
import fr.inria.lille.jefix.patch.StringPatch;
import fr.inria.lille.jefix.synth.InputOutputValues;
import fr.inria.lille.jefix.synth.RepairCandidate;
import fr.inria.lille.jefix.synth.Synthetizer;
import fr.inria.lille.jefix.synth.smt.constraint.ConstraintSolver;
import fr.inria.lille.jefix.synth.smt.model.InputModel;
import fr.inria.lille.jefix.synth.smt.model.InputModelBuilder;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class ConditionalSynthetizer implements Synthetizer {

	private final File sourceFolder;
	private final SourceLocation sourceLocation;

	public ConditionalSynthetizer(final File sourceFolder, final SourceLocation sourceLocation) {
		this.sourceFolder = sourceFolder;
		this.sourceLocation = sourceLocation;
	}

	/**
	 * @see fr.inria.lille.jefix.synth.Synthetizer#buildPatch(java.net.URL[], java.lang.String[])
	 */
	@Override
	public Patch buildPatch(final URL[] classpath, final String[] testClasses) {

		InputOutputValues data = new InputOutputValues();

		// XXX FIXME TODO wtf!?
		data = new ConditionalsConstraintModelBuilder(this.sourceFolder, this.sourceLocation, true).buildFor(classpath,
				testClasses, data);
		data = new ConditionalsConstraintModelBuilder(this.sourceFolder, this.sourceLocation, false).buildFor(
				classpath, testClasses, data);

		// XXX FIXME TODO move this
		// there should be at least one output value, this is weird...
		if (data.getOutputValues().size() < 2) {
			LoggerFactory.getLogger(this.getClass()).info("No model for {}", this.sourceLocation);
			return NO_PATCH;
		}

		InputModelBuilder modelBuilder = new InputModelBuilder(data);
		Level level = CONSTANTS;
		InputModel model = modelBuilder.buildFor(level);
		ConstraintSolver constraintSolver = new ConstraintSolver();
		RepairCandidate newRepair = constraintSolver.solve(model);
		while (null == newRepair && level != MULTIPLICATION) {
			level = level.next();
			model = modelBuilder.buildFor(level);
			newRepair = constraintSolver.solve(model);
		}
		if (null == newRepair) {
			return NO_PATCH;
		}
		return new StringPatch(newRepair.toString(), this.sourceLocation);
	}
}
