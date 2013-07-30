package fr.inria.lille.jefix.synth.smt.model;

import java.util.List;

import fr.inria.lille.jefix.synth.expression.CompositeExpression;

public final class NAryFunction implements Component {

	private final CompositeExpression expression;

	private final String name;

	private final Type outputType;

	private final List<Type> parameters;


	/**
	 * @param name
	 * @param expression
	 * @param parameters
	 * @param outputType
	 */
	NAryFunction(final String name, final CompositeExpression expression, final List<Type> parameters,
			final Type outputType) {
		this.name = name;
		this.expression = expression;
		this.parameters = parameters;
		this.outputType = outputType;
	}

	/**
	 * @return the expression
	 */
	public CompositeExpression getExpression() {
		return this.expression;
	}

	/**
	 * @see fr.inria.lille.jefix.synth.smt.model.Component#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}


	/**
	 * @see fr.inria.lille.jefix.synth.smt.model.Component#getOutputType()
	 */
	@Override
	public Type getOutputType() {
		return this.outputType;
	}

	/**
	 * @see fr.inria.lille.jefix.synth.smt.model.Component#getParameters()
	 */
	@Override
	public List<Type> getParameterTypes() {
		return this.parameters;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
}
