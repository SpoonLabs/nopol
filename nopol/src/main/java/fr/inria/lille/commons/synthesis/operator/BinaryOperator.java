package fr.inria.lille.commons.synthesis.operator;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.smtlib;

import java.util.Arrays;

import org.smtlib.IExpr.ISymbol;

public class BinaryOperator<U, V, T> extends Operator<T> {

	public static BinaryOperator<Boolean, Boolean, Boolean> and() {
		return new BinaryOperator<Boolean, Boolean, Boolean>(Boolean.class, "&&", smtlib().and(), Parameter.aBoolean(), Parameter.aBoolean());
	}
	
	public static BinaryOperator<Boolean, Boolean, Boolean> or() {
		return new BinaryOperator<Boolean, Boolean, Boolean>(Boolean.class, "||", smtlib().or(), Parameter.aBoolean(), Parameter.aBoolean());
	}
	
	public static BinaryOperator<Number, Number, Boolean> numberDistinction() {
		return new BinaryOperator<Number, Number, Boolean>(Boolean.class, "!=", smtlib().distinct(), Parameter.aNumber(), Parameter.aNumber());
	}
	
	public static BinaryOperator<Number, Number, Boolean> numberEquality() {
		return new BinaryOperator<Number, Number, Boolean>(Boolean.class, "==", smtlib().equals(), Parameter.aNumber(), Parameter.aNumber());
	}
	
	public static BinaryOperator<Number, Number, Boolean> lessThan() {
		return new BinaryOperator<Number, Number, Boolean>(Boolean.class, "<", smtlib().lessThan(), Parameter.aNumber(), Parameter.aNumber());
	}
	
	public static BinaryOperator<Number, Number, Boolean> lessOrEqualThan() {
		return new BinaryOperator<Number, Number, Boolean>(Boolean.class, "<=", smtlib().lessOrEqualThan(), Parameter.aNumber(), Parameter.aNumber());
	}
	
	public static BinaryOperator<Number, Number, Number> addition() {
		return new BinaryOperator<Number, Number, Number>(Number.class, "+", smtlib().addition(), Parameter.aNumber(), Parameter.aNumber());
	}
	
	public static BinaryOperator<Number, Number, Number> substraction() {
		return new BinaryOperator<Number, Number, Number>(Number.class, "-", smtlib().substraction(), Parameter.aNumber(), Parameter.aNumber());
	}
	
	public static BinaryOperator<Number, Number, Number> multiplication() {
		return new BinaryOperator<Number, Number, Number>(Number.class, "*", smtlib().multiplication(), Parameter.aNumber(), Parameter.aNumber());
	}
	
	public BinaryOperator(Class<T> resultType, String symbol, ISymbol smtlibIdentifier, Parameter<U> firstParameter, Parameter<V> secondParameter) {
		super(resultType, symbol, smtlibIdentifier, Arrays.asList(firstParameter, secondParameter));
	}

	@Override
	public <K> K accept(OperatorVisitor<K> visitor) {
		return visitor.visitBinaryOperator(this);
	}

}
