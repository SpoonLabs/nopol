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

import fr.inria.lille.commons.spoon.SpoonedClass;
import fr.inria.lille.commons.spoon.SpoonedFile;
import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.commons.trace.RuntimeValues;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.nopol.spoon.ConditionalLoggingInstrumenter;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import fr.inria.lille.repair.nopol.synth.dynamoth.DynamothSynthesizer;

import java.io.File;

/**
 * @author Favio D. DeMarco
 */
public final class SynthesizerFactory {

	private static int nbStatementsAnalysed = 0;

	public static Synthesizer build(final File[] sourceFolders, final SpoonedProject spooner,
									NopolContext nopolContext, final SourceLocation statement,
									NopolProcessor nopolProcessor, SpoonedClass spoonCl) {
		nbStatementsAnalysed++;
		Class<?> type = nopolContext.getType().getType();
		switch (nopolContext.getSynthesis()) {
			case SMT:
				//InstrumentedProgram program = getInstrumentedProgramForThisLocation(nopolProcessor, sourceLocation, spooner);
				return new SMTNopolSynthesizer(spooner, getInstrumentedProgramForThisLocation(spooner, nopolContext, nopolProcessor, statement, spoonCl), statement, nopolProcessor.getRepairType(), nopolProcessor, nopolContext);
			case DYNAMOTH:
				return new DynamothSynthesizer(sourceFolders, statement, nopolProcessor.getRepairType(), nopolProcessor, spooner, nopolContext);
		}
		return Synthesizer.NO_OP_SYNTHESIZER;
	}

	public static int getNbStatementsAnalysed() {
		return nbStatementsAnalysed;
	}

	private static InstrumentedProgram getInstrumentedProgramForThisLocation(SpoonedProject spooner, NopolContext nopolContext, NopolProcessor nopolProcessor, SourceLocation statement, SpoonedFile spoonCl) {
		if (Boolean.class.equals(nopolContext.getType().getType())) {
			RuntimeValues<Boolean> runtimeValuesInstance = RuntimeValues.newInstance();
			switch (nopolContext.getOracle()) {
				case ANGELIC:
					return new ConstraintModelBuilder(runtimeValuesInstance, statement, new ConditionalLoggingInstrumenter(runtimeValuesInstance, nopolProcessor), spooner, nopolContext);
				case SYMBOLIC:
					return new JPFRunner<>(runtimeValuesInstance, statement, nopolProcessor, spoonCl, spooner, nopolContext);
			}
		}
		throw new UnsupportedOperationException();

		// OLD CODE, TO BE RESTORED
//		if (Integer.class.equals(type)) {
//			RuntimeValues<Integer> runtimeValuesInstance = RuntimeValues.newInstance();
//			constraintModelBuilder = new JPFRunner<>(runtimeValuesInstance, statement, nopolProcessor, spoonCl, spooner, nopolContext);
//			return new SMTNopolSynthesizer<>(spooner, constraintModelBuilder, statement, nopolContext.getType(), nopolProcessor, nopolContext);
//		}
//		if (Double.class.equals(type)) {
//			RuntimeValues<Double> runtimeValuesInstance = RuntimeValues.newInstance();
//			constraintModelBuilder = new JPFRunner<>(runtimeValuesInstance, statement, nopolProcessor, spoonCl, spooner, nopolContext);
//			return new SMTNopolSynthesizer<>(spooner, constraintModelBuilder, statement, nopolContext.getType(), nopolProcessor, nopolContext);
//		}

	}

}
