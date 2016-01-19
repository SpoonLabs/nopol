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
package fr.inria.lille.examples.bool.constant;

import static fr.inria.lille.examples.bool.constant.Neg.neg;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Favio D. DeMarco
 *
 */
public class NegTest {

	/**
	 * Test method for {@link fr.inria.lille.examples.bool.constant.Neg#neg(int)}.
	 */
	@Test
	public final void a_positive_value_should_be_returned_negative() {

		int value = neg(1);

		assertEquals(-1, value);
	}

	/**
	 * Test method for {@link fr.inria.lille.examples.bool.constant.Neg#neg(int)}.
	 */
	@Test
	public final void a_negative_value_should_be_returned_positive() {

		int value = neg(-1);

		assertEquals(1, value);
	}

	/**
	 * Test method for {@link fr.inria.lille.examples.bool.constant.Neg#neg(int)}.
	 */
	@Test
	public final void zero_should_be_zero() {

		int value = neg(0);

		assertEquals(0, value);
	}
}
