package fr.inria.lille.jsemfix.synth.model;

import java.util.List;

final class NAryFunction implements Component {

	private final String name;

	private final Type outputType;

	private final List<Type> parameters;

	/**
	 * @param name
	 * @param parameters
	 * @param outputType
	 */
	NAryFunction(final String name, final List<Type> parameters, final Type outputType) {
		this.name = name;
		this.parameters = parameters;
		this.outputType = outputType;
	}

	/**
	 * @see fr.inria.lille.jsemfix.synth.model.Component#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * @see fr.inria.lille.jsemfix.synth.model.Component#getOutputType()
	 */
	@Override
	public Type getOutputType() {
		return this.outputType;
	}

	/**
	 * @see fr.inria.lille.jsemfix.synth.model.Component#getParameters()
	 */
	@Override
	public List<Type> getParameterTypes() {
		return this.parameters;
	}
}
