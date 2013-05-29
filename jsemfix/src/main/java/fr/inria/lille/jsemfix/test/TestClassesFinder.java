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
package fr.inria.lille.jsemfix.test;

import java.util.Arrays;
import java.util.Collection;

import sacha.finder.main.TestInClasspath;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class TestClassesFinder {

	private static final class SamePackage implements Predicate<Class<?>> {

		final Package rootPackage;

		/**
		 * @param rootPackage
		 */
		SamePackage(final Package rootPackage) {
			this.rootPackage = rootPackage;
		}

		@Override
		public boolean apply(final Class<?> input) {
			return this.rootPackage.equals(input.getPackage());
		}
	}

	private final Package rootPackage;

	/**
	 * @param rootPackage
	 */
	public TestClassesFinder(final Package rootPackage) {
		this.rootPackage = rootPackage;
	}

	public Collection<Class<?>> find() {
		return Collections2.filter(Arrays.asList(new TestInClasspath().find()), new SamePackage(this.rootPackage));
	}
}
