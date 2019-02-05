package spoon.example;

import java.util.Collection;

public class ClassToSpoon {
	
	private class NestedClassToSpoon {
		public String publicNestedInstanceField;
		private String privateNestedInstanceField;
		protected String protectedNestedInstanceField;
		
		public int process(NestedClassToSpoon comparable, ClassToSpoon nested) {
			if (nested != null) {
				Object a = comparable.privateNestedInstanceField;
				Object b = comparable.protectedNestedInstanceField;
				Object c = comparable.publicNestedInstanceField;
				Object d = nested.privateInstanceField;
				Object e = nested.publicInstanceField;
				Object f = nested.protectedInstanceField;
				Object g = ClassToSpoon.privateStaticField;
				Object h = ClassToSpoon.protectedStaticField;
				Object i = ClassToSpoon.publicStaticField;
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
					Object a = comparable.privateNestedInstanceField;
					Object b = comparable.protectedNestedInstanceField;
					Object c = comparable.publicNestedInstanceField;
					Object d = nested.privateInstanceField;
					Object e = nested.publicInstanceField;
					Object f = nested.protectedInstanceField;
					Object z = this.anonymousField;
					Object g = ClassToSpoon.privateStaticField;
					Object h = ClassToSpoon.protectedStaticField;
					Object i = ClassToSpoon.publicStaticField;
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
				Object a = comparable.privateNestedInstanceField;
				Object b = comparable.protectedNestedInstanceField;
				Object c = comparable.publicNestedInstanceField;
				Object d = nested2.privateInstanceField;
				Object e = nested2.publicInstanceField;
				Object f = nested2.protectedInstanceField;
				Object g = ClassToSpoon.privateStaticField;
				Object h = ClassToSpoon.protectedStaticField;
				Object i = ClassToSpoon.publicStaticField;
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