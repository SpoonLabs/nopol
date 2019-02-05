package fr.inria.lille.commons.synthesis.operator;

import fr.inria.lille.commons.synthesis.smt.SMTLib;
import org.smtlib.IExpr.ISymbol;

import java.util.Arrays;

public class BinaryOperator<U, V, T> extends Operator<T> {

    public static BinaryOperator<Boolean, Boolean, Boolean> and() {
        return new BinaryOperator<Boolean, Boolean, Boolean>(Boolean.class, "&&", SMTLib.and(), Parameter.aBoolean(), Parameter.aBoolean());
    }

    public static BinaryOperator<Boolean, Boolean, Boolean> or() {
        return new BinaryOperator<Boolean, Boolean, Boolean>(Boolean.class, "||", SMTLib.or(), Parameter.aBoolean(), Parameter.aBoolean());
    }

    public static BinaryOperator<Number, Number, Boolean> numberDistinction() {
        return new BinaryOperator<Number, Number, Boolean>(Boolean.class, "!=", SMTLib.distinct(), Parameter.aNumber(), Parameter.aNumber());
    }

    public static BinaryOperator<Number, Number, Boolean> numberEquality() {
        return new BinaryOperator<Number, Number, Boolean>(Boolean.class, "==", SMTLib.equality(), Parameter.aNumber(), Parameter.aNumber());
    }

    public static BinaryOperator<Number, Number, Boolean> lessThan() {
        return new BinaryOperator<Number, Number, Boolean>(Boolean.class, "<", SMTLib.lessThan(), Parameter.aNumber(), Parameter.aNumber());
    }

    public static BinaryOperator<Number, Number, Boolean> lessOrEqualThan() {
        return new BinaryOperator<Number, Number, Boolean>(Boolean.class, "<=", SMTLib.lessOrEqualThan(), Parameter.aNumber(), Parameter.aNumber());
    }

    public static BinaryOperator<Number, Number, Number> addition() {
        return new BinaryOperator<Number, Number, Number>(Number.class, "+", SMTLib.addition(), Parameter.aNumber(), Parameter.aNumber());
    }

    public static BinaryOperator<Number, Number, Number> subtraction() {
        return new BinaryOperator<Number, Number, Number>(Number.class, "-", SMTLib.subtraction(), Parameter.aNumber(), Parameter.aNumber());
    }

    public static BinaryOperator<Number, Number, Number> multiplication() {
        return new BinaryOperator<Number, Number, Number>(Number.class, "*", SMTLib.multiplication(), Parameter.aNumber(), Parameter.aNumber());
    }

    public BinaryOperator(Class<T> resultType, String symbol, ISymbol smtlibIdentifier, Parameter<U> firstParameter, Parameter<V> secondParameter) {
        super(resultType, symbol, smtlibIdentifier, Arrays.asList(firstParameter, secondParameter));
    }

    @Override
    public <K> K accept(OperatorVisitor<K> visitor) {
        return visitor.visitBinaryOperator(this);
    }

}
