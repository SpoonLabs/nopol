package fr.inria.lille.spirals.repair.expression;


import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.spirals.repair.expression.value.Value;

/**
 * is the generic type of an expression
 */
public abstract class ExpressionImpl implements Expression {
    private double priority;

    private Value value;

    public ExpressionImpl(Value value) {
        setValue(value);
        this.priority = 1;
    }

    @Override
    public Value getValue() {
        return this.value;
    }

    @Override
    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public double getPriority() {
        return priority;
    }

    @Override
    public boolean sameExpression(Expression e) {
        if (e.getClass() != this.getClass()) {
            return false;
        }
        return this.asPatch().equals(e.asPatch());
    }

    @Override
    public int compareTo(Expression o) {
        return (int) (Math.round(100 * this.getWeight()) - Math.round(100 * o.getWeight()));
    }

    @Override
    public Value evaluate(Candidates values) {
        if (getValue().isConstant()) {
            return getValue();
        }
        for (Object value1 : values) {
            Expression expression = (Expression) value1;
            if (expression.sameExpression(this)) {
                return expression.getValue();
            }
        }
        return Value.NOVALUE;
    }


}

