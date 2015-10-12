package fr.inria.lille.commons.trace.collector;

import xxl.java.container.various.Pair;

import java.util.Collection;

import static java.util.Arrays.asList;

public class CollectionCollector extends ValueCollector {

    @Override
    protected Collection<Pair<String, Object>> collectedValues(String name, Object value) {
        Collection<?> collection = (Collection<?>) value;
        Pair<String, Integer> size = Pair.from(name + ".size()", collection.size());
        Pair<String, Boolean> isEmpty = Pair.from(name + ".isEmpty()", collection.isEmpty());
        return (Collection) asList(size, isEmpty);
    }

    @Override
    protected Class<?> collectingClass() {
        return Collection.class;
    }

}
