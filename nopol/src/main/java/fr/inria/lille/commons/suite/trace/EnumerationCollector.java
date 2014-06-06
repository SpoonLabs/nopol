package fr.inria.lille.commons.suite.trace;

import java.util.Enumeration;
import java.util.Map;

public class EnumerationCollector extends ClassTypeCollector {
	
	@Override
	protected void addSpecificInformation(final String name, final Object value, Map<String, Object> storage) {
		Enumeration<?> iterator = (Enumeration<?>) value;
		storage.put(name + ".hasMoreElements()", iterator.hasMoreElements());
	}

	@Override
	protected boolean handlesClassOf(Object object) {
		return Enumeration.class.isInstance(object);
	}
}
