package fr.inria.lille.repair.synthesis.collect.filter;

import com.sun.jdi.*;
import fr.inria.lille.repair.vm.DebugJUnitRunner;

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
        if (method.name().equals("clone") || method.name().contains("copy")) {
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
        if (method.name().equals("replace")) {
            return false;
        }
        if (method.name().startsWith("set")) {
            return false;
        }
        if (method.name().startsWith("to")) {
            return false;
        }
        if (method.name().startsWith("add")) {
            return false;
        }
        if (method.name().startsWith("append")) {
            return false;
        }
        if (method.name().contains("remove") || method.name().contains("delete")) {
            return false;
        }
        try {
            if (type != null) {
                try {
                    method.returnType();
                } catch (ClassNotLoadedException e1) {
                    DebugJUnitRunner.loadClass(method.returnTypeName(), method.virtualMachine());
                    method.returnType();
                }
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
