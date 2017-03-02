package fr.inria.lille.repair.common.finder;

import sacha.finder.classes.impl.ClassloaderFinder;
import sacha.finder.filters.impl.TestFilter;
import sacha.finder.processor.Processor;
import xxl.java.container.classic.MetaList;

import java.net.URLClassLoader;
import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * This class is use in a separate thread to find the classes:
 * we use another tread to avoid ClassNotFoundError when loading the class
 * Created by urli on 02/03/2017.
 */
public class TestClassFinderRunner implements Callable<Collection<Class<?>>> {
    public Collection<Class<?>> call() throws Exception {

        URLClassLoader classLoader = (URLClassLoader)Thread.currentThread().getContextClassLoader();
        ClassloaderFinder classloaderFinder = new ClassloaderFinder(classLoader);
        TestFilter testFilter = new TestFilter();

        Processor processor = new Processor(classloaderFinder, testFilter);
        Class<?>[] classes = processor.process();

        return MetaList.newArrayList(classes);
    }
}
