package fr.inria.lille.commons.trace.collector;

import fr.inria.lille.commons.classes.ClassLibrary;


public class NumberCollector extends PrimitiveTypeCollector {

	@Override
	protected boolean handlesClassOf(Object object) {
		return ClassLibrary.isInstanceOf(Number.class, object);
	}

}
