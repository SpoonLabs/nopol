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
package fr.inria.lille.commons.suite;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import com.google.common.collect.ComputationException;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.utils.library.ClassLibrary;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class JUnitRunner implements Callable<Result> {
	
	public JUnitRunner(@Nonnull final String[] classes, RunListener listener) {
		this.testClasses = checkNotNull(classes);
		this.listener = listener;
	}

	@Override
	public Result call() throws Exception {
		JUnitCore runner = new JUnitCore();
		runner.addListener(listener);
		Class<?>[] testClasses = testClassesFromCustomClassLoader();
		return runner.run(testClasses);
	}

	private Class<?>[] testClassesFromCustomClassLoader() {
		Collection<Class<?>> classes = ListLibrary.newLinkedList();
		for (String className : testClasses) {
			try {
				Class<?> testClass = Thread.currentThread().getContextClassLoader().loadClass(className);
				if (! ClassLibrary.isAbstract(testClass)) {
					classes.add(testClass);
				}
			} catch (ClassNotFoundException e) {
				throw new ComputationException(e);
			}
		}
		return classes.toArray(new Class<?>[classes.size()]);
	}
	
	private final String[] testClasses;
	private final RunListener listener;
}
