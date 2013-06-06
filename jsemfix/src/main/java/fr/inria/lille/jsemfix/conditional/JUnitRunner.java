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
import static java.util.Arrays.asList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.runner.JUnitCore;
import org.junit.runner.notification.RunListener;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ComputationException;

/**
 * @author Favio D. DeMarco
 * 
 */
final class JUnitRunner implements Runnable {

	private final RunListener listener;
	private final String[] classes;

	private enum StringToClass implements Function<String, Class<?>> {
		INSTANCE;

		@Override
		@Nullable
		public Class<?> apply(@Nullable final String input) {
			try {
				return Thread.currentThread().getContextClassLoader().loadClass(input);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				throw new ComputationException(e);
			}
		}
	}

	/**
	 * @param listener
	 * @param classes
	 */
	public JUnitRunner(@Nonnull final RunListener listener, @Nonnull final String[] classes) {
		this.listener = checkNotNull(listener);
		this.classes = checkNotNull(classes);
	}

	@Override
	public void run() {
		JUnitCore runner = new JUnitCore();
		runner.addListener(this.listener);

		Class<?>[] testClasses = Collections2.transform(asList(this.classes), StringToClass.INSTANCE).toArray(
				new Class<?>[this.classes.length]);

		runner.run(testClasses);
	}
}
