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

import fr.inria.lille.commons.spoon.SpoonedProject;
import fr.inria.lille.commons.synthesis.CodeGenesis;
import fr.inria.lille.commons.synthesis.ConstraintBasedSynthesis;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.localization.TestResult;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.patch.StringPatch;
import fr.inria.lille.repair.common.synth.RepairType;
import fr.inria.lille.repair.nopol.NopolResult;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.nopol.spoon.NopolProcessor;
import fr.inria.lille.repair.synthesis.collect.spoon.DefaultConstantCollector;
import org.slf4j.LoggerFactory;
import xxl.java.junit.TestCase;

import java.net.URL;
import java.util.*;

/**
 * @author Favio D. DeMarco
 */
public final class SMTNopolSynthesizer<T> implements Synthesizer {

	private final SourceLocation sourceLocation;
	private final InstrumentedProgram instrumentedProgram;
	private final RepairType type;
	public static int nbStatementsWithAngelicValue = 0;
	private static int dataSize = 0;
	private static int nbVariables;
	private final SpoonedProject spoonedProject;
	private NopolProcessor conditionalProcessor;
	private NopolContext nopolContext;//TODO remove this unused field

	public SMTNopolSynthesizer(SpoonedProject spoonedProject, InstrumentedProgram instrumentedProgram, SourceLocation sourceLocation, RepairType type, NopolProcessor processor, NopolContext nopolContext) {
		this.instrumentedProgram = instrumentedProgram;
		this.nopolContext = nopolContext;
		this.sourceLocation = sourceLocation;
		this.type = type;
		this.spoonedProject = spoonedProject;
		conditionalProcessor = processor;
	}

	/**
	 *
	 *
	 * @see Synthesizer#findAngelicValuesAndBuildPatch(URL[], List, Collection, long, NopolResult)
	 */
	@Override
	public List<Patch> findAngelicValuesAndBuildPatch(URL[] classpath, List<TestResult> testClasses, Collection<TestCase> failures, long maxTimeBuildPatch, NopolResult nopolResult) {

		final Collection<Specification<T>> data = instrumentedProgram.collectSpecifications(classpath, testClasses, failures);

		// XXX FIXME TODO move this
		// there should be at least two sets of values, otherwise the patch would be "true" or "false"
		int dataSize = data.size();
		if (dataSize < 2) {
			LoggerFactory.getLogger(this.getClass()).info("Not enough specifications: {}. A trivial patch is \"true\" or \"false\", please write new tests specifying {}.", dataSize, sourceLocation);
			// we return so that we can start working on the next statement in the suspicious list
			return Collections.EMPTY_LIST;
		}


		// and it should be a viable patch, ie. fix the bug
		if (!instrumentedProgram.isAViablePatch()) {
			LoggerFactory.getLogger(this.getClass()).info("Changing only this statement does not solve the bug. {}", sourceLocation);
			return Collections.EMPTY_LIST;
		}

		nopolResult.incrementNbAngelicValues(sourceLocation, conditionalProcessor);
		nbStatementsWithAngelicValue++;

		//collects available constants
		Map<String, Number> constants = new HashMap<>();
		DefaultConstantCollector constantCollector = new DefaultConstantCollector(constants);
		spoonedProject.forked(sourceLocation.getContainingClassName()).process(constantCollector);
		final ConstraintBasedSynthesis synthesis = new ConstraintBasedSynthesis(constants);
		final CodeGenesis genesis = synthesis.codesSynthesisedFrom((Class<T>) (type.getType()), data);

		if (genesis == null || !genesis.isSuccessful()) {
			return Collections.EMPTY_LIST;
		}
		SMTNopolSynthesizer.dataSize = dataSize;
		SMTNopolSynthesizer.nbVariables = data.iterator().next().inputs().keySet().size();
		return Collections.singletonList((Patch) new StringPatch(genesis.returnStatement(), sourceLocation, type));
	}

	public static int getNbStatementsWithAngelicValue() {
		return nbStatementsWithAngelicValue;
	}

	public static int getDataSize() {
		return dataSize;
	}

	public static int getNbVariables() {
		return nbVariables;
	}

	@Override
	public NopolProcessor getProcessor() {
		return conditionalProcessor;
	}


}
