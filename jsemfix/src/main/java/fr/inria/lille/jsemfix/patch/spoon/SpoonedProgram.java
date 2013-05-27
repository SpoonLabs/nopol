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
package fr.inria.lille.jsemfix.patch.spoon;

import spoon.SpoonClassLoader;
import fr.inria.lille.jsemfix.Program;

/**
 * @author Favio D. DeMarco
 *
 */
class SpoonedProgram implements Program {

	private final SpoonClassLoader classLoader;

	SpoonedProgram(final SpoonClassLoader ccl) {
		this.classLoader = ccl;
	}

	/**
	 * XXX FIXME TODO fat interface
	 * 
	 * @see fr.inria.lille.jsemfix.Program#executeInContext(java.lang.Class, java.lang.String, java.lang.Object)
	 */
	@Override
	public Object executeInContext(final Class<?> class1, final String method, final Object parameter) {
		try {
			// XXX FIXME TODO law of Demeter
			return this.classLoader.loadClass(class1.getName()).getMethod(method, parameter.getClass())
					.invoke(null, parameter);
		} catch (ReflectiveOperationException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
}
