package fr.inria.lille.commons.trace.collector;

import java.util.Map;

public class CharSequenceCollector extends ValueCollector {

	@Override
	protected void addValue(final String name, final Object value, Map<String, Object> storage) {
		CharSequence string = (CharSequence) value;
		storage.put(name + ".length()", string.length());
		storage.put(name + ".length()==0", string.length() == 0);
	}

	@Override
	protected Class<?> collectingClass() {
		return CharSequence.class;
	}
	
}
