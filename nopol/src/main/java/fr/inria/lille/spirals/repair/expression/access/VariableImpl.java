package fr.inria.lille.spirals.repair.expression.access;


import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.spirals.repair.expression.Expression;
import fr.inria.lille.spirals.repair.expression.ExpressionImpl;
import fr.inria.lille.spirals.repair.expression.value.Value;

/**
 * is the generic type of an unary expression
 */

public class VariableImpl extends ExpressionImpl implements Variable {
    private String variableName;
    private Expression target;

    /**
     *
     */
    public VariableImpl(String variableName, Expression target, Value value, Config config) {
        super(value, config);
        this.variableName = variableName;
        this.target = target;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }

    @Override
    public Expression getTarget() {
        return target;
    }

    @Override
    public double getWeight() {
        if (getValue().isConstant()) {
            return config.getConstantWeight() * getPriority();
        }
        return config.getVariableWeight() * getPriority();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getTarget() != null) {
            sb.append(getTarget().toString());
            sb.append(".");
        }
        sb.append(variableName);
        return sb.toString();
    }

    @Override
    public String asPatch() {
        return toString();
    }

    @Override
    public int hashCode() {
        return variableName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        VariableImpl variable = (VariableImpl) o;

        if (variableName != null ? !variableName.equals(variable.variableName) : variable.variableName != null)
            return false;

        return super.equals(o);
    }
}