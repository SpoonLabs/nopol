package fr.inria.lille.commons.spoon.util;

import org.slf4j.Logger;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import xxl.java.container.classic.MetaList;

import java.util.Collection;

import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.*;
import static fr.inria.lille.commons.spoon.util.SpoonMethodLibrary.methodsOf;
import static xxl.java.library.ClassLibrary.isInstanceOf;
import static xxl.java.library.LoggerLibrary.logWarning;
import static xxl.java.library.LoggerLibrary.loggerFor;

public class SpoonReferenceLibrary {

    public static boolean isTypeReference(CtReference element) {
        return isInstanceOf(CtTypeReference.class, element);
    }

    public static Collection<CtMethod<?>> accessibleMethodsFrom(CtTypeReference<?> accessingType, CtTypeReference<?> accessedType) {
        Collection<CtMethod<?>> accessibleMethods = MetaList.newLinkedList();
        try {
            Collection<CtMethod<?>> allMethods = methodsOf(accessedType);
            for (CtMethod<?> method : allMethods) {
                if (isVisibleFrom(accessingType, method, method.getDeclaringType().getReference(), accessedType)) {
                    accessibleMethods.add(method);
                }
            }
        } catch (Throwable e) {
            logWarning(logger(), e.toString());
        }
        return accessibleMethods;
    }

    public static Collection<CtField<?>> accessibleFieldsFrom(CtTypeReference<?> accessingType, CtTypeReference<?> accessedType) {
        Collection<CtField<?>> accessibleFields = MetaList.newLinkedList();
        try {
            Collection<CtFieldReference<?>> allFields = accessedType.getAllFields();
            for (CtFieldReference<?> field : allFields) {
                CtField<?> actualField = field.getDeclaration();
                if (actualField != null && isVisibleFrom(accessingType, actualField, field.getDeclaringType(), accessedType)) {
                    accessibleFields.add(actualField);
                }
            }
        } catch (Throwable e) {
            logWarning(logger(), e.toString());
        }
        return accessibleFields;
    }

    private static boolean isVisibleFrom(CtTypeReference<?> accessingClass, CtModifiable modifiable, CtTypeReference<?> declaringClass, CtTypeReference<?> actualClass) {
        if (hasPublicModifier(modifiable)) {
            return true;
        }
        if ((isNestedIn(accessingClass, actualClass) || isNestedIn(actualClass, accessingClass)) && areSameClass(declaringClass, actualClass)) {
            return true;
        }
        if (hasNoVisibilityModifier(modifiable) && areFromSamePackage(declaringClass, actualClass) && areFromSamePackage(actualClass, accessingClass)) {
            return true;
        }
        if (hasPrivateModifier(modifiable) && areSameClass(declaringClass, accessingClass)) {
            return true;
        }
        if (hasProtectedModifier(modifiable) && areFromSamePackage(declaringClass, accessingClass)) {
            return true;
        }
        if (hasProtectedModifier(modifiable) && isSubclassOf(declaringClass, accessingClass) && areSameClass(actualClass, accessingClass)) {
            return true;
        }
        return false;
    }

    public static boolean isVoidType(CtReference element) {
        if (isTypeReference(element)) {
            CtTypeReference<?> type = (CtTypeReference<?>) element;
            return type.getSimpleName().equalsIgnoreCase("void");
        }
        return false;
    }

    public static Class<?> referencedTypeOf(CtTypeReference<?> reference) {
        return reference.box().getActualClass();
    }

    public static boolean areSameClass(CtTypeReference<?> type, CtTypeReference<?> otherType) {
        return type.getQualifiedName().equals(otherType.getQualifiedName());
    }

    public static String packageOf(CtTypeReference<?> type) {
        CtPackageReference thePackage = type.getPackage();
        if (thePackage == null) {
            thePackage = type.getDeclaringType().getPackage();
        }
        return thePackage.getSimpleName();
    }

    public static boolean areFromSamePackage(CtTypeReference<?> type, CtTypeReference<?> otherType) {
        return packageOf(type).equals(packageOf(otherType));
    }

    public static boolean isSubclassOf(CtTypeReference<?> superType, CtTypeReference<?> type) {
        return superType.isAssignableFrom(type);
    }

    public static boolean isNestedIn(CtTypeReference<?> nestingType, CtTypeReference<?> type) {
        String simpleName = nestingType.getSimpleName();
        String qualifiedName = type.getQualifiedName();
        return qualifiedName.contains(simpleName + '$') && areFromSamePackage(nestingType, type);
    }

    private static Logger logger() {
        return loggerFor(SpoonReferenceLibrary.class);
    }
}
