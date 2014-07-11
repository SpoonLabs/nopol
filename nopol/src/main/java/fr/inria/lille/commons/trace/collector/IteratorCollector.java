package fr.inria.lille.commons.trace.collector;

import java.util.Iterator;
import java.util.Map;

public class IteratorCollector extends ValueCollector {

	@Override
	protected void addValue(final String name, final Object value, Map<String, Object> storage) {
		Iterator<?> iterator = (Iterator<?>) value;
		storage.put(name + ".hasNext()", iterator.hasNext());
	}

	@Override
	protected Class<?> collectingClass() {
		return Iterator.class;
	}

}
