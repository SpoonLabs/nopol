package spoon.example;

import java.util.Collection;

public class ClassToSpoon {
	
	static class NestedClassToSpoon {
		public String publicNestedInstanceField;
		private String privateNestedInstanceField;
		protected String protectedNestedInstanceField;
	}
	
	public static void process(NestedClassToSpoon nested) {
		if (nested.publicNestedInstanceField == null) {
			nested.publicNestedInstanceField = "public";
			nested.privateNestedInstanceField = "private";
			nested.protectedNestedInstanceField = "protected";
		}
	}
	
	public String publicInstanceField;
	private String privateInstanceField;
	protected String protectedInstanceField;
	
	public static Collection<Boolean> publicStaticField;
	private static Collection<Integer> privateStaticField;
	protected static Collection<Number> protectedStaticField;
}