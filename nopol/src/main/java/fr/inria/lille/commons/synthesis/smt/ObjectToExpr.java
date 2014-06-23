package fr.inria.lille.commons.synthesis.smt;

import java.math.BigDecimal;
import java.util.Map;

import org.smtlib.IExpr;
import org.smtlib.ISort;

import fr.inria.lille.commons.classes.ClassLibrary;
import fr.inria.lille.commons.collections.MapLibrary;

public class ObjectToExpr {

	public static ISort sortFor(Object object) {
		return sortFor(object.getClass());
	}
	
	public static ISort sortFor(Class<?> aClass) {
		if (conversions().containsKey(aClass)) {
			return conversions().get(aClass);
		}
		throw new UnsupportedOperationException("Could not get corresponding org.smtlib.ISort for " + aClass);		
	}
	
	public static IExpr asIExpr(Object object) {
		if (ClassLibrary.isInstanceOf(Boolean.class, object)) {
			return (Boolean) object ? smtlib().booleanTrue() : smtlib().booleanFalse();
		}
		if (ClassLibrary.isInstanceOf(Integer.class, object)) {
			return numeral((Integer) object);
		}
		if (ClassLibrary.isInstanceOf(Number.class, object)) {
			return decimal((Number) object);
		}
		throw new UnsupportedOperationException("Could not get corresponding org.smtlib.IExpr for " + object);
	}
	
	private static IExpr numeral(Integer integer) {
		String value = integer.toString();
		if (value.startsWith("-")) {
			value = value.substring(1);
			return negatedNumber(smtlib().numeral(value));
		}
		return smtlib().numeral(value);
	}
	
	private static IExpr decimal(Number number) {
		String value = number.toString();
		if (value.contains("E")) {
			value = BigDecimal.valueOf(number.doubleValue()).toPlainString();
		}
		if (value.startsWith("-")) {
			value = value.substring(1);
			return negatedNumber(smtlib().decimal(value));
		}
		return smtlib().decimal(value);
	}
	
	private static IExpr negatedNumber(IExpr numberExpression) {
		return smtlib().expression(smtlib().substraction(), numberExpression);
	}
	
	private static SMTLib smtlib() {
		if (smtlib == null) {
			smtlib = new SMTLib();
		}
		return smtlib;
	}
	
	private static Map<Class<?>, ISort> conversions() {
		if (conversions == null) {
			Map<Class<?>, ISort> classes = MapLibrary.newHashMap();
			classes.put(Long.class, smtlib().intSort());
			classes.put(Short.class, smtlib().intSort());
			classes.put(Integer.class, smtlib().intSort());
			classes.put(Float.class, smtlib().numberSort());
			classes.put(Double.class, smtlib().numberSort());
			classes.put(Number.class, smtlib().numberSort());
			classes.put(Boolean.class, smtlib().boolSort());
			conversions = classes;
		}
		return conversions;
	}
	
	private static SMTLib smtlib;
	private static Map<Class<?>, ISort> conversions;
}
