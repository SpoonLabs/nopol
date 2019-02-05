package fr.inria.lille.repair.expression.access;


import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.expression.ExpressionImpl;
import fr.inria.lille.repair.expression.value.Value;

public class LiteralImpl extends ExpressionImpl implements Literal {

    public LiteralImpl(Value value, NopolContext nopolContext) {
        super(value, nopolContext);
        value.setConstant(true);
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public double getWeight() {
        return nopolContext.getConstantWeight() * getPriority();
    }

    @Override
    public String asPatch() {
        return this.toString();
    }
}