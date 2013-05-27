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



/**
 * @author Favio D. DeMarco
 *
 */
public interface Program {

	static final Program DEFAULT = new Program() {

		/**
		 * XXX FIXME TODO it must not use reflection
		 * 
		 * @see fr.inria.lille.jsemfix.Program#executeInContext(java.lang.Class, java.lang.String, java.lang.Object)
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
	};

	/**
	 * XXX FIXME TODO It's coupled to class loader context kind
	 */
	Object executeInContext(Class<?> class1, String method, Object parameter);
}
