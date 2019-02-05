package fr.inria.lille.commons.synthesis.operator;

import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.expression.ObjectTemplate;

public class Parameter<T> extends ObjectTemplate<T> {

    public static Parameter<Boolean> aBoolean() {
        return new Parameter<Boolean>(Boolean.class);
    }

    public static Parameter<Integer> anInteger() {
        return new Parameter<Integer>(Integer.class);
    }

    public static Parameter<Double> aDouble() {
        return new Parameter<Double>(Double.class);
    }

    public static Parameter<Number> aNumber() {
        return new Parameter<Number>(Number.class);
    }

    public static Parameter<String> aString() {
        return new Parameter<String>(String.class);
    }

    public static Parameter<Character> aCharacter() {
        return new Parameter<Character>(Character.class);
    }

    public static Parameter<Object> anObject() {
        return new Parameter<Object>(Object.class);
    }

    public Parameter(Class<T> aClass) {
        super(aClass);
    }

    public boolean admitsAsArgument(Expression<?> expression) {
        return typeIsSuperClassOf(expression.type());
    }

    public boolean admitsAsArgument(Object object) {
        return typeIsSuperClassOf(object.getClass());
    }
}
