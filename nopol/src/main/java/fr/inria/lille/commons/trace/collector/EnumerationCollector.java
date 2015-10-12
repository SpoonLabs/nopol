package fr.inria.lille.commons.trace.collector;

import xxl.java.container.various.Pair;

import java.util.Collection;
import java.util.Enumeration;

import static java.util.Arrays.asList;

public class EnumerationCollector extends ValueCollector {

    @Override
    protected Collection<Pair<String, Object>> collectedValues(final String name, final Object value) {
        Enumeration<?> iterator = (Enumeration<?>) value;
        Pair<String, Boolean> hasMoreElements = Pair.from(name + ".hasMoreElements()", iterator.hasMoreElements());
        return (Collection) asList(hasMoreElements);
    }

    @Override
    protected Class<?> collectingClass() {
        return Enumeration.class;
    }

}
