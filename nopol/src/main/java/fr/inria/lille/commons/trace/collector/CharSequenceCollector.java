package fr.inria.lille.commons.trace.collector;

import xxl.java.container.various.Pair;

import java.util.Collection;

import static java.util.Arrays.asList;

public class CharSequenceCollector extends ValueCollector {

    @Override
    protected Collection<Pair<String, Object>> collectedValues(final String name, final Object value) {
        CharSequence string = (CharSequence) value;
        Pair<String, Integer> length = Pair.from(name + ".length()", string.length());
        Pair<String, Boolean> isEmpty = Pair.from(name + ".length()==0", string.length() == 0);
        return (Collection) asList(length, isEmpty);
    }

    @Override
    protected Class<?> collectingClass() {
        return CharSequence.class;
    }

}
