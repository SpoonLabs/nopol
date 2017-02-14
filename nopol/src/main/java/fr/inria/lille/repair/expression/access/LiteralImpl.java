package fr.inria.lille.repair.expression.access;


import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.repair.expression.ExpressionImpl;
import fr.inria.lille.repair.expression.value.Value;

public class LiteralImpl extends ExpressionImpl implements Literal {

    public LiteralImpl(Value value, Config config) {
        super(value, config);
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
        return config.getConstantWeight() * getPriority();
    }

    @Override
    public String asPatch() {
        return this.toString();
    }
}