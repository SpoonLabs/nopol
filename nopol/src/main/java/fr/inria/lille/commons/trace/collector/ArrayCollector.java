package fr.inria.lille.commons.trace.collector;

import java.lang.reflect.Array;
import java.util.Map;

public class ArrayCollector extends ValueCollector {

	@Override
	protected void addValue(String name, Object value, Map<String, Object> storage) {
		storage.put(name + ".length", Array.getLength(value));
	}

	@Override
	protected Class<?> collectingClass() {
		return Array.class;
	}
	
	@Override
	public boolean handlesClassOf(Object value) {
		return value.getClass().isArray();
	}

}
