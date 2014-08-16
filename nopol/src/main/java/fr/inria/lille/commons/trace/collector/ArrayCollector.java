package fr.inria.lille.commons.trace.collector;

import static java.util.Arrays.asList;

import java.lang.reflect.Array;
import java.util.Collection;

import xxl.java.extensions.collection.Pair;

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
