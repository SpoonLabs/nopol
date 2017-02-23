package fr.inria.lille.repair.expression.access;


import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.ExpressionImpl;
import fr.inria.lille.repair.expression.value.Value;

import java.util.List;

public class MethodImpl extends ExpressionImpl implements Method {
    private final String method;
    private final String declaringType;
    private List<String> argumentTypes;
    private final Expression target;
    private final List<Expression> parameters;
    private String strExpression = null;

    public MethodImpl(String method, List<String> argumentTypes, String declaringType, Expression target, List<Expression> parameters, Value value, NopolContext nopolContext) {
        super(value, nopolContext);
        this.method = method;
        this.argumentTypes = argumentTypes;
        this.target = target;
        this.parameters = parameters;
        this.declaringType = declaringType;
    }

    public String getMethod() {
        return method;
    }

    public Expression getTarget() {
        return target;
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    @Override
    public double getWeight() {
        return nopolContext.getMethodCallWeight() * getPriority() * getTarget().getWeight() / (getParameters().size() == 0 ? 1 : getParameters().size());
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
            strExpression = getTarget().toString() + "." + getMethod() + "(" + arguments + ")";
        }
        return strExpression;
    }

    @Override
    public String asPatch() {
        if (strExpression != null) {
            return strExpression;
        }
        StringBuilder sb = new StringBuilder();
        if (declaringType != null) {
            sb.append("((");
            sb.append(declaringType);
            sb.append(") ");
        }
        sb.append(getTarget().asPatch());
        if (declaringType != null) {
            sb.append(")");
        }
        sb.append(".");
        sb.append(getMethod());
        sb.append("(");
        int size = parameters.size();
        for (int i = 0; i < size; i++) {
            Expression expression1 = parameters.get(i);
            sb.append("(");
            sb.append(argumentTypes.get(i));
            sb.append(") ");
            sb.append(expression1.asPatch());
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        strExpression = sb.toString();
        return strExpression;
    }

    @Override
    public int hashCode() {
        return method.hashCode() * this.declaringType.hashCode() * parameters.hashCode();
    }
}

