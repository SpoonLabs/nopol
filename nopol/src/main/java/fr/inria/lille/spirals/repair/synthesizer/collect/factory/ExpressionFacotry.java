package fr.inria.lille.spirals.repair.synthesizer.collect.factory;

import com.sun.jdi.*;
import com.sun.jdi.BooleanValue;
import fr.inria.lille.spirals.repair.expression.*;
import fr.inria.lille.spirals.repair.vm.DebugJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by Thomas Durieux on 06/03/15.
 */
public class ExpressionFacotry {

    public static Variable create(LocalVariable variable, Value value) {
        try {
            try {
                variable.type();
            } catch (ClassNotLoadedException e1) {
                DebugJUnitRunner.loadClass(variable.typeName(), variable.virtualMachine());
                variable.type();
            }
            if (variable.type() instanceof ReferenceType) {
                return new ComplexValueImpl(variable.name(), value);
            } else if (variable.type() instanceof PrimitiveType) {
                if (value.type() instanceof BooleanType) {
                    if (value == null) {
                        return new BooleanValueImpl(variable.name(), value, null);
                    }
                    return new BooleanValueImpl(variable.name(), value, ((BooleanValue) value).value());
                }
                if (value == null) {
                    return new NumericalValueImpl(variable.name(), value, null, Number.class);
                }
                try {
                    java.lang.reflect.Method valueMethod = value.getClass().getMethod("value");
                    Object result = valueMethod.invoke(value);
                    return new NumericalValueImpl(variable.name(), value, result, result.getClass());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (ClassNotLoadedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Expression create(ComplexTypeExpression exp, Field field, Value value) {
        if (value instanceof ObjectReference) {
            if (field.isFinal() && field.isStatic()) {
                if (exp instanceof Constant) {
                    return new ComplexConstantImpl((Constant) exp, field.name(), value);
                }
                return null;
            } else {
                return new ComplexFieldAccessImpl(field.name(), exp, value, Object.class);
            }
        } else try {
            try {
                field.type();
            } catch (ClassNotLoadedException e1) {
                DebugJUnitRunner.loadClass(field.typeName(), field.virtualMachine());
                field.type();
            }
            if (value == null || field.type() instanceof PrimitiveType) {
                if (field.type() instanceof BooleanType) {
                    if (value == null) {
                        return new PrimitiveFieldAccessImpl(field.name(), exp, value, null, Boolean.class);
                    }
                    if (field.isFinal() && field.isStatic()) {
                        if (exp instanceof Constant) {
                            return new PrimitiveConstantImpl((Constant) exp, field.name(), ((BooleanValue) value).value(), Boolean.class);
                        }
                        return null;
                    }
                    return new PrimitiveFieldAccessImpl(field.name(), exp, value, ((BooleanValue) value).value(), Boolean.class);
                }
                if (value == null) {
                    return new PrimitiveFieldAccessImpl(field.name(), exp, value, null, Number.class);
                }
                try {
                    java.lang.reflect.Method valueMethod = value.getClass().getMethod("value");
                    Object result = valueMethod.invoke(value);
                    if (field.isFinal() && field.isStatic()) {
                        if (exp instanceof Constant) {
                            return new PrimitiveConstantImpl((Constant) exp, field.name(), result, result.getClass());
                        }
                        return null;
                    }
                    return new PrimitiveFieldAccessImpl(field.name(), exp, value, result, result.getClass());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (ClassNotLoadedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MethodInvocation create(ComplexTypeExpression exp, Method method, List<Expression> parameters, Value value, boolean isTocast) {
        String[] castSplit = method.declaringType().name().split("\\$");
        String cast = castSplit[0];
        if (!isTocast) {
            cast = null;
        }
        if (value == null || value instanceof ObjectReference) {
            return new ComplexMethodInvocationImpl(method.name(), cast, exp, parameters, value, Object.class);
        } else try {
            try {
                method.returnType();
            } catch (ClassNotLoadedException e1) {
                DebugJUnitRunner.loadClass(method.returnTypeName(), method.virtualMachine());
                method.returnType();
            }
            if (method.returnType() instanceof PrimitiveType) {
                if (method.returnType() instanceof BooleanType) {
                    return new PrimitiveMethodInvocationImpl(method.name(), cast, exp, parameters, value, ((BooleanValue) value).value(), Boolean.class);
                }
                try {
                    java.lang.reflect.Method valueMethod = value.getClass().getMethod("value");
                    Object result = valueMethod.invoke(value);
                    return new PrimitiveMethodInvocationImpl(method.name(), cast, exp, parameters, value, result, result.getClass());
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } catch (ClassNotLoadedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Expression create(ComplexTypeExpression exp, Method method, List<Expression> expressions, Value result) {
        return create(exp, method, expressions, result, true);
    }
}
