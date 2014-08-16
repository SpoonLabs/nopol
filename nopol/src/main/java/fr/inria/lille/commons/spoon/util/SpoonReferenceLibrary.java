package fr.inria.lille.commons.spoon.util;

import static xxl.java.extensions.library.ClassLibrary.isInstanceOf;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

public class SpoonReferenceLibrary {

	public static boolean isTypeReference(CtReference element) {
		return isInstanceOf(CtTypeReference.class, element);
	}
	
	public static boolean isVoidType(CtReference element) {
		if (isTypeReference(element)) {
			CtTypeReference<?> type = (CtTypeReference) element;
			return type.getSimpleName().equalsIgnoreCase("void");
		}
		return false;
	}
}
