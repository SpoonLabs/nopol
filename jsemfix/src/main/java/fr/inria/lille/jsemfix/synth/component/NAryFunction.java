package fr.inria.lille.jsemfix.synth.component;

import java.util.List;

final class NAryFunction implements Function {

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
	 * @see fr.inria.lille.jsemfix.synth.component.Function#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * @see fr.inria.lille.jsemfix.synth.component.Function#getOutputType()
	 */
	@Override
	public Type getOutputType() {
		return this.outputType;
	}

	/**
	 * @see fr.inria.lille.jsemfix.synth.component.Function#getParameters()
	 */
	@Override
	public List<Type> getParameterTypes() {
		return this.parameters;
	}
}
