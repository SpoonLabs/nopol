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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.gzoltar.core.GZoltar;
import com.gzoltar.core.components.Statement;

/**
 * @author Favio D. DeMarco
 * 
 */
public class GzoltarTest {

	/**
	 * Test method for {@link com.gzoltar.core.GZoltar#run()}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void should_find_the_bug() throws IOException {

		// GIVEN
		GZoltar loc = new GZoltar("/");
		loc.addClassToInstrument(Objects.class.getName());
		loc.addTestToExecute(ObjectsTest.class.getName());

		// WHEN
		loc.run();

		// THEN
		List<Statement> suspiciousStatements = loc.getSuspiciousStatements();
		assertEquals(4, suspiciousStatements.size());
		for (int index = 0; index < 3; index++) {
			Statement s = suspiciousStatements.get(index);
			int lineNumber = 52 + index;
			assertEquals(lineNumber, s.getLineNumber());
			assertEquals("fr.inria.lille.jsemfix.gzoltar.Objects{equal(Ljava/lang/Object;Ljava/lang/Object;)Z["
					+ lineNumber, s.getLabel());
			assertEquals(1D, s.getSuspiciousness(), 0D);
		}
		Statement s = suspiciousStatements.get(3);
		assertEquals("fr.inria.lille.jsemfix.gzoltar.Objects{hashCode([Ljava/lang/Object;)I[75", s.getLabel());
		assertEquals(75, s.getLineNumber());
		assertEquals(0D, s.getSuspiciousness(), 0D);
	}
}
