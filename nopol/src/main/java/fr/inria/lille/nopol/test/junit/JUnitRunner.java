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
package fr.inria.lille.nopol.test.junit;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import com.google.common.collect.ComputationException;

/**
 * @author Favio D. DeMarco
 * 
 */
public final class JUnitRunner implements Callable<Result> {

	/**
	 * @param classes
	 */
	public JUnitRunner(@Nonnull final String[] classes) {
		this.classes = checkNotNull(classes);
	}

	/**
	 * @param classes
	 * @param listener
	 */
	public JUnitRunner(@Nonnull final String[] classes, @Nonnull final RunListener listener) {
		this.listeners.add(checkNotNull(listener));
		this.classes = checkNotNull(classes);
	}

	@Override
	public Result call() throws Exception {
		JUnitCore runner = new JUnitCore();
		for (RunListener listener : this.listeners) {
			runner.addListener(listener);
		}
		Class<?>[] testClasses = classArrayFrom(classes);
		return runner.run(testClasses);
	}

	private Class<?>[] classArrayFrom(String[] classNames) {
		Class<?>[] classes = new Class<?>[classNames.length];
		int index = 0;
		for (String className : classNames) {
			try {
				classes[index] = Thread.currentThread().getContextClassLoader().loadClass(className);
			} catch (ClassNotFoundException e) {
				throw new ComputationException(e);
			}
			index += 1;
		}
		return classes;
	}
	
	private final String[] classes;
	private final List<RunListener> listeners = new ArrayList<>();
}
