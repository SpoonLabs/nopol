package fr.inria.lille.commons.synthesis.operator;

import org.smtlib.IExpr.ISymbol;

import java.util.List;

public abstract class DoubleOperator<T> extends Operator<T> {

    public DoubleOperator(Class<T> resultType, String firstSymbol, String secondSymbol, ISymbol smtlibIdentifier, List<Parameter<?>> parameters) {
        super(resultType, firstSymbol + secondSymbol, smtlibIdentifier, parameters);
        this.firstSymbol = firstSymbol;
        this.secondSymbol = secondSymbol;
    }

    public String firstSymbol() {
        return firstSymbol;
    }

    public String secondSymbol() {
        return secondSymbol;
    }

    private String firstSymbol;
    private String secondSymbol;
}
