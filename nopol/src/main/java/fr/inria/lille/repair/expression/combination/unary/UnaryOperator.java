package fr.inria.lille.repair.expression.combination.unary;


import fr.inria.lille.repair.expression.combination.Operator;

import java.util.ArrayList;
import java.util.List;

public enum UnaryOperator implements Operator{
    INV(Boolean.class, "!", OperatorPosition.PRE);

    /*PREINC(Number.class, "++", OperatorPosition.PRE),
    POSTINC(Number.class, "++", OperatorPosition.POST),
    PREDEC(Number.class, "--", OperatorPosition.PRE),
    POSTDEC(Number.class, "--", OperatorPosition.POST)*/

    private final Class returnType;
    private final String symbol;
    private final OperatorPosition position;

    /**
     *
     */
    UnaryOperator(Class returnType, String symbol, OperatorPosition position) {
        this.returnType = returnType;
        this.symbol = symbol;
        this.position = position;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public Class getReturnType() {
        return returnType;
    }

    @Override
    public List<Class> getTypeParameters() {
        List<Class> output = new ArrayList<>();
        output.add(boolean.class);
        return output;
    }
    public OperatorPosition getPosition() {
        return position;
    }

    public enum OperatorPosition {
        PRE, POST
    }
}

