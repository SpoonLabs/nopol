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
package fr.inria.lille.jefix.synth.expression;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class TernaryExpression implements Expression {

	private final Expression leftExpression;
	private final String leftOperator;
	private final Expression centerExpression;
	private final String rightOperator;
	private final Expression rightExpression;

	/**
	 * @param leftExpression
	 * @param leftOperator
	 * @param centerExpression
	 * @param rightOperator
	 * @param rightExpression
	 */
	public TernaryExpression(final Expression leftExpression, final String leftOperator,
			final Expression centerExpression, final String rightOperator, final Expression rightExpression) {
		this.leftExpression = checkNotNull(leftExpression);
		this.leftOperator = checkNotNull(leftOperator);
		this.centerExpression = checkNotNull(centerExpression);
		this.rightOperator = checkNotNull(rightOperator);
		this.rightExpression = checkNotNull(rightExpression);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.leftExpression + this.leftOperator + this.centerExpression + this.rightOperator
				+ this.rightExpression;
	}
}
