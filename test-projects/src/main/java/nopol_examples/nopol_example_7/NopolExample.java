package nopol_examples.nopol_example_7;

public class NopolExample {

	/*
	 * Return true if a is Prime Number
	 */
	public boolean isPrime(int a){
		
		if ( a < 0 ){
			return false;
		}
		
		/*
		 * Nopol doesn't find patch after the nullness check modification
		 */
		
		int intermediaire = a%2;

		// FIX if ( intermediaire == 0 && a!=2)
		if ( intermediaire == 0 )
			return false;
		
		int sqrtMiddle = (int)(Math.sqrt(a)/2);
		for ( int i = 3 ; i <= sqrtMiddle ; i+=2 ){
			int tmp = a%i;
			if ( tmp == 0 ){
				return false;
			}
		}
		return true;
	}
	
	
}
