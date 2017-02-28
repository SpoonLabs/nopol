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

import sacha.finder.classes.impl.ClassloaderFinder;
import sacha.finder.filters.impl.TestFilter;
import sacha.finder.processor.Processor;
import xxl.java.container.classic.MetaList;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Favio D. DeMarco
 */
public class TestClassesFinder {
    protected String[] namesFrom(Collection<Class<?>> classes) {
        String[] names = new String[classes.size()];
        int index = 0;
        for (Class<?> aClass : classes) {
            names[index] = aClass.getName();
            index += 1;
        }
        return names;
    }

    public String[] findIn(final URL[] classpath, boolean acceptTestSuite) {
        URLClassLoader classLoader = new URLClassLoader(classpath);
        ClassloaderFinder finder = new ClassloaderFinder(classLoader);
        TestFilter testFilter = new TestFilter();
        Processor processor = new Processor(finder, testFilter);
        Class<?>[] classes = processor.process();

        Collection<Class<?>> allTestClasses = MetaList.newArrayList(classes);

        String[] testClasses = namesFrom(allTestClasses);

        if (!acceptTestSuite) {
            return removeTestSuite(testClasses);
        }

        return testClasses;
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
