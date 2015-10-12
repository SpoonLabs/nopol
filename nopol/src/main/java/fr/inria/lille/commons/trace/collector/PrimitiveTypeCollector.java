package fr.inria.lille.commons.trace.collector;

import xxl.java.container.various.Pair;

import java.util.Collection;

import static java.util.Arrays.asList;

public abstract class PrimitiveTypeCollector extends ValueCollector {

    @Override
    protected Collection<Pair<String, Object>> collectedValues(String name, Object object) {
        Pair<String, Object> value = Pair.from(name, object);
        return asList(value);
    }
}
