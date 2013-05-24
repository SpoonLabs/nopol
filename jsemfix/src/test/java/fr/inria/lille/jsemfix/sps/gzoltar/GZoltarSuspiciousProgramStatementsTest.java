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
package fr.inria.lille.jsemfix.sps.gzoltar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.junit.Test;

import fr.inria.lille.jsemfix.gzoltar.Objects;
import fr.inria.lille.jsemfix.gzoltar.ObjectsTest;
import fr.inria.lille.jsemfix.sps.SuspiciousStatement;
import fr.inria.lille.jsemfix.sps.SuspiciousProgramStatements;

/**
 * @author Favio D. DeMarco
 * 
 */
public class GZoltarSuspiciousProgramStatementsTest {

	@Test
	public void sortBySusiciousness_returns_the_statements_in_the_correct_order() {

		// GIVEN
		SuspiciousProgramStatements sps = GZoltarSuspiciousProgramStatements
				.createWithPackageAndTestClasses(Objects.class.getPackage(), ObjectsTest.class);

		// WHEN
		List<SuspiciousStatement> statements = sps.sortBySuspiciousness();

		// THEN
		assertEquals(4, statements.size());
		for (int index = 0; index < 3; index++) {
			SuspiciousStatement s = statements.get(index);
			int lineNumber = 52 + index;
			assertEquals(lineNumber, s.getLineNumber());
			assertEquals(1D, s.getSuspiciousness(), 0D);
			assertSame(Objects.class, s.getContainingClass());
		}
		SuspiciousStatement s = statements.get(3);
		assertEquals(75, s.getLineNumber());
		assertEquals(0D, s.getSuspiciousness(), 0D);
		assertSame(Objects.class, s.getContainingClass());
	}
}
