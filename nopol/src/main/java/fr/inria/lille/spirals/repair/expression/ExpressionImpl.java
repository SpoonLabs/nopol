package fr.inria.lille.spirals.repair.expression;


import fr.inria.lille.spirals.repair.commons.Candidates;

import java.util.ArrayList;
import java.util.List;

/**
 * is the generic type of an expression
 */
public abstract class ExpressionImpl implements Expression {
    private double priority;
    private Object value;
    private Class returnType;
    private List<Expression> alternatives;
    private List<Expression> inAlternativesOf;
    private List<Expression> inExpressions;

    /**
     *
     */
    public ExpressionImpl(Object value, Class returnType) {
        this.value = value;
        this.returnType = returnType;
        this.alternatives = new ArrayList<>();
        this.inAlternativesOf = new ArrayList<>();
        this.inExpressions = new ArrayList<>();
        this.priority = 1;
    }

    /**
     *
     */
    @Override
    public Object getValue() {
        return this.value;
    }

    protected void setValue(Object value) {
        this.value = value;
    }

    /**
     *
     */
    @Override
    public Class getType() {
        return returnType;
    }

    protected void setType(Class returnType) {
        this.returnType = returnType;
    }

    /**
     *
     */
    @Override
    public List<Expression> getAlternatives() {
        return alternatives;
    }

    @Override
    public List<Expression> getInAlternativesOf() {
        return inAlternativesOf;
    }

    public List<Expression> getInExpressions() {
        return inExpressions;
    }

    @Override
    public double getPriority() {
        return priority;
    }

    @Override
    public void setPriority(double priority) {
        this.priority = priority;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Expression)) return false;
        if (o instanceof Constant && !(this instanceof Constant)) return false;
        if (this instanceof Constant && !(o instanceof Constant)) return false;

        Expression that = (Expression) o;

        if (returnType != null ? !returnType.equals(that.getType()) : that.getType() != null) return false;
        return !(value != null ? !value.equals(that.getValue()) : that.getValue() != null);

    }

    public boolean sameExpression(Expression e) {
        return this.toString().equals(e.toString());
    }

    @Override
    public int compareTo(Expression o) {
        return (int) (Math.round(100 * this.getWeight()) - Math.round(100 * o.getWeight()));
    }

    @Override
    public Object evaluate(Candidates values) {
        for (int i = 0; i < values.size(); i++) {
            ExpressionImpl expression = (ExpressionImpl) values.get(i);
            if (expression.sameExpression(this)) {
                return expression.getValue();
            }
            for (int j = 0; j < expression.getAlternatives().size(); j++) {
                ExpressionImpl expression1 = (ExpressionImpl) expression.getAlternatives().get(j);
                if (expression1.sameExpression(this)) {
                    return expression1.getValue();
                }
            }
        }
        throw new RuntimeException("Expression not found");
    }

    @Override
    public String asPatch() {
        return this.toString();
    }
}

