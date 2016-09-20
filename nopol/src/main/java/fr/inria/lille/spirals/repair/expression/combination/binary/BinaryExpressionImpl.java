package fr.inria.lille.spirals.repair.expression.combination.binary;


import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.spirals.repair.expression.Expression;
import fr.inria.lille.spirals.repair.expression.ExpressionImpl;
import fr.inria.lille.spirals.repair.expression.access.Literal;
import fr.inria.lille.spirals.repair.expression.factory.AccessFactory;
import fr.inria.lille.spirals.repair.expression.factory.CombinationFactory;
import fr.inria.lille.spirals.repair.expression.value.Value;

/**
 * is the generic type of a binary expression
 */

public class BinaryExpressionImpl extends ExpressionImpl implements BinaryExpression {
    private BinaryOperator operator;
    private Expression first;
    private Expression second;
    private String strExpression = null;

    /**
     *
     */
    public BinaryExpressionImpl(BinaryOperator operator, Expression first, Expression second, Config config) {
        super(null, config);
        this.operator = operator;
        this.first = first;
        this.second = second;
        if (operator.isCommutative() && (first instanceof Literal || first.getValue().getRealValue() == null)) {
            this.first = second;
            this.second = first;
        }
        setValue(performExpression());
    }


    @Override
    public Value evaluate(Candidates values) {

        Value exp1Value = this.getFirstExpression().getValue();
        Value exp2Value = this.getSecondExpression().getValue();
        Value value = Value.NOVALUE;
        try {
            Value evaluate = this.getFirstExpression().evaluate(values);
            if (evaluate != Value.NOVALUE && evaluate != null) {
                this.getFirstExpression().setValue(evaluate);

                Value evaluate2 = this.getSecondExpression().evaluate(values);
                if (evaluate2 != null) {
                    this.getSecondExpression().setValue(evaluate2);
                    value = performExpression();
                    if (value == null && evaluate2 == Value.NOVALUE) {
                        value = Value.NOVALUE;
                    }
                }
            }
        } finally {
            this.getFirstExpression().setValue(exp1Value);
            this.getSecondExpression().setValue(exp2Value);
        }
        return value;
    }

    public void evaluate() {
        try {
            Value value = performExpression();
            setValue(value);
        } catch (ArithmeticException e) {
            // ignore
        }
    }

    boolean isExpressionMakeSense() {
        Value firstValue = getFirstExpression().getValue();
        if (firstValue == Value.NOVALUE) {
            return false;
        }

        Value secondValue = getSecondExpression().getValue();
        if (secondValue == Value.NOVALUE && getOperator() != BinaryOperator.OR) {
            return false;
        }

        Class param1 = getOperator().getParam1();
        Class param2 = getOperator().getParam2();

        // check the compatibility with the operator
        if (!param1.isAssignableFrom(firstValue.getType())) {
            return false;
        }

        if (secondValue != Value.NOVALUE && !param2.isAssignableFrom(secondValue.getType())) {
            return false;
        }
        if (secondValue != Value.NOVALUE && firstValue.isConstant() && secondValue.isConstant()) {
            return false;
        }

        switch (getOperator()) {
            case EQ:
            case NEQ:
                // the two expressions type
                if (secondValue.isPrimitive() != firstValue.isPrimitive()) {
                    return false;
                }

                if (getFirstExpression().sameExpression(getSecondExpression())) {
                    return false;
                }

                // comparison between null and a primitive
                if (firstValue.getRealValue() instanceof Number) {
                    if (!(secondValue.getRealValue() instanceof Number)) {
                        return false;
                    }
                } else if (secondValue.getRealValue() instanceof Number) {
                    if (!(firstValue.getRealValue() instanceof Number)) {
                        return false;
                    }
                } else if (firstValue.getRealValue() instanceof Boolean) {
                    if (!(secondValue.getRealValue() instanceof Boolean)) {
                        return false;
                    }
                } else if (secondValue.getRealValue() instanceof Boolean) {
                    if (!(firstValue.getRealValue() instanceof Boolean)) {
                        return false;
                    }
                }
                break;
            case AND:
            case OR:
                if (getFirstExpression().sameExpression(getSecondExpression())) {
                    return false;
                }
                if (firstValue.isConstant() || (secondValue != Value.NOVALUE && secondValue.isConstant())) {
                    return false;
                }
                break;
            case LESSEQ:
            case LESS:
                if (getFirstExpression().sameExpression(getSecondExpression())) {
                    return false;
                }
                break;
            case ADD:
                if ((firstValue.isConstant() && isValue(firstValue, 0))
                        || (secondValue.isConstant() && isValue(secondValue, 0))) {
                    return false;
                }
                break;
            case SUB:
                if (firstValue.isConstant() && isValue(firstValue, 0)) {
                    return false;
                }
                if (getFirstExpression().sameExpression(getSecondExpression())) {
                    return false;
                }
                break;
            case MULT:
                if ((firstValue.isConstant() && isValue(firstValue, 0))
                        || (secondValue.isConstant() && isValue(secondValue, 0))) {
                    return false;
                }
                if ((firstValue.isConstant() && isValue(firstValue, 1))
                        || (secondValue.isConstant() && isValue(secondValue, 1))) {
                    return false;
                }
            case DIV:
                if (isValue(secondValue, 0)) {
                    return false;
                }
                if ((firstValue.isConstant() && isValue(firstValue, 1))
                        || (secondValue.isConstant() && isValue(secondValue, 1))) {
                    return false;
                }
                if (getFirstExpression().sameExpression(getSecondExpression())) {
                    return false;
                }
                break;
        }
        return true;
    }

    private boolean isValue(Value v1, Number v2) {
        Value eval = new BinaryExpressionEvaluator(CombinationFactory.create(BinaryOperator.EQ, AccessFactory.literal(v1.getRealValue(), config), AccessFactory.literal(v2, config), config)).eval();
        if (eval == null) {
            return false;
        }
        return (boolean) eval.getRealValue();
    }


    Value performExpression() {
        if (!isExpressionMakeSense()) {
            return null;
        }
        return new BinaryExpressionEvaluator(this).eval();
    }

    public BinaryOperator getOperator() {
        return operator;
    }

    public Expression getFirstExpression() {
        return first;
    }

    public Expression getSecondExpression() {
        return second;
    }

    public void setFirst(Expression first) {
        this.first = first;
    }

    public void setSecond(Expression second) {
        this.second = second;
    }

    @Override
    public double getWeight() {
        double weight = 0;
        switch (getOperator()) {
            case AND:
                weight = this.config.getAndWeight();
                break;
            case OR:
                weight = this.config.getOrWeight();
                break;
            case EQ:
                weight = this.config.getEqWeight();
                break;
            case NEQ:
                weight = this.config.getnEqWeight();
                break;
            case LESS:
                weight = this.config.getLessWeight();
                break;
            case LESSEQ:
                weight = this.config.getLessEqWeight();
                break;
            case ADD:
                weight = this.config.getAddWeight();
                break;
            case SUB:
                weight = this.config.getSubWeight();
                break;
            case MULT:
                weight = this.config.getMulWeight();
                break;
            case DIV:
                weight = this.config.getDivWeight();
                break;
        }
        return weight * getPriority() * getFirstExpression().getWeight() * getSecondExpression().getWeight();
    }

    @Override
    public String toString() {
        if (strExpression == null) {
            strExpression = asPatch();
        }
        return strExpression;
    }

    @Override
    public String asPatch() {
        if (strExpression != null) {
            return strExpression;
        }
        StringBuilder sb = new StringBuilder();
        String first = getFirstExpression().asPatch().intern();
        if (getFirstExpression() instanceof BinaryExpression) {
            sb.append("(");
            sb.append(first);
            sb.append(")");
        } else {
            sb.append(first);
        }
        sb.append(" ");
        sb.append(getOperator().getSymbol());
        sb.append(" ");
        String second = getSecondExpression().asPatch().intern();
        if (getSecondExpression() instanceof BinaryExpression) {
            sb.append("(");
            sb.append(second);
            sb.append(")");
        } else {
            sb.append(second);
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return operator.hashCode() * first.hashCode() * second.hashCode();
    }

    @Override
    public int nbSubExpression() {
        return 2;
    }
}

