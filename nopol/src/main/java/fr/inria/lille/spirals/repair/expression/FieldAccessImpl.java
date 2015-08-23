package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;
import fr.inria.lille.repair.common.config.Config;

/**
 *
 *
 *
 */
public abstract class FieldAccessImpl extends ExpressionImpl implements FieldAccess {
    private final ComplexTypeExpression expression;
    private final String fieldName;
    private final Value jdiValue;
    private String strExpression = null;

    /**
     *
     */
    public FieldAccessImpl(String fieldName, ComplexTypeExpression expression, Value jdiValue, Object value, Class returnType) {
        super(value, returnType);
        this.fieldName = fieldName;
        this.expression = expression;
        this.jdiValue = jdiValue;
    }

    /**
     *
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     *
     */

    public ComplexTypeExpression getExpression() {
        return this.expression;
    }

    public Value getJdiValue() {
        return jdiValue;
    }

    @Override
    public int countInnerExpression() {
        return getExpression().countInnerExpression();
    }

    @Override
    public double getWeight() {
        return Config.INSTANCE.getFieldAccessWeight() * getPriority() + getExpression().getWeight();
    }

    @Override
    public String toString() {
        if(strExpression == null) {
            strExpression = this.getExpression().toString().intern() + "." + fieldName;
        }
        return strExpression;
    }

    @Override
    public String asPatch() {
        return this.getExpression().asPatch().intern() + "." + fieldName;
    }
}

