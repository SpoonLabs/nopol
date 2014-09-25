package fr.inria.lille.repair.nopol.synth;

public final class AngelicExecution {

	public static String invocation(String booleanSnippet) {
		return AngelicExecution.class.getName() + ".angelicValue(" + booleanSnippet + ")";
	}
	
	public static boolean angelicValue(boolean condition) {
		if (enabled()) {
			return booleanValue();
		}
		return condition;
	}
	
	public static boolean booleanValue() {
		return booleanValue;
	}
	
	public static void flip() {
		booleanValue = ! booleanValue;
	}
	
	public static void enable() {
		enabled = true;
	}
	
	public static void disable() {
		enabled = false;
	}
	
	private static boolean enabled() {
		return enabled;
	}

	private AngelicExecution() {}
	
	private static boolean enabled = false;
	private static boolean booleanValue = true;
}
