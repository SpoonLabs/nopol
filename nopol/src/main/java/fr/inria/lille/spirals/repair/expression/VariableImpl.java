package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;
import fr.inria.lille.repair.common.config.Config;

/**
 * is the generic type of an unary expression
 */

public abstract class VariableImpl extends ExpressionImpl implements Variable {
    private String variableName;
    private final Value jdiValue;

    /**
     *
     */
    public VariableImpl(String variableName, Value jdiValue, Object value, Class type) {
        super(value, type);
        this.variableName = variableName;
        this.jdiValue = jdiValue;
    }

    @Override
    public String toString() {
        return variableName;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }

    @Override
    public Value getJdiValue() {
        return jdiValue;
    }

    @Override
    public int countInnerExpression() {
        return 1;
    }

    @Override
    public double getWeight() {
        return Config.INSTANCE.getVariableWeight() * getPriority();
    }

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        VariableImpl variable = (VariableImpl) o;

        if (variableName != null ? !variableName.equals(variable.variableName) : variable.variableName != null)
            return false;

        return super.equals(o);
    }*/
}