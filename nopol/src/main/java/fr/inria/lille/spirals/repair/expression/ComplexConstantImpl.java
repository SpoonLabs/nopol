package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.ReferenceType;
import com.sun.jdi.Value;
import fr.inria.lille.repair.common.config.Config;

/**
 * is the generic type of a primitive value
 */

public class ComplexConstantImpl extends ComplexValueImpl implements ComplexConstant {
    /**
     * @param value
     */
    public ComplexConstantImpl(String name, ReferenceType value) {
        super(name, value);
    }

    public ComplexConstantImpl(Constant exp, String name, Value value) {
        super(exp.toString() + "." + name, value);
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

