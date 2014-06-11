package fr.inria.lille.commons.trace.collector;

import fr.inria.lille.commons.classes.ClassLibrary;


public class BooleanCollector extends PrimitiveTypeCollector {
	
	@Override
	protected boolean handlesClassOf(Object object) {
		return ClassLibrary.isInstanceOf(Boolean.class, object);
	}

}
