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
package fr.inria.lille.nopol.synth.smt.model;

import static java.util.Arrays.asList;

import java.util.Collections;

import fr.inria.lille.nopol.synth.expression.CompositeExpression;
import fr.inria.lille.nopol.synth.expression.InfixExpression;
import fr.inria.lille.nopol.synth.expression.TernaryExpression;
import fr.inria.lille.nopol.synth.expression.UnaryExpression;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class Components {

	public static final Component NOT = createFunction("not", new UnaryExpression("!"), Type.BOOLEAN, Type.BOOLEAN);

	public static final Component AND = createFunction("and", new InfixExpression("&&"), Type.BOOLEAN, Type.BOOLEAN,
			Type.BOOLEAN);
	public static final Component OR = createFunction("or", new InfixExpression("||"), Type.BOOLEAN, Type.BOOLEAN,
			Type.BOOLEAN);

	public static final Component ITE = createFunction("ite", new TernaryExpression("?", ":"), Type.NUMBER,
			Type.BOOLEAN, Type.NUMBER, Type.NUMBER);

	public static final Component DISTINCT = createFunction("distinct", new InfixExpression("!="), Type.BOOLEAN,
			Type.NUMBER, Type.NUMBER);
	public static final Component EQUALS = createFunction("=", new InfixExpression("=="), Type.BOOLEAN, Type.NUMBER,
			Type.NUMBER);
	public static final Component LESS_OR_EQUAL_THAN = createFunction("<=", new InfixExpression("<="), Type.BOOLEAN,
			Type.NUMBER, Type.NUMBER);
	public static final Component LESS_THAN = createFunction("<", new InfixExpression("<"), Type.BOOLEAN, Type.NUMBER,
			Type.NUMBER);

	public static final Component PLUS = createFunction("+", new InfixExpression("+"), Type.NUMBER, Type.NUMBER,
			Type.NUMBER);
	public static final Component MINUS = createFunction("-", new InfixExpression("-"), Type.NUMBER, Type.NUMBER,
			Type.NUMBER);

	public static final Component MULTIPLICATION = createFunction("*", new InfixExpression("*"), Type.NUMBER,
			Type.NUMBER, Type.NUMBER);

	public static Component createFunction(final String name, final CompositeExpression expression,
			final Type outputType, final Type parameter) {
		return new NAryFunction(name, expression, Collections.singletonList(parameter), outputType);
	}

	public static Component createFunction(final String name, final CompositeExpression expression,
			final Type outputType, final Type... parameters) {
		return new NAryFunction(name, expression, asList(parameters), outputType);
	}

	private Components() {}
}
