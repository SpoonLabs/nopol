package fr.inria.lille.commons.trace.collector;

import xxl.java.container.various.Pair;

import java.util.Collection;
import java.util.Iterator;

import static java.util.Arrays.asList;

public class IteratorCollector extends ValueCollector {

    @Override
    protected Collection<Pair<String, Object>> collectedValues(final String name, final Object value) {
        Iterator<?> iterator = (Iterator<?>) value;
        Pair<String, Boolean> hasNext = Pair.from(name + ".hasNext()", iterator.hasNext());
        return (Collection) asList(hasNext);
    }

    @Override
    protected Class<?> collectingClass() {
        return Iterator.class;
    }

}
