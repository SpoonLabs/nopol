package nopol_examples.nopol_example_5;

public class NopolExample {

	public static class InnerStaticClass {
		public void method(String stringParameter) {
			if (! stringParameter.isEmpty()) {
				System.out.println(stringParameter);
			}
		}
	}
	
	/*
	 * Return -a if a isn't negative number, otherwise return a if it's already negative number
	 */
	public int negate(int a){
		int r = 1;		
		
		// FIX : precondition missing : if ( -1<a  )
		r = -1;				
		return r*a;		
	}
	
	private Integer unreachableFromInnterStaticClass;
}
