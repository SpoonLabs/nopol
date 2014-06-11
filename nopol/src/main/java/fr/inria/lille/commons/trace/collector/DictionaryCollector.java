package fr.inria.lille.commons.trace.collector;

import java.util.Dictionary;
import java.util.Map;

import fr.inria.lille.commons.classes.ClassLibrary;

public class DictionaryCollector extends ClassTypeCollector {

	@Override
	protected void addSpecificInformation(final String name, final Object value, Map<String, Object> storage) {
		Dictionary<?, ?> dictionary = (Dictionary<?, ?>) value;
		storage.put(name + ".size()", dictionary.size());
		storage.put(name + ".isEmpty()", dictionary.isEmpty());
	}

	@Override
	protected boolean handlesClassOf(Object object) {
		return ClassLibrary.isInstanceOf(Dictionary.class, object);
	}
}
