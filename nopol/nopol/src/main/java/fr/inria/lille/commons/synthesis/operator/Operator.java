package fr.inria.lille.commons.synthesis.operator;

import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.expression.ObjectTemplate;
import org.smtlib.IExpr.ISymbol;

import java.util.List;

public abstract class Operator<T> extends ObjectTemplate<T> {

    public abstract <K> K accept(OperatorVisitor<K> visitor);

    public Operator(Class<T> resultType, String symbol, ISymbol smtlibIdentifier, List<Parameter<?>> parameters) {
        super(resultType);
        this.symbol = symbol;
        this.parameters = parameters;
        this.smtlibIdentifier = smtlibIdentifier;
    }

    public boolean admitsAsArguments(List<Expression<?>> expressions) {
        if (expressions.size() == arity()) {
            return admitsAll(expressions);
        }
        return false;
    }

    private boolean admitsAll(List<Expression<?>> expressions) {
        int index = 0;
        for (Parameter<?> parameter : parameters()) {
            Expression<?> expression = expressions.get(index);
            if (!parameter.admitsAsArgument(expression)) {
                return false;
            }
            index += 1;
        }
        return true;
    }

    public int arity() {
        return parameters().size();
    }

    public String symbol() {
        return symbol;
    }

    public ISymbol smtlibIdentifier() {
        return smtlibIdentifier;
    }

    public List<Parameter<?>> parameters() {
        return parameters;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result + ((smtlibIdentifier == null) ? 0 : smtlibIdentifier.hashCode());
        result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Operator<?> other = (Operator<?>) obj;
        if (parameters == null) {
            if (other.parameters != null)
                return false;
        } else if (!parameters.equals(other.parameters))
            return false;
        if (smtlibIdentifier == null) {
            if (other.smtlibIdentifier != null)
                return false;
        } else if (!smtlibIdentifier.equals(other.smtlibIdentifier))
            return false;
        if (symbol == null) {
            if (other.symbol != null)
                return false;
        } else if (!symbol.equals(other.symbol))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return symbol() + " of arity: " + arity();
    }

    private String symbol;
    private ISymbol smtlibIdentifier;
    private List<Parameter<?>> parameters;
}