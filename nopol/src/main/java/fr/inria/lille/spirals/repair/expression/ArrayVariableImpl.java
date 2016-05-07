package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;

import java.util.List;


/**
 * is the generic type of a binary expression
 */

public class ArrayVariableImpl extends ArrayExpressionImpl implements ArrayVariable {
    private String variableName;

    /**
     *
     */
    public ArrayVariableImpl(String variableName, String cType, List<ArrayAccessExpression> values, Value jdiValue, Object value, Class type) {
        super(cType, values, jdiValue, value, type);
        this.variableName = variableName;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }
}

