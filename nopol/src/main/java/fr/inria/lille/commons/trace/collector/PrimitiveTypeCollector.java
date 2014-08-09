package fr.inria.lille.commons.trace.collector;

import java.util.Map;

public abstract class PrimitiveTypeCollector extends ValueCollector {

	public void addValue(String name, Object value, Map<String, Object> storage) {
		storage.put(name, value);
	}
}
