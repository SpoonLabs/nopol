package fr.inria.lille.commons.trace.collector;

import java.util.Map;

public class MapCollector extends ValueCollector {

	@Override
	protected void addValue(final String name, final Object value, Map<String, Object> storage) {
		Map<?, ?> map = (Map<?, ?>) value;
		storage.put(name + ".size()", map.size());
		storage.put(name + ".isEmpty()", map.isEmpty());
	}

	@Override
	protected Class<?> collectingClass() {
		return Map.class;
	}
	
}
