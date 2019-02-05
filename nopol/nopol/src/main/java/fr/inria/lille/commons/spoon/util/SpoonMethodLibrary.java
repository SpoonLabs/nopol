package fr.inria.lille.commons.spoon.util;

import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collection;

import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.*;
import static java.util.Arrays.asList;

public class SpoonMethodLibrary {

    public static boolean isGetter(CtMethod<?> method) {
        if (hasNoArguments(method) && numberOfStatements(method) == 1) {
            CtStatement statement = lastStatementOf(method);
            return isReturnStatement(statement) && isFieldAccess(((CtReturn<?>) statement).getReturnedExpression());
        }
        return false;
    }

    public static boolean isAbstract(CtMethod<?> method) {
        return hasModifier(method, ModifierKind.ABSTRACT);
    }

    public static boolean isInterfaceMethod(CtMethod<?> method) {
        return isInterface(method.getParent());
    }

    public static boolean hasNoArguments(CtMethod<?> method) {
        return method.getParameters().isEmpty();
    }

    public static boolean hasArguments(CtMethod<?> method) {
        return !hasNoArguments(method);
    }

    public static boolean hasBody(CtMethod<?> method) {
        return method.getBody() != null;
    }

    public static int numberOfStatements(CtMethod<?> method) {
        if (!hasBody(method)) {
            return 0;
        }
        return method.getBody().getStatements().size();
    }

    public static CtStatement statementOf(CtMethod<?> method, int statementNumber) {
        if (numberOfStatements(method) < statementNumber) {
            method.getBody().getStatement(statementNumber);
        }
        return null;
    }

    public static CtStatement lastStatementOf(CtMethod<?> method) {
        return method.getBody().getLastStatement();
    }

    public static Collection<CtMethod<?>> methodsOf(CtTypeReference<?> type) {
        CtType<?> declaration = type.getDeclaration();
        if (isType(declaration)) {
            return ((CtType<?>) declaration).getAllMethods();
        }
        return asList();
    }

}
