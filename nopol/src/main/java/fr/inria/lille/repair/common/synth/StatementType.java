package fr.inria.lille.repair.common.synth;

public enum StatementType {
    PRE_THEN_COND(Boolean.class),//this mode runs PRECONDITION mode, then if no patch has been found runs CONDITIONAL mode
    LOOP(Boolean.class),
    CONDITIONAL(Boolean.class),
    PRECONDITION(Boolean.class),
    INTEGER_LITERAL(Integer.class),
    DOUBLE_LITERAL(Double.class),
    BOOLEAN_LITERAL(Boolean.class),
    NONE(null);

    private Class<?> type;

    private StatementType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }
}
