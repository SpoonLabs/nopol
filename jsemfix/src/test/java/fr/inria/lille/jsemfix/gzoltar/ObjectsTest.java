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
package fr.inria.lille.jsemfix.gzoltar;

import junit.framework.TestCase;

/**
 * Tests for {@link Objects}.
 * 
 * @author Favio D. DeMarco
 */
public class ObjectsTest extends TestCase {

	public void testEqual() throws Exception {
		assertTrue(Objects.equal(1, 1));
		assertTrue(Objects.equal(null, null));

		// test distinct string objects
		String s1 = "foobar";
		String s2 = new String(s1);
		assertTrue(Objects.equal(s1, s2));

		assertFalse(Objects.equal("foo", "bar"));
		assertFalse(Objects.equal("1", 1));
		assertFalse(Objects.equal(s1, null));
		assertFalse(Objects.equal(null, s1));
	}

	public void testHashCode() throws Exception {
		int h1 = Objects.hashCode(1, "two", 3.0);
		int h2 = Objects.hashCode(new Integer(1), new String("two"), new Double(3.0));
		// repeatable
		assertEquals(h1, h2);

		// These don't strictly need to be true, but they're nice properties.
		assertTrue(Objects.hashCode(1, 2, null) != Objects.hashCode(1, 2));
		assertTrue(Objects.hashCode(1, 2, null) != Objects.hashCode(1, null, 2));
		assertTrue(Objects.hashCode(1, null, 2) != Objects.hashCode(1, 2));
		assertTrue(Objects.hashCode(1, 2, 3) != Objects.hashCode(3, 2, 1));
		assertTrue(Objects.hashCode(1, 2, 3) != Objects.hashCode(2, 3, 1));
	}
}
