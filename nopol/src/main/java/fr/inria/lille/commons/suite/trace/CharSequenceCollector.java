package fr.inria.lille.commons.suite.trace;

import java.util.Map;

public class CharSequenceCollector extends ClassTypeCollector {

	@Override
	protected void addSpecificInformation(final String name, final Object value, Map<String, Object> storage) {
		CharSequence string = (CharSequence) value;
		storage.put(name + ".length()", string.length());
		storage.put(name + ".length()==0", string.length() == 0);
	}

	@Override
	protected boolean handlesClassOf(Object object) {
		return CharSequence.class.isInstance(object);
	}
	
}
