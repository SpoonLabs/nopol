package fr.inria.lille.spirals.repair.expression.operator;



public enum UnaryOperator {
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

    public String getSymbol() {
        return symbol;
    }

    public Class getReturnType() {
        return returnType;
    }

    public OperatorPosition getPosition() {
        return position;
    }

    public enum OperatorPosition {
        PRE, POST
    }
}

