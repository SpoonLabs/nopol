package spoon.example;

import java.util.Collection;

public class ClassToSpoon {
	
	class NestedClassToSpoon {
		public String publicNestedInstanceField;
		private String privateNestedInstanceField;
		protected String protectedNestedInstanceField;
		
		public int process(NestedClassToSpoon comparable, ClassToSpoon nested) {
			if (nested != null) {
				return 1;
			}
			return 0;
		}
	}

	public void comparable(final ClassToSpoon nested) {
		new Comparable<NestedClassToSpoon>() {
			@Override
			public int compareTo(NestedClassToSpoon comparable) {
				if (comparable != null) {
					return process(comparable, nested);
				}
				return 0;
			}
			private String anonymousField = "doley";
		};
	}
	
	public int process(NestedClassToSpoon comparable, ClassToSpoon nested2) {
		if (nested2 != null) {
			if (comparable.publicNestedInstanceField == null) {
				return 1;
			}
			return 2;
		}
		return 3;
	}
	
	public String publicInstanceField;
	private String privateInstanceField;
	protected String protectedInstanceField;
	
	public static Collection<Boolean> publicStaticField;
	private static Collection<Integer> privateStaticField;
	protected static Collection<Number> protectedStaticField;
}