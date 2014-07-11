package fr.inria.lille.commons.trace.collector;

import java.util.Collection;
import java.util.Map;

public class CollectionCollector extends ValueCollector {

	@Override
	protected void addValue(String name, Object value, Map<String, Object> storage) {
		Collection<?> collection = (Collection<?>) value;
		storage.put(name + ".size()", collection.size());
		storage.put(name + ".isEmpty()", collection.isEmpty());
	}

	@Override
	protected Class<?> collectingClass() {
		return Collection.class;
	}
	
}
