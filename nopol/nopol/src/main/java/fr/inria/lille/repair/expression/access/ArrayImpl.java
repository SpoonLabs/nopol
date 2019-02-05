package fr.inria.lille.repair.expression.access;


import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.ExpressionImpl;
import fr.inria.lille.repair.expression.value.Value;

public class ArrayImpl extends ExpressionImpl implements Array {

    private Expression target;
    private Expression index;

    public ArrayImpl(Expression target, Expression index, Value value, NopolContext nopolContext) {
        super(value, nopolContext);
        this.target = target;
        this.index = index;
    }

    @Override
    public Expression getIndex() {
        return index;
    }

    @Override
    public Expression getTarget() {
        return target;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTarget().toString());
        sb.append("[");
        sb.append(index);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public double getWeight() {
        return nopolContext.getVariableWeight() * getPriority();
    }

    @Override
    public String asPatch() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTarget().asPatch());
        sb.append("[");
        sb.append(index);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }
}