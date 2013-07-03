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

import java.util.List;

import org.smtlib.IExpr;
import org.smtlib.IExpr.IFactory;

/**
 * @author Favio D. DeMarco
 * 
 */
final class Simplifier {

	private final IFactory factory;

	/**
	 * @param factory
	 */
	Simplifier(final IFactory factory) {
		this.factory = factory;
	}

	IExpr simplify(final List<IExpr> constraints) {
		if (constraints.isEmpty()) {
			return this.factory.symbol("true");
		} else if (constraints.size() == 1) {
			return constraints.get(0);
		} else {
			return this.factory.fcn(this.factory.symbol("and"), constraints);
		}
	}
}
