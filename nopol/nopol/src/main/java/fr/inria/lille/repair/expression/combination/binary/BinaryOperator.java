package fr.inria.lille.repair.expression.combination.binary;


import fr.inria.lille.repair.expression.combination.Operator;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public enum BinaryOperator implements Operator {
    AND(Boolean.class, "&&", Boolean.class, Boolean.class, false),
    OR(Boolean.class, "||", Boolean.class, Boolean.class, false),
    EQ(Boolean.class, "==", Object.class, Object.class, true),
    NEQ(Boolean.class, "!=", Object.class, Object.class, true),
    LESSEQ(Boolean.class, "<=", Number.class, Number.class, false),
    LESS(Boolean.class, "<", Number.class, Number.class, false),
    //GREATER(Boolean.class, ">", Number.class, Number.class, false),
    //GREATEREQ(Boolean.class, ">=", Number.class, Number.class, false),

    ADD(Number.class, "+", Number.class, Number.class, true),
    SUB(Number.class, "-", Number.class, Number.class, false),
    MULT(Number.class, "*", Number.class, Number.class, true),
    DIV(Number.class, "/", Number.class, Number.class, false);
    // Operator MOD(Number.class, "%", Number.class, Number.class);

    private final Class returnType;
    private final String symbol;
    private final Class param1;
    private final Class param2;
    private final boolean isCommutative;

    /**
     *
     */
    BinaryOperator(Class returnType, String symbol, Class param1, Class param2, boolean isCommutative) {
        this.returnType = returnType;
        this.symbol = symbol;
        this.param1 = param1;
        this.param2 = param2;
        this.isCommutative = isCommutative;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    public boolean isCommutative() {
        return this.isCommutative;
    }

    @Override
    public Class getReturnType() {
        return returnType;
    }

    @Override
    public List<Class> getTypeParameters() {
        return Arrays.asList(getParam1(), getParam2());
    }

    public Class getParam1() {
        return param1;
    }

    public Class getParam2() {
        return param2;
    }


}

