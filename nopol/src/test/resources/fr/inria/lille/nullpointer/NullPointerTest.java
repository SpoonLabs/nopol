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
package fr.inria.lille.examples.nullpointer;

import static java.lang.Integer.MIN_VALUE;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Favio D. DeMarco
 * 
 */
public class NullPointerTest {

	private NullPointer nullPointer;

	/**
	 * Test method for {@link fr.inria.lille.examples.nullpointer.NullPointer#toStringLength(java.lang.Object)}.
	 */
	@Test
	public final void null_should_return_Integer_MIN_VALUE() {

		assertEquals(MIN_VALUE, this.nullPointer.toStringLength(null));
	}

	/**
	 * Test method for {@link fr.inria.lille.examples.nullpointer.NullPointer#toStringLength(java.lang.Object)}.
	 */
	@Test
	public final void should_return_toString_length() {

		Object object = new Object();

		assertEquals(object.toString().length(), this.nullPointer.toStringLength(object));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() {
		this.nullPointer = new NullPointer();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() {
		this.nullPointer = null;
	}
}
