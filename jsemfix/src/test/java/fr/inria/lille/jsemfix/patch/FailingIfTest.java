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
package fr.inria.lille.jsemfix.patch;

import fr.inria.lille.jsemfix.test.Test;

/**
 * @author Favio D. DeMarco
 *
 */
final class FailingIfTest implements Test {

	static Test shouldBeFalse() {
		return new FailingIfTest();
	}

	static Test shouldBeTrue() {
		return new FailingIfTest();
	}

	/**
	 * 
	 */
	private FailingIfTest() {}

	/**
	 * @see fr.inria.lille.jsemfix.test.Test#getClassName()
	 */
	@Override
	public String getClassName() {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("FailingIfTest.getClassName");
	}

	/**
	 * @see fr.inria.lille.jsemfix.test.Test#getMethodName()
	 */
	@Override
	public String getMethodName() {
		// TODO Auto-generated method stub
		// return null;
		throw new UnsupportedOperationException("FailingIfTest.getMethodName");
	}
}
