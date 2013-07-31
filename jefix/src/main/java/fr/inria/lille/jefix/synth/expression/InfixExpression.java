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
public final class InfixExpression implements CompositeExpression {

	private Expression leftExpression;
	private final String operator;
	private Expression rightExpression;

	/**
	 * @param leftExpression
	 * @param operator
	 * @param rightExpression
	 */
	public InfixExpression(final String operator) {
		this.operator = checkNotNull(operator);
	}

	@Override
	public String asGuardedString() {
		return '(' + this.asString() + ')';
	}

	@Override
	public String asString() {
		return this.leftExpression.asGuardedString() + this.operator + this.rightExpression.asGuardedString();
	}

	@Override
	public CompositeExpression setSubExpressions(final List<Expression> subExpressions) {
		Iterator<Expression> expressions = subExpressions.iterator();
		this.leftExpression = checkNotNull(expressions.next());
		this.rightExpression = checkNotNull(expressions.next());
		checkArgument(!expressions.hasNext(), "More than two subexpressions.");
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
