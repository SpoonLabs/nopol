package fr.inria.lille.jsemfix.synth.component;

import java.util.List;

public interface Function {

	/**
	 * @return the name
	 */
	String getName();

	/**
	 * @return the outputType
	 */
	Type getOutputType();

	/**
	 * @return the parameters
	 */
	List<Type> getParameterTypes();
}
