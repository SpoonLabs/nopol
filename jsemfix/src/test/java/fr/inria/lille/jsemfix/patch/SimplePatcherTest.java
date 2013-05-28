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

import static fr.inria.lille.jsemfix.patch.Patch.NO_PATCH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.Collections;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.lille.jsemfix.constraint.RepairConstraint;
import fr.inria.lille.jsemfix.constraint.RepairConstraintBuilder;
import fr.inria.lille.jsemfix.patch.spoon.SpoonPatcher;
import fr.inria.lille.jsemfix.sps.SuspiciousStatement;

/**
 * @author Favio D. DeMarco
 * 
 */
public class SimplePatcherTest {

	private static final class ToStringConstraintBuilder implements RepairConstraintBuilder<Void> {

		final String source;

		ToStringConstraintBuilder(final String source) {
			this.source = source;
		}

		@Override
		public RepairConstraint<Void> buildFor(final SuspiciousStatement rootCause,
				final Set<fr.inria.lille.jsemfix.test.Test> s) {
			return new RepairConstraint<Void>() {

				/**
				 * @see java.lang.Object#toString()
				 */
				@Override
				public String toString() {
					return ToStringConstraintBuilder.this.source;
				}

				@Override
				public Void getValue() {
					// TODO Auto-generated method stub
					// return null;
					throw new UnsupportedOperationException("RepairConstraint<Void>.getValue");
				}
			};
		}
	}

	/**
	 * Test method for {@link fr.inria.lille.jsemfix.patch.spoon.SpoonPatcher#createPatch(java.util.Set)}.
	 */
	private void testCreatePatch(final fr.inria.lille.jsemfix.test.Test test, final String source) {

		// GIVEN
		SuspiciousStatement rc = new SuspiciousIfStatement();
		Set<fr.inria.lille.jsemfix.test.Test> tests = Collections.singleton(test);
		RepairConstraintBuilder<Void> rcb = new ToStringConstraintBuilder(source);

		// WHEN
		Patch patch = new SpoonPatcher(rc, rcb).createPatch(tests);

		// THEN
		assertNotSame(NO_PATCH, patch);
		assertEquals(rc.getLineNumber(), patch.getLineNumber());
		assertEquals(source, patch.asString());
		assertEquals(rc.getContainingClass().getSimpleName() + ".java", patch.getFile().getName());
	}

	/**
	 * Test method for {@link fr.inria.lille.jsemfix.patch.spoon.SpoonPatcher#createPatch(java.util.Set)}.
	 */
	@Test
	@Ignore
	public final void testCreatePatch_constant_false() {

		this.testCreatePatch(FailingIfTest.shouldBeFalse(), "if(false) {");
	}

	/**
	 * Test method for {@link fr.inria.lille.jsemfix.patch.spoon.SpoonPatcher#createPatch(java.util.Set)}.
	 */
	@Test
	@Ignore
	public final void testCreatePatch_constant_true() {

		this.testCreatePatch(FailingIfTest.shouldBeTrue(), "if(true) {");
	}
}
