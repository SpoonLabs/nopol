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
package fr.inria.lille.nopol.synth.expression;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Favio D. DeMarco
 *
 */
public final class SimpleExpression implements Expression {

	private final String value;

	/**
	 * @param value
	 */
	public SimpleExpression(final String value) {
		this.value = checkNotNull(value);
	}

	@Override
	public String asGuardedString() {
		return "("+ this.asString()+")";
	}

	@Override
	public String asString() {
		return this.value;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.asString();
	}
}
