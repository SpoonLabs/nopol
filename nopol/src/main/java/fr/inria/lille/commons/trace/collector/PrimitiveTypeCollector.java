package fr.inria.lille.commons.trace.collector;

import static java.util.Arrays.asList;

import java.util.Collection;

import fr.inria.lille.commons.collections.Pair;

public abstract class PrimitiveTypeCollector extends ValueCollector {

	@Override
	protected Collection<Pair<String, Object>> collectedValues(String name, Object object) {
		Pair<String, Object> value = Pair.from(name, object);
		return asList(value);
	}
}
