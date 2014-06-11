package fr.inria.lille.commons.trace.collector;

import java.util.Collection;
import java.util.Map;

import fr.inria.lille.commons.classes.ClassLibrary;

public class CollectionCollector extends ClassTypeCollector {

	@Override
	protected void addSpecificInformation(final String name, final Object value, Map<String, Object> storage) {
		Collection<?> collection = (Collection<?>) value;
		storage.put(name + ".size()", collection.size());
		storage.put(name + ".isEmpty()", collection.isEmpty());
	}

	@Override
	protected boolean handlesClassOf(Object object) {
		return ClassLibrary.isInstanceOf(Collection.class, object);
	}
	
}
