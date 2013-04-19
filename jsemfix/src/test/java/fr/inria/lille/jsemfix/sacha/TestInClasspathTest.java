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
package fr.inria.lille.jsemfix.sacha;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import sacha.finder.main.TestInClasspath;

/**
 * @author Favio D. DeMarco
 * 
 */
public class TestInClasspathTest {

	/**
	 * Test method for {@link sacha.finder.main.TestInClasspath#find()}.
	 */
	@Test
	public void find_results_should_include_this_test() {
		// GIVEN
		// WHEN
		Class<?>[] testClasses = new TestInClasspath().find();

		// THEN
		assertTrue("testClasses should contain this class.", Arrays.asList(testClasses).contains(getClass()));
	}
}
