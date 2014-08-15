package nopol_examples.nopol_example;

public class NopolExample {

	/*
	 * return true if a can be divided by 3
	 */
	
	public boolean canBeDividedby3(String a) {
		if ( a.charAt(0)== '0' )
			return false;
	
		/*
		 *  FIX : (((1)+(1))<((a.length())-((1)+(1))))||(((a.length())-((1)+(1)))<=(1))
		 */
		a = a.substring(1);
	
		if ( a.length() == 0 ){
			return false;
		}
	
		boolean result = (Integer.parseInt(a)%3 == 0);
		return result;
	}
}