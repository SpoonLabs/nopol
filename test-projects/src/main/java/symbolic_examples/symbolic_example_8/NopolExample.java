package symbolic_examples.symbolic_example_8;

public class NopolExample {

	/*
	 * Return true if (a * b) <= 100
	 * 
	 */
	public boolean productLowerThan100(double a, double b){
		
		// if ( a * b <= 100) // FIX
		if ( a * b < 100 )
			return true;
		
		return false;
	}

	public boolean subconditionCollection(double a, double b) {
		if (( a * b < 11 || productLowerThan100(a, b) || ! (a < b)) || (a = -b) > 0) {
			return false;
		}
		return true;
	}
}
