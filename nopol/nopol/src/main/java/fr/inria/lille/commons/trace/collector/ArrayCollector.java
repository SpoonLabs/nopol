package fr.inria.lille.commons.trace.collector;

import xxl.java.container.various.Pair;

import java.lang.reflect.Array;
import java.util.Collection;

import static java.util.Arrays.asList;

public class ArrayCollector extends ValueCollector {

    @Override
    protected Class<?> collectingClass() {
        return Array.class;
    }

    @Override
    public boolean handlesClassOf(Object value) {
        return value.getClass().isArray();
    }

    @Override
    protected Collection<Pair<String, Object>> collectedValues(String name, Object value) {
        Pair<String, Integer> length = Pair.from(name + ".length", Array.getLength(value));
        return (Collection) asList(length);
    }

}
