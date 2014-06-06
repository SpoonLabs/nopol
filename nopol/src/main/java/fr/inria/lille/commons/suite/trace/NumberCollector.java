package fr.inria.lille.commons.suite.trace;


public class NumberCollector extends PrimitiveTypeCollector {

	@Override
	protected boolean handlesClassOf(Object object) {
		return Number.class.isInstance(object);
	}

}
