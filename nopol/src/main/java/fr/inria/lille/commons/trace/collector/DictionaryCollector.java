package fr.inria.lille.commons.trace.collector;

import java.util.Dictionary;
import java.util.Map;

public class DictionaryCollector extends ValueCollector {

	@Override
	protected void addValue(final String name, final Object value, Map<String, Object> storage) {
		Dictionary<?, ?> dictionary = (Dictionary<?, ?>) value;
		storage.put(name + ".size()", dictionary.size());
		storage.put(name + ".isEmpty()", dictionary.isEmpty());
	}

	@Override
	protected Class<?> collectingClass() {
		return Dictionary.class;
	}
	
}
