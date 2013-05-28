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

import static com.google.common.base.Preconditions.checkNotNull;
import static fr.inria.lille.jsemfix.patch.Patch.NO_PATCH;

import java.util.Set;

import fr.inria.lille.jsemfix.constraint.RepairConstraint;
import fr.inria.lille.jsemfix.constraint.RepairConstraintBuilder;
import fr.inria.lille.jsemfix.patch.Level;
import fr.inria.lille.jsemfix.patch.Patch;
import fr.inria.lille.jsemfix.patch.Patcher;
import fr.inria.lille.jsemfix.sps.SuspiciousStatement;
import fr.inria.lille.jsemfix.test.Test;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class SpoonPatcher implements Patcher {

	private final RepairConstraintBuilder<?> repairConstraintBuilder;

	private final SuspiciousStatement rootCause;

	public SpoonPatcher(final SuspiciousStatement rc, final RepairConstraintBuilder<?> repairConstraintBuilder) {
		this.rootCause = checkNotNull(rc);
		this.repairConstraintBuilder = checkNotNull(repairConstraintBuilder);
	}

	/**
	 * @see fr.inria.lille.jsemfix.patch.Patcher#createPatch(java.util.Set)
	 */
	@Override
	public Patch createPatch(final Set<Test> s) {
		Patch newRepair;
		RepairConstraint<?> c = this.generateRepairConstraint(s);
		Level level = Level.CONSTANTS;
		do {
			newRepair = this.synthesize(c, level);
			level = level.next();
		} while (newRepair == NO_PATCH && level != Level.LOGIC_COMPARISON_ITERATION);
		return newRepair;
	}

	private RepairConstraint<?> generateRepairConstraint(final Set<Test> s) {
		return this.repairConstraintBuilder.buildFor(this.rootCause, s);
	}

	private Patch synthesize(final RepairConstraint<?> c, final Level level) {
		return new SpoonPatch();
	}
}
