package fr.inria.lille.nopol.synth.smt.model;

import java.util.List;

import fr.inria.lille.nopol.synth.expression.CompositeExpression;

public interface Component {

	/**
	 * The related/equivalent Java expression
	 * 
	 * @return
	 */
	CompositeExpression getExpression();

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
