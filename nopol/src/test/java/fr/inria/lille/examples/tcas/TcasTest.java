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
package fr.inria.lille.examples.tcas;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Favio D. DeMarco
 * 
 */
public class TcasTest {

	/**
	 * See <a href="http://www.comp.nus.edu.sg/~abhik/pdf/ICSE13-SEMFIX.pdf">SemFix: Program Repair via Semantic
	 * Analysis</a> table I at page 772.
	 * 
	 * Test method for {@link fr.inria.lille.examples.tcas.Tcas#is_upward_preferred(boolean, int, int)}.
	 */
	@Test
	public void _1() {

		// GIVEN
		// WHEN
		int actual = new Tcas().is_upward_preferred(1, 0, 100);

		// THEN
		assertEquals(0, actual);
	}

	/**
	 * See <a href="http://www.comp.nus.edu.sg/~abhik/pdf/ICSE13-SEMFIX.pdf">SemFix: Program Repair via Semantic
	 * Analysis</a> table I at page 772.
	 * 
	 * Test method for {@link fr.inria.lille.examples.tcas.Tcas#is_upward_preferred(boolean, int, int)}.
	 */
	@Test
	public void _2() {

		// GIVEN
		// WHEN
		int actual = new Tcas().is_upward_preferred(1, 11, 110);

		// THEN
		assertEquals(1, actual);
	}

	/**
	 * See <a href="http://www.comp.nus.edu.sg/~abhik/pdf/ICSE13-SEMFIX.pdf">SemFix: Program Repair via Semantic
	 * Analysis</a> table I at page 772.
	 * 
	 * Test method for {@link fr.inria.lille.examples.tcas.Tcas#is_upward_preferred(boolean, int, int)}.
	 */
	@Test
	public void _3() {

		// GIVEN
		// WHEN
		int actual = new Tcas().is_upward_preferred(0, 100, 50);

		// THEN
		assertEquals(1, actual);
	}

	/**
	 * See <a href="http://www.comp.nus.edu.sg/~abhik/pdf/ICSE13-SEMFIX.pdf">SemFix: Program Repair via Semantic
	 * Analysis</a> table I at page 772.
	 * 
	 * Test method for {@link fr.inria.lille.examples.tcas.Tcas#is_upward_preferred(boolean, int, int)}.
	 */
	@Test
	public void _4() {

		// GIVEN
		// WHEN
		int actual = new Tcas().is_upward_preferred(1, -20, 60);

		// THEN
		assertEquals(1, actual);
	}

	/**
	 * See <a href="http://www.comp.nus.edu.sg/~abhik/pdf/ICSE13-SEMFIX.pdf">SemFix: Program Repair via Semantic
	 * Analysis</a> table I at page 772.
	 * 
	 * Test method for {@link fr.inria.lille.examples.tcas.Tcas#is_upward_preferred(boolean, int, int)}.
	 */
	@Test
	public void _5() {

		// GIVEN
		// WHEN
		int actual = new Tcas().is_upward_preferred(0, 0, 10);

		// THEN
		assertEquals(0, actual);
	}

	@Test
	@Ignore("Added to get a response != '0 != up_sep'")
	public void _6() {

		// GIVEN
		// WHEN
		int actual = new Tcas().is_upward_preferred(1, 0, 99);

		// THEN
		assertEquals(1, actual);
	}
}
