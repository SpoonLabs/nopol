package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.Value;
import fr.inria.lille.repair.common.config.Config;

import java.util.List;

public abstract class MethodInvocationImpl extends ExpressionImpl implements MethodInvocation {
    private final String method;
    private final String cast;
    private List<String> argumentTypes;
    private final Expression expression;
    private final List<Expression> parameters;
    private final Value jdiValue;
    private String strExpression = null;

    public MethodInvocationImpl(String method, List<String> argumentTypes, String declaringType, Expression expression, List<Expression> parameters, Value jdiValue, Object value, Class type) {
        super(value, type);
        this.method = method;
        this.argumentTypes = argumentTypes;
        this.expression = expression;
        this.parameters = parameters;
        this.jdiValue = jdiValue;
        this.cast = declaringType;
    }

    public String getMethod() {
        return method;
    }

    public Expression getExpression() {
        return expression;
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    @Override
    public double getWeight() {
        return Config.INSTANCE.getMethodCallWeight() * getPriority() * getExpression().getWeight() / (getParameters().size() == 0 ? 1 : getParameters().size());
    }

    @Override
    public int countInnerExpression() {
        return getExpression().countInnerExpression();
    }

    @Override
    public Value getJdiValue() {
        return jdiValue;
    }

    @Override
    public String toString() {
        if (strExpression == null) {
            String arguments = "";
            for (int i = 0; i < parameters.size(); i++) {
                Expression expression1 = parameters.get(i);
                arguments += "(" + argumentTypes.get(i) + ") ";
                arguments += expression1.toString();
                if (i < parameters.size() - 1) {
                    arguments += ", ";
                }
            }
            strExpression = getExpression().toString() + "." + getMethod() + "(" + arguments + ")";
        }
        return strExpression;
    }

    @Override
    public String asPatch() {
        String arguments = "";
        for (int i = 0; i < parameters.size(); i++) {
            Expression expression1 = parameters.get(i);
            arguments += "(" + argumentTypes.get(i) + ") ";
            arguments += expression1.asPatch();
            if (i < parameters.size() - 1) {
                arguments += ", ";
            }
        }
        if (cast == null) {
            return getExpression().asPatch() + "." + getMethod() + "(" + arguments + ")";
        }
        return "((" + cast + ")" + getExpression().asPatch() + ")." + getMethod() + "(" + arguments + ")";
    }

    @Override
    public int hashCode() {
        return method.hashCode() * this.cast.hashCode() * parameters.hashCode();
    }
}

