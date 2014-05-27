package fr.inria.lille.commons.spoon;

import java.util.Collection;

import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.support.reflect.code.CtWhileImpl;
import spoon.support.reflect.declaration.CtSimpleTypeImpl;

public class InstanceOfClassFilter<T extends CtElement> extends AbstractFilter<CtElement> {

	public static Collection<CtWhile> whileStatementsIn(Factory factory) {
		return (Collection) getElements(CtWhileImpl.class, factory);
	}
	
	public static Collection<CtSimpleType> classDefinitionsIn(Factory factory) {
		return (Collection) getElements(CtSimpleTypeImpl.class, factory);
	}
	
	public static <T extends CtElement> Collection<T> getElements(Class<T> aClass, Factory factory) {
		return Query.getElements(factory, new InstanceOfClassFilter(aClass));
	}
	
	public InstanceOfClassFilter(Class<CtElement> aClass) {
		super(aClass);
		matchingClass = aClass;
	}
	
	public Class<? extends CtElement> matchingClass() {
		return matchingClass;
	}
	
	@Override
	public boolean matches(CtElement element) {
		return matchingClass().isInstance(element);
	}

	private Class<CtElement> matchingClass;
}
