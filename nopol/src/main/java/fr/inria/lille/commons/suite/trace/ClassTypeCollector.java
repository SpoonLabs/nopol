package fr.inria.lille.commons.suite.trace;

import java.util.Map;

public abstract class ClassTypeCollector extends ValueCollector {
	
	@Override
	protected void addValue(String name, Object value, Map<String, Object> storage) {
		checkNullness(name, value, storage);
		addSpecificInformation(name, value, storage);
	}
	
	private boolean checkNullness(String name, Object value, Map<String, Object> storage) {
		boolean isNotNull = value != null;
		storage.put(name + "!=null", isNotNull);
		return isNotNull;
	}

	protected abstract void addSpecificInformation(String name, Object value, Map<String, Object> storage);
}
