package fr.inria.lille.commons.trace.collector;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.Iterator;

import xxl.java.container.various.Pair;

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
