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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.List;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class TernaryExpression implements CompositeExpression {

	private Expression centerExpression;
	private Expression leftExpression;
	private final String leftOperator;
	private Expression rightExpression;
	private final String rightOperator;

	/**
	 * @param leftExpression
	 * @param leftOperator
	 * @param centerExpression
	 * @param rightOperator
	 * @param rightExpression
	 */
	public TernaryExpression(final String leftOperator, final String rightOperator) {
		this.leftOperator = checkNotNull(leftOperator);
		this.rightOperator = checkNotNull(rightOperator);
	}

	@Override
	public String asGuardedString() {
		return '(' + this.asString() + ')';
	}

	@Override
	public String asString() {
		return this.leftExpression + this.leftOperator + this.centerExpression + this.rightOperator
				+ this.rightExpression;
	}

	@Override
	public CompositeExpression setSubExpressions(final List<Expression> subExpressions) {
		Iterator<Expression> expressions = subExpressions.iterator();
		this.leftExpression = checkNotNull(expressions.next());
		this.centerExpression = checkNotNull(expressions.next());
		this.rightExpression = checkNotNull(expressions.next());
		checkArgument(!expressions.hasNext(), "More than 3 subexpressions.");
		return this;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.asString();
	}
}
