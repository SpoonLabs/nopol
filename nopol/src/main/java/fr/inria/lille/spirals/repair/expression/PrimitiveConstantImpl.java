package fr.inria.lille.spirals.repair.expression;


import fr.inria.lille.repair.common.config.Config;

/**
 * is the generic type of a primitive value
 */

public class PrimitiveConstantImpl extends VariableImpl implements PrimitiveConstant {
    /**
     * @param value
     * @param type
     */
    public PrimitiveConstantImpl(Object value, Class<?> type) {
        super(value + "", null, value, type);
    }

    public PrimitiveConstantImpl(Constant exp, String name, Object value, Class<?> type) {
        super(exp.toString() + "." + name, null, value, type);
    }

    @Override
    public double getWeight() {
        return Config.INSTANCE.getConstantWeight() * getPriority();
    }

    @Override
    public String toString() {
        return this.getVariableName();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Constant)) {
            return false;
        }
        return super.equals(o);
    }
}

