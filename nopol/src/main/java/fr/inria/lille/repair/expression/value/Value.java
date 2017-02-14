package fr.inria.lille.repair.expression.value;


import com.sun.jdi.Type;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;

public interface Value extends Serializable {

    Class getType();

    void setType(Class<?> aClass);

    com.sun.jdi.Value getJDIValue();

    void setJDIValue(com.sun.jdi.Value JDIValue);

    Object getRealValue();

    void setRealValue(Object value);

    boolean isPrimitive();

    boolean isConstant();

    boolean isAssignableTo(Type refAss);

    void setConstant(boolean b);

    Value NOVALUE = new Value() {
        @Override
        public Class getType() {
            throw new NotImplementedException();
        }

        @Override
        public void setType(Class<?> aClass) {
            throw new NotImplementedException();
        }

        @Override
        public com.sun.jdi.Value getJDIValue() {
            throw new NotImplementedException();
        }

        @Override
        public void setJDIValue(com.sun.jdi.Value JDIValue) {
            throw new NotImplementedException();
        }

        @Override
        public Object getRealValue() {
            throw new NotImplementedException();
        }

        @Override
        public void setRealValue(Object value) {
            throw new NotImplementedException();
        }

        @Override
        public boolean isPrimitive() {
            throw new NotImplementedException();
        }

        @Override
        public boolean isConstant() {
            throw new NotImplementedException();
        }

        @Override
        public boolean isAssignableTo(Type refAss) {
            throw new NotImplementedException();
        }

        @Override
        public void setConstant(boolean b) {
            throw new NotImplementedException();
        }
    };
}

