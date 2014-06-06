package fr.inria.lille.commons.suite.trace;

import java.util.Collection;
import java.util.Map;

public class CollectionCollector extends ClassTypeCollector {

	@Override
	protected void addSpecificInformation(final String name, final Object value, Map<String, Object> storage) {
		Collection<?> collection = (Collection<?>) value;
		storage.put(name + ".size()", collection.size());
		storage.put(name + ".isEmpty()", collection.isEmpty());
	}

	@Override
	protected boolean handlesClassOf(Object object) {
		return Collection.class.isInstance(object);
	}
	
}
