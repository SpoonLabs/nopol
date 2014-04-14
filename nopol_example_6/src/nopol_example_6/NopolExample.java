package nopol_example_6;

public class NopolExample {

	
	/*
	 * return true if a can be divided by 3
	 */
	public boolean canBeDividedby3(int a){
		if ( a == 0 )
			return false;
		String s = String.valueOf(a);
		/* 
		 *	FIX : !(((s.length())+(-1))<=(1))&&((0)<((s.length())+(-1)))
		 */
		s = s.substring(1);
		if ( s.length() == 0 ){
			return false;
		}
		boolean result = (Integer.parseInt(s)%3 == 0);
		return result;
	}
}
