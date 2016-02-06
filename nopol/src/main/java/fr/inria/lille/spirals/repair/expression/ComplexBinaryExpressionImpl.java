package fr.inria.lille.spirals.repair.expression;


import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.spirals.repair.expression.operator.Operator;

public class ComplexBinaryExpressionImpl extends BinaryExpressionImpl implements ComplexBinaryExpression {

    public ComplexBinaryExpressionImpl(Operator operator, Expression first, Expression second, Object value, Class type) {
        super(operator, first, second, value, Boolean.class);
    }

    public ComplexBinaryExpressionImpl(Operator operator, Expression first, Expression second) {
        super(operator, first, second, null, Boolean.class);
        evaluate();
    }

    @Override
    public Object evaluate(Candidates values) {

        Object exp1Value = this.getFirstExpression().getValue();
        Object exp2Value = this.getSecondExpression().getValue();

        ((ExpressionImpl) this.getFirstExpression()).setValue(this.getFirstExpression().evaluate(values));
        ((ExpressionImpl) this.getSecondExpression()).setValue(this.getSecondExpression().evaluate(values));

        Object value = null;
        try {
            value = getValueFromOperation();
        } catch (ArithmeticException e) {
            // ignore
        }

        ((ExpressionImpl) this.getFirstExpression()).setValue(exp1Value);
        ((ExpressionImpl) this.getSecondExpression()).setValue(exp2Value);

        return value;
    }


    @Override
    protected Object getValueFromOperation() {
        if (!getOperator().getParam1().isAssignableFrom(getFirstExpression().getType())) {
            return null;
        }
        if (!getOperator().getParam2().isAssignableFrom(getSecondExpression().getType())) {
            return null;
        }
        if (getFirstExpression().getValue() == null && !(getOperator() == Operator.EQ || getOperator() == Operator.NEQ)) {
            return null;
        }
        if (getSecondExpression().getValue() == null && !(getOperator() == Operator.EQ || getOperator() == Operator.NEQ || getOperator() == Operator.OR)) {
            return null;
        }

        switch (getOperator()) {
            case EQ:
                return getFirstExpression().getValue() == getSecondExpression().getValue();
            case NEQ:
                return getFirstExpression().getValue() != getSecondExpression().getValue();
        }
        return null;
    }


}

