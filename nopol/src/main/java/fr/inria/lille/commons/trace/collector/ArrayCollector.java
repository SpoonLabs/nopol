package fr.inria.lille.commons.trace.collector;

import java.lang.reflect.Array;
import java.util.Map;

public class ArrayCollector extends ClassTypeCollector {

	@Override
	protected void addSpecificInformation(final String name, final Object value, Map<String, Object> storage) {
		storage.put(name + ".length", Array.getLength(value));
	}

	@Override
	protected boolean handlesClassOf(Object object) {
		return object.getClass().isArray();
	}

}
