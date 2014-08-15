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

import static fr.inria.lille.repair.nopol.patch.Patch.NO_PATCH;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.slf4j.LoggerFactory;

import fr.inria.lille.commons.synthesis.CodeGenesis;
import fr.inria.lille.commons.synthesis.ConstraintBasedSynthesis;
import fr.inria.lille.commons.trace.Specification;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.nopol.patch.Patch;
import fr.inria.lille.repair.nopol.patch.StringPatch;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class DefaultSynthesizer implements Synthesizer {

	private final SourceLocation sourceLocation;
	private final ConstraintModelBuilder constraintModelBuilder;
	private final BugKind type;
	private static int nbStatementsWithAngelicValue = 0;

	public DefaultSynthesizer(final ConstraintModelBuilder constraintModelBuilder, final SourceLocation sourceLocation,
			final BugKind type, final File outputFolder) {
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
		Collection<Specification<Boolean>> data = constraintModelBuilder.buildFor(classpath, testClasses);

		// XXX FIXME TODO move this
		// there should be at least two sets of values, otherwise the patch would be "true" or "false"
		int dataSize = data.size();
		if (dataSize < 2) {
			LoggerFactory.getLogger(this.getClass()).info("{} input values set(s). There are not enough tests for {} otherwise the patch would be \"true\" or \"false\"",
					dataSize, sourceLocation);
			return NO_PATCH;
		}

		// and it should be a viable patch, ie. fix the bug
		if (!constraintModelBuilder.isAViablePatch()) {
			LoggerFactory.getLogger(this.getClass()).info("Changing only this statement does not solve the bug. {}", sourceLocation);
			return NO_PATCH;
		}
		nbStatementsWithAngelicValue++;
		ConstraintBasedSynthesis synthesis = new ConstraintBasedSynthesis();
		CodeGenesis genesis = synthesis.codesSynthesisedFrom(Boolean.class, data);
		if (! genesis.isSuccessful()) {
			return NO_PATCH;
		}
		return new StringPatch(genesis.returnStatement(), sourceLocation, type);
	}
	
	public static int getNbStatementsWithAngelicValue(){
		return nbStatementsWithAngelicValue;
	}
}
