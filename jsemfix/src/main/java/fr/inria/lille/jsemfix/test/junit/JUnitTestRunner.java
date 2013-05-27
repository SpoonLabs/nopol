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
package fr.inria.lille.jsemfix.test.junit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.google.common.collect.Collections2;

import fr.inria.lille.jsemfix.Program;
import fr.inria.lille.jsemfix.test.Test;
import fr.inria.lille.jsemfix.test.TestRunner;

/**
 * @author Favio D. DeMarco
 *
 */
public final class JUnitTestRunner implements TestRunner {

	private final Class<?>[] tests;

	/**
	 * 
	 */
	public JUnitTestRunner(final Class<?>... tests) {
		this.tests = tests;
	}

	@Override
	public Set<Test> run(final Program program) {

		Result result = (Result) program.executeInContext(JUnitCore.class, "runClasses", this.tests);

		// XXX FIXME TODO law of Demeter
		Collection<Failure> failures = result.getFailures();

		Collection<Test> tests = Collections2.transform(failures, FailureTestWrapperFunction.INSTANCE);

		return new HashSet<>(tests);
	}
}
