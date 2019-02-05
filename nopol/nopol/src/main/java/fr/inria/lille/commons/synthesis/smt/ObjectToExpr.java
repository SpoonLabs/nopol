package fr.inria.lille.commons.synthesis.smt;

import org.smtlib.IExpr;
import org.smtlib.ISort;
import xxl.java.container.classic.MetaMap;
import xxl.java.library.StringLibrary;

import java.util.Map;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.*;
import static xxl.java.library.ClassLibrary.isInstanceOf;

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
        if (isInstanceOf(Character.class, object)) {
            return asIExpr((int) (char) object);
        }
        if (isInstanceOf(Boolean.class, object)) {
            return (Boolean) object ? booleanTrue() : booleanFalse();
        }
        if (isInstanceOf(Integer.class, object)) {
            return numeral((Integer) object);
        }
        if (isInstanceOf(Number.class, object)) {
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
        String value = StringLibrary.plainDecimalRepresentation(number);
        if (value.startsWith("-")) {
            value = value.substring(1);
            return negatedNumber(smtlib().decimal(value));
        }
        return smtlib().decimal(value);
    }

    private static IExpr negatedNumber(IExpr numberExpression) {
        return smtlib().expression(subtraction(), numberExpression);
    }

    private static SMTLib smtlib() {
        if (smtlib == null) {
            smtlib = new SMTLib();
        }
        return smtlib;
    }

    private static Map<Class<?>, ISort> conversions() {
        if (conversions == null) {
            Map<Class<?>, ISort> classes = MetaMap.newHashMap();
            classes.put(Long.class, intSort());
            classes.put(Short.class, intSort());
            classes.put(Integer.class, intSort());
            classes.put(Character.class, intSort());
            classes.put(Float.class, numberSort());
            classes.put(Double.class, numberSort());
            classes.put(Number.class, numberSort());
            classes.put(Boolean.class, boolSort());
            conversions = classes;
        }
        return conversions;
    }

    private static SMTLib smtlib;
    private static Map<Class<?>, ISort> conversions;
}
