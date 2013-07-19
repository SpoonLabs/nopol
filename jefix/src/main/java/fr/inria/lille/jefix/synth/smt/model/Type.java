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
package fr.inria.lille.jefix.synth.smt.model;

import com.google.common.base.Function;

/**
 * @author Favio D. DeMarco
 * 
 */
public enum Type {

	BOOLEAN, INTEGER;

	enum ValueToType implements Function<Object, Type> {
		INSTANCE;
		@Override
		public Type apply(final Object value) {
			if (value instanceof Boolean) {
				return BOOLEAN;
			} else if (value instanceof Long || value instanceof Integer || value instanceof Short
					|| value instanceof Byte) {
				return INTEGER;
			}
			throw new IllegalStateException("Can't find a sort for class " + value.getClass());
		}
	}
}
