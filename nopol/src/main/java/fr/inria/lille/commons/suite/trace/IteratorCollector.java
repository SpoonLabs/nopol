package fr.inria.lille.commons.suite.trace;

import java.util.Iterator;
import java.util.Map;

public class IteratorCollector extends ClassTypeCollector {

	@Override
	protected void addSpecificInformation(final String name, final Object value, Map<String, Object> storage) {
		Iterator<?> iterator = (Iterator<?>) value;
		storage.put(name + ".hasNext()", iterator.hasNext());
	}

	@Override
	protected boolean handlesClassOf(Object object) {
		return Iterator.class.isInstance(object);
	}
}
