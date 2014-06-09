package fr.inria.lille.nopol.synth;

public final class GlobalBooleanVariable {

	public static final String accessName() {
		return GlobalBooleanVariable.class.getName() + '.' + "value";
	}

	public static volatile boolean value = true;

	public static void flip() {
		value = ! value;
	}

	private GlobalBooleanVariable() {}
}
