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
package fr.inria.lille.jsemfix.conditional;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import sacha.finder.classes.impl.ClassloaderFinder;
import sacha.finder.filters.impl.TestFilter;
import sacha.finder.processor.Processor;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import fr.inria.lille.jsemfix.functors.ClassName;

/**
 * @author Favio D. DeMarco
 * 
 */
final class TestClassesFinder implements Callable<String[]> {

	private static final class SamePackage implements Predicate<Class<?>> {

		final String rootPackage;

		/**
		 * @param rootPackage
		 */
		SamePackage(@Nonnull final String rootPackage) {
			this.rootPackage = checkNotNull(rootPackage);
		}

		@Override
		public boolean apply(@Nullable final Class<?> input) {
			return this.rootPackage.equals(input.getPackage().getName());
		}
	}

	private final String rootPackage;

	/**
	 * @param rootPackage
	 */
	public TestClassesFinder(@Nonnull final String rootPackage) {
		this.rootPackage = checkNotNull(rootPackage);
	}

	@Override
	public String[] call() throws Exception {

		Class<?>[] classes = new Processor(new ClassloaderFinder((URLClassLoader) Thread.currentThread()
				.getContextClassLoader()), new TestFilter()).process();

		Collection<Class<?>> filteredClasses = Collections2.filter(Arrays.asList(classes), new SamePackage(
				this.rootPackage));

		return Collections2.transform(filteredClasses, ClassName.INSTANCE).toArray(new String[filteredClasses.size()]);
	}
}
