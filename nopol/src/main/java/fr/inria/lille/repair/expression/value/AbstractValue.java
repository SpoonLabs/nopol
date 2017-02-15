package fr.inria.lille.repair.expression.value;


import com.sun.jdi.*;

/**
 * is the generic type of an expression
 */
public abstract class AbstractValue implements Value {
    private double priority;

    private transient Object realValue;
    private boolean isPrimitive = false;
    private boolean isConstant = false;
    private transient com.sun.jdi.Value JDIValue;
    private Class type;

    public AbstractValue(Object realValue) {
        isPrimitive = realValue instanceof com.sun.jdi.PrimitiveValue;
        this.realValue = realValue;
        if (realValue != null) {
            type = realValue.getClass();
            isPrimitive = realValue.getClass().isPrimitive();
        } else {
            type = Object.class;
            setPrimitive(false);
        }
    }

    @Override
    public com.sun.jdi.Value getJDIValue() {
        return JDIValue;
    }

    @Override
    public void setJDIValue(com.sun.jdi.Value JDIValue) {
        this.JDIValue = JDIValue;
    }

    @Override
    public Object getRealValue() {
        return this.realValue;
    }

    public void setRealValue(Object realValue) {
        this.realValue = realValue;
    }

    public Class<?> getClass(String typeName) {
        if (typeName.equals("byte"))
            return Byte.class;
        if (typeName.equals("short"))
            return Short.class;
        if (typeName.equals("int"))
            return Integer.class;
        if (typeName.equals("long"))
            return Long.class;
        if (typeName.equals("char"))
            return Character.class;
        if (typeName.equals("float"))
            return Float.class;
        if (typeName.equals("double"))
            return Double.class;
        if (typeName.equals("boolean"))
            return Boolean.class;
        if (typeName.equals("void"))
            return void.class;
        try {
            return Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found : " + typeName);
        }
    }

    @Override
    public Class getType() {
        return this.type;
    }

    @Override
    public void setType(Class<?> aClass) {

    }

    @Override
    public boolean isConstant() {
        return isConstant;
    }

    @Override
    public void setConstant(boolean b) {
        this.isConstant = b;
    }

    @Override
    public boolean isPrimitive() {
        return isPrimitive;
    }

    public void setPrimitive(boolean primitive) {
        isPrimitive = primitive;
    }

    @Override
    public boolean isAssignableTo(Type refAss) {
        try {
            ReferenceType ref;

            if(isPrimitive() && !(refAss instanceof PrimitiveType)) {
                return false;
            }
            if(!isPrimitive() && (refAss instanceof PrimitiveType)) {
                return false;
            }

            if (getRealValue() == null) {
                // this should result in never passing null as method parameters
                return false;
            }
            // either it is a debug object
            else if (this.getRealValue() instanceof com.sun.jdi.Value) {
                // big hack
                // isAssignableTo is not in the API, so we have to set it accessible
                ref = ((ObjectReference) this.getRealValue()).referenceType();
                java.lang.reflect.Method isAssignableTo = null;
                try {
                    isAssignableTo = ref.getClass().getSuperclass().getSuperclass().getDeclaredMethod("isAssignableTo", ReferenceType.class);            // big hack            // big hack
                } catch (NoSuchMethodException e) {
                    isAssignableTo = ref.getClass().getDeclaredMethod("isAssignableTo", ReferenceType.class);            // big hack            // big hack
                }
                isAssignableTo.setAccessible(true);
                try {
                    return (boolean) isAssignableTo.invoke(ref, refAss);
                } catch (IllegalArgumentException e) {
                    // the two types are not compatible
                    return false;
                }
            }
            // or it is an actual value, a real object
            else {
                // classical isAssignableFrom
                // constants are never handled in isAssignableTo
                return getClass(refAss.name()).isAssignableFrom(getType());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        if (this.getRealValue() == null) {
            return "null";
        }
        if (this.getRealValue().getClass().isPrimitive()) {
            return this.getRealValue() + "";
        }
        return this.getRealValue().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractValue that = (AbstractValue) o;

        if (getRealValue() != null ? !getRealValue().equals(that.getRealValue()) : that.getRealValue() != null)
            return false;

        return getType() != null ? getType().equals(that.getType()) : that.getType() == null;

    }

    @Override
    public int hashCode() {
        int result = getRealValue() != null ? getRealValue().hashCode() : 0;
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        return result;
    }
}

