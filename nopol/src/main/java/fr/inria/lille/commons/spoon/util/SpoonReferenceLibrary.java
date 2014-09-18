package fr.inria.lille.commons.spoon.util;

import static xxl.java.library.ClassLibrary.isInstanceOf;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

public class SpoonReferenceLibrary {

	public static boolean isTypeReference(CtReference element) {
		return isInstanceOf(CtTypeReference.class, element);
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
	
	public static boolean haveSameClass(CtTypeReference<?> superType, CtTypeReference<?> type) {
		return superType.getQualifiedName().equals(type.getQualifiedName());
	}
	
	public static boolean isSubclassOf(CtTypeReference<?> superType, CtTypeReference<?> type) {
		return superType.isAssignableFrom(type);
	}
	
	public static boolean isNestedIn(CtTypeReference<?> nestingType, CtTypeReference<?> type) {
		String nestingTypeQualifiedName = nestingType.getQualifiedName();
		String qualifiedName = type.getQualifiedName();
		return qualifiedName.contains(nestingTypeQualifiedName);
	}
}
