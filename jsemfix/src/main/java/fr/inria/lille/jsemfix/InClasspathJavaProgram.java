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
package fr.inria.lille.jsemfix;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class InClasspathJavaProgram implements JavaProgram {

	private final Package rootPackage;

	/**
	 * @param rootPackage
	 */
	public InClasspathJavaProgram(final Package rootPackage) {
		this.rootPackage = checkNotNull(rootPackage);
	}

	/**
	 * XXX FIXME TODO it must not use reflection
	 * 
	 * @see fr.inria.lille.jsemfix.JavaProgram#executeInContext(java.lang.Class, java.lang.String, java.lang.Object)
	 */
	@Override
	public Object executeInContext(final Class<?> class1, final String method, final Object parameter) {
		try {
			return class1.getMethod(method, parameter.getClass()).invoke(null, parameter);
		} catch (ReflectiveOperationException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}

	@Override
	public Package getRootPackage() {
		return this.rootPackage;
	}
}
