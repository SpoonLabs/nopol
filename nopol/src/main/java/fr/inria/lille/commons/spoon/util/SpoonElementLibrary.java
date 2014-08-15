package fr.inria.lille.commons.spoon.util;

import static fr.inria.lille.commons.utils.library.ClassLibrary.castTo;
import static fr.inria.lille.commons.utils.library.ClassLibrary.isInstanceOf;

import java.util.List;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

public class SpoonElementLibrary {

	public static <T extends CtElement> List<T> allChildrenOf(CtElement rootElement, Class<T> childrenClasses) {
		return Query.getElements(rootElement, new TypeFilter<T>(childrenClasses));
	}
	
	public static <T extends CtElement> List<T> filteredElements(Factory factory, Filter<T> filter) {
		return Query.getElements(factory, filter);
	}
	
	public static boolean isBlock(CtElement element) {
		return isInstanceOf(CtBlock.class, element);
	}
	
	public static boolean isMethod(CtElement element) {
		return isInstanceOf(CtMethod.class, element);
	}
	
	public static boolean isLocalVariable(CtElement element) {
		return isInstanceOf(CtLocalVariable.class, element);
	}
	
	public static boolean isAnonymousClass(CtElement element) {
		return isInstanceOf(CtNewClass.class, element);
	}
	
	public static boolean isConstructor(CtElement element) {
		return isInstanceOf(CtConstructor.class, element);
	}
	
	public static boolean isInitializationBlock(CtElement element) {
		return isInstanceOf(CtAnonymousExecutable.class, element);
	}
	
	public static boolean isAType(CtElement element) {
		return isInstanceOf(CtSimpleType.class, element);
	}
	
	public static boolean isField(CtElement element) {
		return isInstanceOf(CtField.class, element);
	}
	
	public static boolean isStatement(CtElement element) {
		return isInstanceOf(CtStatement.class, element);
	}
	
	public static boolean allowsModifiers(CtElement element) {
		return isInstanceOf(CtModifiable.class, element);
	}
	
	public static boolean hasStaticModifier(CtElement element) {
		if (allowsModifiers(element)) {
			return castTo(CtModifiable.class, element).getModifiers().contains(ModifierKind.STATIC);
		}
		return false;
	}
	
	public static boolean inStaticCode(CtElement element) {
		if (allowsModifiers(element)) {
			return hasStaticModifier(element);
		}
		return hasStaticModifier(element.getParent(CtModifiable.class)) || hasStaticModifier(element.getParent(CtSimpleType.class));
	}
}
