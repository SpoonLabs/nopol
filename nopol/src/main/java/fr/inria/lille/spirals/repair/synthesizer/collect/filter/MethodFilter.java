package fr.inria.lille.spirals.repair.synthesizer.collect.filter;

import com.sun.jdi.*;

/**
 * Created by Thomas Durieux on 06/03/15.
 */
public class MethodFilter {
    public static boolean toProcess(Method method) {
        return toProcess(method, null);
    }

    public static boolean toProcess(Method method, Class type) {
        if (method.isConstructor()) {
            return false;
        }
        if (method.returnTypeName().equals("void")) {
            return false;
        }
        if (method.returnTypeName().contains("Iterator")) {
            return false;
        }
        if (method.name().equals("length")) {
            return true;
        }
        if (method.declaringType().name().startsWith("java.lang.")) {
            return false;
        }
        if (method.name().equals("clone")) {
            return false;
        }
        if (method.name().contains("hash")) {
            return false;
        }
        if (method.name().equals("getClass")) {
            return false;
        }
        if (method.name().equals("toString")) {
            return false;
        }
        if (method.name().startsWith("set")) {
            return false;
        }
        try {
            if (type != null) {
                Type returnType = method.returnType();
                if (returnType instanceof BooleanType) {
                    return type.isAssignableFrom(Boolean.class);
                }
                if (returnType instanceof PrimitiveType && !type.isAssignableFrom(Number.class)) {
                    return false;
                }
                if (returnType instanceof ReferenceType && !type.getCanonicalName().equals(method.returnType().name())) {
                    return false;
                }
            }
        } catch (ClassNotLoadedException e) {
            // ignore
        }
        return true;
    }
}
