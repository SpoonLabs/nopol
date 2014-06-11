package fr.inria.lille.commons.trace.collector;

import java.util.Map;

import fr.inria.lille.commons.classes.ClassLibrary;

public class MapCollector extends ClassTypeCollector {

	@Override
	protected void addSpecificInformation(final String name, final Object value, Map<String, Object> storage) {
		Map<?, ?> map = (Map<?, ?>) value;
		storage.put(name + ".size()", map.size());
		storage.put(name + ".isEmpty()", map.isEmpty());
	}

	@Override
	protected boolean handlesClassOf(Object object) {
		return ClassLibrary.isInstanceOf(Map.class, object);
	}
	
}
