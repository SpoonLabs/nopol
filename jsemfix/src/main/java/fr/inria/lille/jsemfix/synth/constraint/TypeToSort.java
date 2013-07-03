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
package fr.inria.lille.jsemfix.synth.constraint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.smtlib.ISort;
import org.smtlib.ISort.IFactory;

import com.google.common.base.Function;

import fr.inria.lille.jsemfix.synth.model.Type;

/**
 * @author Favio D. DeMarco
 */
final class TypeToSort implements Function<Type, ISort> {

	private final ISort boolSort;
	private final ISort intSort;

	/**
	 * @param sortFactory
	 */
	TypeToSort(@Nonnull final IFactory sortFactory, @Nonnull final org.smtlib.IExpr.IFactory eFactory) {
		this.boolSort = sortFactory.Bool();
		this.intSort = sortFactory.createSortExpression(eFactory.symbol("Int"));
	}

	@Override
	@Nullable
	public ISort apply(@Nullable final Type input) {
		switch (input) {
		case BOOLEAN:
			return this.boolSort;
		case INTEGER:
			return this.intSort;

		default:
			throw new IllegalArgumentException("Unknown type " + input);
		}
	}
}
