package fr.inria.lille.spirals.repair.synthesizer.collect.factory;

import com.sun.jdi.*;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.PrimitiveValue;
import fr.inria.lille.spirals.repair.expression.*;
import fr.inria.lille.spirals.repair.vm.DebugJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
                if (value instanceof ArrayReference) {
                    ArrayReference array = (ArrayReference) value;
                    return create(variable.name(), array);
                }
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
    public static ArrayAccessExpression createArrayAccessExpression(int index, ArrayExpression arrayExpression, Value arrayValue) {
        if (arrayValue instanceof PrimitiveValue) {
            try {
                java.lang.reflect.Method valueMethod = arrayValue.getClass().getMethod("value");
                Object result = valueMethod.invoke(arrayValue);
                return new PrimitiveArrayAccessExpressionImpl(index, arrayExpression, result, result.getClass());
            } catch (Exception e) {
                return new ComplexArrayAccessExpressionImpl(index, arrayExpression, arrayValue, arrayValue, Object.class);
            }
        } else {
            return new ComplexArrayAccessExpressionImpl(index, arrayExpression, arrayValue, arrayValue, Object.class);
        }
    }

    public static ArrayExpression create(ArrayReference array) {
        List<ArrayAccessExpression> values = new ArrayList<>();
        ArrayExpression arrayExpression = new ArrayExpressionImpl(array.type().name(), values, array, array, Object[].class);

        List<Value> arrayValues = array.getValues();
        for (int i = 0; i < arrayValues.size(); i++) {
            Value arrayValue =  arrayValues.get(i);
            ArrayAccessExpression arrayAccessExpression = createArrayAccessExpression(i, arrayExpression, arrayValue);
            values.add(arrayAccessExpression);
        }
        return arrayExpression;
    }

    public static ArrayVariable create(String variableName, ArrayReference array) {
        List<ArrayAccessExpression> values = new ArrayList<>();
        ArrayVariable arrayExpression = new ArrayVariableImpl(variableName, array.type().name(), values, array, array, Object[].class);

        List<Value> arrayValues = array.getValues();
        for (int i = 0; i < arrayValues.size(); i++) {
            Value arrayValue =  arrayValues.get(i);
            ArrayAccessExpression arrayAccessExpression = createArrayAccessExpression(i, arrayExpression, arrayValue);
            values.add(arrayAccessExpression);
        }
        return arrayExpression;
    }

    public static Expression create(ComplexTypeExpression exp, Field field, Value value) {
        if (value instanceof ObjectReference) {
            if (field.isFinal() && field.isStatic()) {
                if (exp instanceof Constant) {
                    return new ComplexConstantImpl((Constant) exp, field.name(), value);
                }
                return null;
            } else {
                if (value instanceof ArrayReference) {
                    ArrayReference array = (ArrayReference) value;
                    return create(field.name(), array);
                }
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
        List<String> argumentTypes = new ArrayList<String>();
        try {
            List<Type> types = method.argumentTypes();
            for (Type type : types) {
                argumentTypes.add(type.name());
            }
        } catch (ClassNotLoadedException e) {
            e.printStackTrace();
        }
        if (value == null || value instanceof ObjectReference) {
            if (value instanceof ArrayReference) {
                ArrayReference array = (ArrayReference) value;
                //return create(array);
            }
            return new ComplexMethodInvocationImpl(method.name(), argumentTypes, cast, exp, parameters, value, Object.class);
        } else try {
            try {
                method.returnType();
            } catch (ClassNotLoadedException e1) {
                DebugJUnitRunner.loadClass(method.returnTypeName(), method.virtualMachine());
                method.returnType();
            }
            if (method.returnType() instanceof PrimitiveType) {
                if (method.returnType() instanceof BooleanType) {
                    return new PrimitiveMethodInvocationImpl(method.name(), argumentTypes, cast, exp, parameters, value, ((BooleanValue) value).value(), Boolean.class);
                }
                try {
                    java.lang.reflect.Method valueMethod = value.getClass().getMethod("value");
                    Object result = valueMethod.invoke(value);
                    return new PrimitiveMethodInvocationImpl(method.name(), argumentTypes, cast, exp, parameters, value, result, result.getClass());
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
