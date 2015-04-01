package fr.inria.lille.spirals.repair.expression;


import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.spirals.repair.expression.operator.Operator;

/**
 * is the generic type of a binary expression
 */

public abstract class BinaryExpressionImpl extends ExpressionImpl implements BinaryExpression {
    private Operator operator;
    private Expression first;
    private Expression second;

    /**
     *
     */
    public BinaryExpressionImpl(Operator operator, Expression first, Expression second, Object value, Class type) {
        super(value, type);
        this.operator = operator;
        this.first = first;
        this.second = second;
    }

    /**
     *
     */

    public Operator getOperator() {
        return operator;
    }

    /**
     *
     */

    public Expression getFirstExpression() {
        return first;
    }

    /**
     *
     */

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
    public int countInnerExpression() {
        return getFirstExpression().countInnerExpression() + getSecondExpression().countInnerExpression();
    }

    @Override
    public double getWeight() {
        double weight = 0;
        switch (getOperator()) {
            case AND:
                weight = Config.INSTANCE.getAndWeight();
                break;
            case OR:
                weight = Config.INSTANCE.getOrWeight();
                break;
            case EQ:
                weight = Config.INSTANCE.getEqWeight();
                break;
            case NEQ:
                weight = Config.INSTANCE.getnEqWeight();
                break;
            case LESS:
                weight = Config.INSTANCE.getLessWeight();
                break;
            case LESSEQ:
                weight = Config.INSTANCE.getLessEqWeight();
                break;
            case ADD:
                weight = Config.INSTANCE.getAddWeight();
                break;
            case SUB:
                weight = Config.INSTANCE.getSubWeight();
                break;
            case MULT:
                weight = Config.INSTANCE.getMulWeight();
                break;
            case DIV:
                weight = Config.INSTANCE.getDivWeight();
                break;
        }
        return weight * getPriority() * getFirstExpression().getWeight() * getSecondExpression().getWeight();
    }

    @Override
    public String toString() {
        String first = getFirstExpression().toString();
        if (getFirstExpression() instanceof BinaryExpression) {
            first = "(" + first + ")";
        }
        String second = getSecondExpression().toString();
        if (getSecondExpression() instanceof BinaryExpression) {
            second = "(" + second + ")";
        }
        return first + " " + getOperator().getSymbol() + " " + second;
    }

    @Override
    public String asPatch() {
        String first = getFirstExpression().asPatch();
        if (getFirstExpression() instanceof BinaryExpression) {
            first = "(" + first + ")";
        }
        String second = getSecondExpression().asPatch();
        if (getSecondExpression() instanceof BinaryExpression) {
            second = "(" + second + ")";
        }
        return first + " " + getOperator().getSymbol() + " " + second;
    }
}

