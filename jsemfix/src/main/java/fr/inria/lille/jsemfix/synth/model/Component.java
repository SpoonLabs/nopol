package fr.inria.lille.jsemfix.synth.model;

import java.util.List;

public interface Component {

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
