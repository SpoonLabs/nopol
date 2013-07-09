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
package fr.inria.lille.jsemfix.synth.model;

import static java.util.Arrays.asList;

import java.util.Collections;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class Components {

	public static final Component NOT = createFunction("not", Type.BOOLEAN, Type.BOOLEAN);

	public static final Component AND = createFunction("and", Type.BOOLEAN, Type.BOOLEAN, Type.BOOLEAN);
	public static final Component OR = createFunction("or", Type.BOOLEAN, Type.BOOLEAN, Type.BOOLEAN);

	public static final Component ITE = createFunction("ite", Type.INTEGER, Type.BOOLEAN, Type.INTEGER, Type.INTEGER);

	public static final Component DISTINCT = createFunction("distinct", Type.BOOLEAN, Type.INTEGER, Type.INTEGER);
	public static final Component EQUALS = createFunction("=", Type.BOOLEAN, Type.INTEGER, Type.INTEGER);
	public static final Component LESS_OR_EQUAL_THAN = createFunction("<=", Type.BOOLEAN, Type.INTEGER, Type.INTEGER);
	public static final Component LESS_THAN = createFunction("<", Type.BOOLEAN, Type.INTEGER, Type.INTEGER);

	public static Component createFunction(final String name, final Type outputType, final Type parameter) {
		return new NAryFunction(name, Collections.singletonList(parameter), outputType);
	}

	public static Component createFunction(final String name, final Type outputType, final Type... parameters) {
		return new NAryFunction(name, asList(parameters), outputType);
	}

	private Components() {}
}
