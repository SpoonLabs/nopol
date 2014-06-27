package nopol_example_7;

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
	
}
