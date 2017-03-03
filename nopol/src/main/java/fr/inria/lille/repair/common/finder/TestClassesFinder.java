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
package fr.inria.lille.repair.common.finder;

import xxl.java.junit.CustomClassLoaderThreadFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * @author Favio D. DeMarco
 */
public final class TestClassesFinder {

    protected String[] namesFrom(Collection<Class<?>> classes) {
        String[] names = new String[classes.size()];
        int index = 0;
        for (Class<?> aClass : classes) {
            names[index] = aClass.getName();
            index += 1;
        }
        return names;
    }

    public String[] findIn(ClassLoader dumpedToClassLoader, boolean acceptTestSuite) {

        ThreadFactory threadFactory = new CustomClassLoaderThreadFactory(dumpedToClassLoader);
        ExecutorService executor = Executors.newSingleThreadExecutor(threadFactory);
        TestClassFinderRunner testClassFinderRunner = new TestClassFinderRunner();
        Future<Collection<Class<?>>> future = executor.submit(testClassFinderRunner);
        String[] testClasses;

        try {
            executor.shutdown();

            Collection<Class<?>> findingClasses = future.get();
            testClasses = namesFrom(findingClasses);
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdownNow();
        }

        if (!acceptTestSuite) {
            testClasses = removeTestSuite(testClasses);
        }

        return testClasses;
    }

    public String[] findIn(final URL[] classpath, boolean acceptTestSuite) {
        return findIn(new URLClassLoader(classpath), acceptTestSuite);
    }

    public String[] removeTestSuite(String[] totalTest) {
        List<String> tests = new ArrayList<String>();
        for (int i = 0; i < totalTest.length; i++) {
            if (!totalTest[i].endsWith("Suite")) {
                tests.add(totalTest[i]);
            }
        }
        return tests.toArray(new String[tests.size()]);
    }

}
