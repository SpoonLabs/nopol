package fr.inria.lille.commons.synthesis.operator;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.smtlib;

import java.util.Arrays;
import java.util.List;

import org.smtlib.IExpr.ISymbol;

public class UnaryOperator<U, T> extends Operator<T> {

	public static UnaryOperator<Boolean, Boolean> not() {
		return new UnaryOperator<Boolean, Boolean>(Boolean.class, "!", smtlib().not(), Parameter.aBoolean());
	}
	
	public UnaryOperator(Class<T> resultType, String symbol, ISymbol smtlibIdentifier, Parameter<U> parameter) {
		super(resultType, symbol, smtlibIdentifier, (List) Arrays.asList(parameter));
	}

	@Override
	public <K> K accept(OperatorVisitor<K> visitor) {
		return visitor.visitUnaryOperator(this);
	}
	
}
