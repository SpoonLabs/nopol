package fr.inria.lille.repair.symbolic.synth;

public enum StatementType {
	CONDITIONAL(Boolean.class), PRECONDITION(Boolean.class), INTEGER_LITERAL(Integer.class), BOOLEAN_LITERAL(Boolean.class) , NONE(null);
	
	private Class<?> type;

	private StatementType(Class<?> type) {
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}
}
