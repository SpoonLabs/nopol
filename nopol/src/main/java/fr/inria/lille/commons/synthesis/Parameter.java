package fr.inria.lille.commons.synthesis;

public class Parameter<T> extends ObjectTemplate<T> {

	public Parameter(Class<T> aClass) {
		super(aClass);
	}

	public boolean admitsAsArgument(Variable<?> variable) {
		return admitsAsArgument(variable.value());
	}
	
	public boolean admitsAsArgument(Object object) {
		return myClass().isInstance(object);
	}
}
