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
package fr.inria.lille.jsemfix.constraint;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Iterators;

import fr.inria.lille.jsemfix.sps.SuspiciousStatement;
import fr.inria.lille.jsemfix.test.Test;

/**
 * @author Favio D. DeMarco
 *
 */
public final class BooleanRepairConstraintBuilder implements RepairConstraintBuilder<Boolean> {

	@SuppressWarnings("unchecked")
	private final Iterator<RepairConstraint<Boolean>> constraints = Iterators.<RepairConstraint<Boolean>> forArray(
			new ConstantRepairConstraint<Boolean>(true), new ConstantRepairConstraint<Boolean>(false));

	/**
	 * @see fr.inria.lille.jsemfix.constraint.RepairConstraintBuilder#buildFor(fr.inria.lille.jsemfix.sps.SuspiciousStatement, java.util.Set)
	 */
	@Override
	public RepairConstraint<Boolean> buildFor(final SuspiciousStatement rootCause, final Set<Test> s) {

		RepairConstraint<Boolean> constraint = this.constraints.next();

		return constraint;
	}
}
