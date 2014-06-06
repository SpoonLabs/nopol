package fr.inria.lille.commons.suite.trace;


public class BooleanCollector extends PrimitiveTypeCollector {
	
	@Override
	protected boolean handlesClassOf(Object object) {
		return Boolean.class.isInstance(object);
	}

}
