package fr.inria.lille.commons.trace.collector;

import java.util.Enumeration;
import java.util.Map;

public class EnumerationCollector extends ValueCollector {
	
	@Override
	protected void addValue(final String name, final Object value, Map<String, Object> storage) {
		Enumeration<?> iterator = (Enumeration<?>) value;
		storage.put(name + ".hasMoreElements()", iterator.hasMoreElements());
	}

	@Override
	protected Class<?> collectingClass() {
		return Enumeration.class;
	}

}
